package io.github.vakho10.springjavafxboot.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
@Component
public class FxRouteRegistry {

    private static final Logger log = LoggerFactory.getLogger(FxRouteRegistry.class);

    private final ApplicationContext applicationContext;
    private final Map<String, HandlerMethod> routes = new LinkedHashMap<>();

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
                routes.put(path, new HandlerMethod(path, parent, routeBean, method));
            }
        }

        // Second pass: validate parent references
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
     * Look up a handler for the given path.
     *
     * @param path the route path (e.g. "/main")
     * @return the handler method, or {@code null} if no route matches
     */
    public HandlerMethod resolve(String path) {
        return routes.get(normalizePath(path));
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

        for (Class<?> paramType : method.getParameterTypes()) {
            if (!FxModel.class.equals(paramType) && !Map.class.isAssignableFrom(paramType)) {
                throw new IllegalStateException(
                        "@FxMapping method %s.%s() has unsupported parameter type: %s. "
                                .formatted(routeClass.getSimpleName(), method.getName(), paramType.getSimpleName())
                                + "Supported: FxModel, Map<String, Object>");
            }
        }
    }
}