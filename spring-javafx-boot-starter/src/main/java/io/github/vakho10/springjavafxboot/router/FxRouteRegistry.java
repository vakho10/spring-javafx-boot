package io.github.vakho10.springjavafxboot.router;

import io.github.vakho10.springjavafxboot.annotation.FxMapping;
import io.github.vakho10.springjavafxboot.annotation.FxRoutes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans the Spring {@link ApplicationContext} at startup for beans annotated
 * with {@link FxRoutes @FxRoutes} and registers methods annotated with
 * {@link FxMapping @FxMapping} into a route table.
 * <p>
 * Supports parent–child route relationships. Routes are logged at startup:
 * <pre>
 *   Mapped "/"       → AppRoutes.layout()
 *   Mapped "/main"   → AppRoutes.main()     [parent: /]
 *   Mapped "/second" → AppRoutes.second()   [parent: /]
 *   Registered 3 FxMapping route(s)
 * </pre>
 *
 * @see FxRoutes
 * @see FxMapping
 * @see FxRouter
 */
@Slf4j
public class FxRouteRegistry {

    /** Matches {@code {name}} placeholders in route paths. */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^/}]+)}");

    private final ApplicationContext applicationContext;
    private final Map<String, HandlerMethod> routes = new LinkedHashMap<>();
    private final List<ParameterizedRoute> parameterizedRoutes = new ArrayList<>();

    public FxRouteRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    void scanAndRegister() {
        Map<String, Object> routeBeans = applicationContext.getBeansWithAnnotation(FxRoutes.class);

        // First pass: collect all routes
        for (Object routeBean : routeBeans.values()) {
            Class<?> routeClass = routeBean.getClass();

            for (Method method : routeClass.getDeclaredMethods()) {
                FxMapping mapping = method.getAnnotation(FxMapping.class);
                if (mapping == null) {
                    continue;
                }

                String path = normalizePath(mapping.value());
                String parent = mapping.parent().isEmpty() ? "" : normalizePath(mapping.parent());

                validateHandlerMethod(method, path, routeClass);
                checkDuplicate(path, routeClass, method);

                method.setAccessible(true);
                String title = mapping.title();
                routes.put(path, new HandlerMethod(path, parent, title, routeBean, method));
            }
        }

        // Second pass: build regex patterns for parameterized routes
        for (HandlerMethod handler : routes.values()) {
            if (isParameterized(handler.path())) {
                parameterizedRoutes.add(buildParameterizedRoute(handler));
            }
        }

        // Third pass: validate parent references
        for (HandlerMethod handler : routes.values()) {
            if (handler.hasParent() && !routes.containsKey(handler.parent())) {
                throw new IllegalStateException(
                        "@FxMapping(\"%s\") references parent \"%s\" which does not exist. "
                                .formatted(handler.path(), handler.parent())
                                + "Available routes: " + routes.keySet());
            }
        }

        // Log registered routes
        for (HandlerMethod handler : routes.values()) {
            if (handler.hasParent()) {
                log.info("Mapped \"{}\" → {}.{}()  [parent: {}]",
                        handler.path(),
                        handler.routeConfigClass().getSimpleName(),
                        handler.method().getName(),
                        handler.parent());
            } else {
                log.info("Mapped \"{}\" → {}.{}()",
                        handler.path(),
                        handler.routeConfigClass().getSimpleName(),
                        handler.method().getName());
            }
        }

        if (routes.isEmpty()) {
            log.warn("No @FxMapping routes found. "
                    + "Create a class annotated with @FxRoutes containing @FxMapping methods.");
        } else {
            log.info("Registered {} FxMapping route(s)", routes.size());
        }
    }

    /**
     * Look up a handler for the given path (exact match only).
     * <p>
     * For parameterized routes, use {@link #resolveRoute(String)} which also
     * extracts path variables.
     *
     * @param path the route path (e.g. "/main") or template (e.g. "/users/{id}")
     * @return the handler method, or {@code null} if no route matches
     */
    public HandlerMethod resolve(String path) {
        return routes.get(normalizePath(path));
    }

    /**
     * Resolves a concrete navigation path against both exact and parameterized routes.
     * <p>
     * Exact matches take priority. If no exact match is found, parameterized routes
     * are checked in registration order.
     *
     * @param path the concrete path (e.g. "/users/42")
     * @return the resolved route with extracted path variables, or {@code null}
     */
    public ResolvedRoute resolveRoute(String path) {
        String normalized = normalizePath(path);

        // 1. Exact match (highest priority)
        HandlerMethod exact = routes.get(normalized);
        if (exact != null) {
            return new ResolvedRoute(exact, Map.of());
        }

        // 2. Parameterized match
        for (ParameterizedRoute pr : parameterizedRoutes) {
            Matcher matcher = pr.pattern.matcher(normalized);
            if (matcher.matches()) {
                Map<String, String> vars = new LinkedHashMap<>();
                for (int i = 0; i < pr.variableNames.size(); i++) {
                    vars.put(pr.variableNames.get(i), matcher.group(i + 1));
                }
                return new ResolvedRoute(pr.handler, Collections.unmodifiableMap(vars));
            }
        }

        return null;
    }

    /**
     * Returns an unmodifiable view of all registered routes.
     */
    public Map<String, HandlerMethod> getRoutes() {
        return Collections.unmodifiableMap(routes);
    }

    String normalizePath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void checkDuplicate(String path, Class<?> routeClass, Method method) {
        if (routes.containsKey(path)) {
            HandlerMethod existing = routes.get(path);
            throw new IllegalStateException(
                    "Duplicate @FxMapping for path \"%s\": %s.%s() conflicts with %s.%s()".formatted(
                            path,
                            existing.routeConfigClass().getSimpleName(), existing.method().getName(),
                            routeClass.getSimpleName(), method.getName()));
        }
    }

    private void validateHandlerMethod(Method method, String path, Class<?> routeClass) {
        if (!String.class.equals(method.getReturnType())) {
            throw new IllegalStateException(
                    "@FxMapping method %s.%s() must return String (view name), but returns %s".formatted(
                            routeClass.getSimpleName(), method.getName(),
                            method.getReturnType().getSimpleName()));
        }

        for (var param : method.getParameters()) {
            Class<?> paramType = param.getType();
            if (!FxModel.class.equals(paramType)
                    && !Map.class.isAssignableFrom(paramType)
                    && !param.isAnnotationPresent(
                            io.github.vakho10.springjavafxboot.annotation.PathVariable.class)) {
                throw new IllegalStateException(
                        "@FxMapping method %s.%s() has unsupported parameter type: %s. "
                                .formatted(routeClass.getSimpleName(), method.getName(), paramType.getSimpleName())
                                + "Supported: FxModel, Map<String, Object>, @PathVariable");
            }
        }
    }

    // =========================================================================
    // Parameterized route support
    // =========================================================================

    /**
     * Whether a route path contains {@code {variable}} placeholders.
     */
    boolean isParameterized(String path) {
        return path.contains("{") && path.contains("}");
    }

    private ParameterizedRoute buildParameterizedRoute(HandlerMethod handler) {
        String path = handler.path();
        List<String> variableNames = new ArrayList<>();
        Matcher m = PLACEHOLDER_PATTERN.matcher(path);

        StringBuilder regex = new StringBuilder("^");
        int lastEnd = 0;
        while (m.find()) {
            // Escape the literal segment before the placeholder
            regex.append(Pattern.quote(path.substring(lastEnd, m.start())));
            // Capture group for the variable value (any non-slash characters)
            regex.append("([^/]+)");
            variableNames.add(m.group(1));
            lastEnd = m.end();
        }
        // Append any trailing literal segment
        regex.append(Pattern.quote(path.substring(lastEnd)));
        regex.append("$");

        return new ParameterizedRoute(handler, Pattern.compile(regex.toString()), variableNames);
    }

    /**
     * A compiled parameterized route with its regex pattern and variable names.
     */
    private record ParameterizedRoute(
            HandlerMethod handler,
            Pattern pattern,
            List<String> variableNames
    ) {
    }
}