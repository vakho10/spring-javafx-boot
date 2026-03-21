package io.github.vakho10.springjavafxboot.router;

import io.github.vakho10.springjavafxboot.navigation.ViewResolver;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * The central JavaFX routing service — analogous to Spring MVC's {@code DispatcherServlet},
 * with Angular-style nested child routing.
 * <p>
 * Supports unlimited nesting depth. Parent routes define layout templates with
 * a {@link RouterOutlet} (or {@code fx:id="routerOutlet"}) where child views render.
 * When navigating to a child route, the router builds the full ancestor chain,
 * reuses any already-active parent layouts, and only reloads what changed.
 *
 * <h3>Route hierarchy example:</h3>
 * <pre>
 *   "/"           → layout.fxml       (menu bar + outlet)
 *   "/main"       → main.fxml         (child of /)
 *   "/settings"   → settings.fxml     (child of /)
 * </pre>
 *
 * <h3>Navigation:</h3>
 * <pre>{@code
 * router.navigateTo("/main");       // loads "/" layout, then "/main" inside its outlet
 * router.navigateTo("/settings");   // reuses "/" layout, swaps "/settings" into outlet
 * }</pre>
 *
 * @see FxMapping
 * @see FxRoutes
 * @see RouterOutlet
 * @see FxRouteRegistry
 */
@Service
public class FxRouter {

    private static final Logger log = LoggerFactory.getLogger(FxRouter.class);

    /**
     * Convention-based fx:id for router outlets in FXML.
     */
    private static final String OUTLET_FX_ID = "routerOutlet";

    private final FxRouteRegistry routeRegistry;
    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;

    private BorderPane rootPane;
    private String currentPath;
    private Map<String, Object> currentParams = Map.of();

    /**
     * Cache of currently active routes, keyed by path.
     */
    private final Map<String, ActiveRoute> activeRoutes = new LinkedHashMap<>();

    public FxRouter(FxRouteRegistry routeRegistry,
                    ViewResolver viewResolver,
                    ApplicationContext applicationContext) {
        this.routeRegistry = routeRegistry;
        this.viewResolver = viewResolver;
        this.applicationContext = applicationContext;
    }

    /**
     * Sets the root pane whose center region will be swapped on navigation.
     * Must be called before any navigation (typically in {@code Application.start()}).
     */
    public void setRootPane(BorderPane rootPane) {
        this.rootPane = rootPane;
    }

    /**
     * Navigate to a route with no parameters.
     */
    public void navigateTo(String path) {
        navigateTo(path, Map.of());
    }

    /**
     * Navigate to a route with parameters.
     * <p>
     * For child routes, the router automatically resolves the full ancestor chain,
     * reuses active parent layouts, and only loads/reloads what is necessary.
     *
     * @param path   the route path (e.g. "/main")
     * @param params key-value pairs to pre-populate in the model
     * @throws RoutingException if the route is not found or navigation fails
     */
    public void navigateTo(String path, Map<String, Object> params) {
        log.debug("Navigating to: {} with params: {}", path, params.keySet());

        HandlerMethod handler = routeRegistry.resolve(path);
        if (handler == null) {
            throw new RoutingException("No @FxMapping found for path: \"" + path + "\"");
        }

        Runnable navigation = () -> {
            requireRootPane();
            executeNavigation(handler, params);
            currentPath = path;
            currentParams = params;
        };

        if (Platform.isFxApplicationThread()) {
            navigation.run();
        } else {
            Platform.runLater(navigation);
        }
    }

    /**
     * Returns the currently active route path, or {@code null} before first navigation.
     */
    public String getCurrentPath() {
        return currentPath;
    }

    /**
     * Re-navigates to the current route with the same parameters.
     * Forces a full reload including all parent layouts.
     * Useful after a locale change.
     */
    public void reload() {
        if (currentPath == null) {
            throw new RoutingException("Cannot reload — no current route.");
        }
        // Clear active routes to force full rebuild
        activeRoutes.clear();
        navigateTo(currentPath, currentParams);
    }

    // -------------------------------------------------------------------------
    // Core navigation engine
    // -------------------------------------------------------------------------

    private void executeNavigation(HandlerMethod handler, Map<String, Object> params) {
        // Build the full ancestor chain: [root, ..., parent, target]
        List<HandlerMethod> chain = buildRouteChain(handler);

        // Walk the chain from root to target
        Pane currentOutlet = rootPane;

        for (int i = 0; i < chain.size(); i++) {
            HandlerMethod current = chain.get(i);
            boolean isTarget = (i == chain.size() - 1);

            // Check if this level is already active and can be reused
            ActiveRoute active = activeRoutes.get(current.path());
            if (active != null && !isTarget) {
                // Parent is already loaded — reuse it, skip to next level
                log.debug("Reusing active parent: \"{}\"", current.path());
                if (active.outlet() != null) {
                    currentOutlet = active.outlet();
                }
                continue;
            }

            // Invoke the handler
            FxModel model = new FxModel();
            if (isTarget) {
                params.forEach(model::put);
            }
            String viewName = invokeHandler(current, model);
            log.debug("Handler for \"{}\" returned view: \"{}\"", current.path(), viewName);

            // Load FXML + inject model attributes
            ViewResolver.ViewResult result = loadAndPrepareView(viewName, model);
            Parent view = result.view();
            Object controller = result.controller();

            // Find the outlet in this view for child routes
            Pane outlet = null;
            if (!isTarget || hasChildRoutes(current.path())) {
                outlet = findOutlet(controller, view);
            }

            // Invalidate any active routes at this level and below
            invalidateFrom(current.path());

            // Place the view into the current outlet
            placeView(currentOutlet, view);

            // Cache as active
            activeRoutes.put(current.path(), new ActiveRoute(current.path(), view, controller, outlet));
            log.info("Navigated to \"{}\" → view \"{}\"", current.path(), viewName);

            // Move to the next level's outlet
            if (outlet != null) {
                currentOutlet = outlet;
            }
        }
    }

    /**
     * Builds the route chain from root ancestor to the target handler.
     * For a route "/settings" with parent "/", this returns ["/", "/settings"].
     * For unlimited nesting: ["/", "/admin", "/admin/users"].
     */
    private List<HandlerMethod> buildRouteChain(HandlerMethod target) {
        LinkedList<HandlerMethod> chain = new LinkedList<>();
        HandlerMethod current = target;

        // Walk up the parent chain
        Set<String> visited = new HashSet<>();
        while (current != null) {
            if (!visited.add(current.path())) {
                throw new RoutingException("Circular parent reference detected at: \"" + current.path() + "\"");
            }
            chain.addFirst(current);
            if (current.hasParent()) {
                HandlerMethod parent = routeRegistry.resolve(current.parent());
                if (parent == null) {
                    throw new RoutingException(
                            "Parent route \"%s\" not found for child \"%s\""
                                    .formatted(current.parent(), current.path()));
                }
                current = parent;
            } else {
                current = null;
            }
        }

        return chain;
    }

    /**
     * Checks if any registered route declares the given path as its parent.
     */
    private boolean hasChildRoutes(String path) {
        String normalized = routeRegistry.normalizePath(path);
        return routeRegistry.getRoutes().values().stream()
                .anyMatch(h -> normalized.equals(h.parent()));
    }

    /**
     * Finds the router outlet in a loaded view. Tries two strategies:
     * <ol>
     *   <li>{@link RouterOutlet @RouterOutlet} annotation on a controller field</li>
     *   <li>{@code fx:id="routerOutlet"} convention in the FXML node tree</li>
     * </ol>
     */
    private Pane findOutlet(Object controller, Parent view) {
        // Strategy 1: @RouterOutlet annotation on controller field
        if (controller != null) {
            for (Field field : controller.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(RouterOutlet.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(controller);
                        if (value instanceof Pane pane) {
                            log.debug("Found @RouterOutlet on {}.{}",
                                    controller.getClass().getSimpleName(), field.getName());
                            return pane;
                        } else {
                            throw new RoutingException(
                                    "@RouterOutlet field %s.%s must be a Pane, but is %s".formatted(
                                            controller.getClass().getSimpleName(),
                                            field.getName(),
                                            value != null ? value.getClass().getSimpleName() : "null"));
                        }
                    } catch (IllegalAccessException e) {
                        throw new RoutingException(
                                "Cannot access @RouterOutlet field " + field.getName(), e);
                    }
                }
            }
        }

        // Strategy 2: fx:id="routerOutlet" convention
        Node node = view.lookup("#" + OUTLET_FX_ID);
        if (node instanceof Pane pane) {
            log.debug("Found outlet via fx:id=\"{}\"", OUTLET_FX_ID);
            return pane;
        }

        return null;
    }

    /**
     * Places a child view into an outlet pane.
     */
    private void placeView(Pane outlet, Parent view) {
        if (outlet instanceof BorderPane borderPane) {
            borderPane.setCenter(view);
        } else {
            outlet.getChildren().setAll(view);
        }
    }

    /**
     * Invalidates (removes) active routes at or below a given path.
     * Called when a route at that level is being reloaded.
     */
    private void invalidateFrom(String path) {
        activeRoutes.entrySet().removeIf(entry -> {
            String activePath = entry.getKey();
            if (activePath.equals(path)) {
                return true;
            }
            // Also remove any route that is a descendant of this path
            HandlerMethod handler = routeRegistry.resolve(activePath);
            if (handler != null && handler.hasParent()) {
                return isDescendantOf(activePath, path);
            }
            return false;
        });
    }

    /**
     * Checks whether a route is a descendant (child, grandchild, etc.) of an ancestor path.
     */
    private boolean isDescendantOf(String path, String ancestorPath) {
        HandlerMethod handler = routeRegistry.resolve(path);
        Set<String> visited = new HashSet<>();
        while (handler != null && handler.hasParent()) {
            if (!visited.add(handler.path())) {
                return false; // circular reference guard
            }
            if (handler.parent().equals(ancestorPath)) {
                return true;
            }
            handler = routeRegistry.resolve(handler.parent());
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Handler invocation & view loading (unchanged from v2)
    // -------------------------------------------------------------------------

    private String invokeHandler(HandlerMethod handler, FxModel model) {
        try {
            Method method = handler.method();
            Object[] args = resolveHandlerArguments(method, model);
            Object result = method.invoke(handler.bean(), args);

            if (result == null) {
                throw new RoutingException(
                        "@FxMapping method %s.%s() returned null — expected a view name.".formatted(
                                handler.routeConfigClass().getSimpleName(), method.getName()));
            }
            return (String) result;

        } catch (RoutingException e) {
            throw e;
        } catch (Exception e) {
            throw new RoutingException(
                    "Failed to invoke handler %s.%s()".formatted(
                            handler.routeConfigClass().getSimpleName(),
                            handler.method().getName()),
                    e);
        }
    }

    private Object[] resolveHandlerArguments(Method method, FxModel model) {
        Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Class<?> type = params[i].getType();
            if (FxModel.class.equals(type)) {
                args[i] = model;
            } else if (Map.class.isAssignableFrom(type)) {
                args[i] = model.asMap();
            } else {
                throw new RoutingException(
                        "Cannot resolve parameter of type %s in %s".formatted(
                                type.getSimpleName(), method.getName()));
            }
        }
        return args;
    }

    private ViewResolver.ViewResult loadAndPrepareView(String viewName, FxModel model) {
        try {
            ViewResolver.ViewResult result = viewResolver.loadView(
                    viewName, applicationContext::getBean);

            Object controller = result.controller();
            if (controller != null) {
                injectModelAttributes(controller, model);
                invokePostModelInit(controller, model);
            }

            return result;

        } catch (RoutingException e) {
            throw e;
        } catch (Exception e) {
            throw new RoutingException(
                    "Failed to load view \"%s\"".formatted(viewName), e);
        }
    }

    private void injectModelAttributes(Object controller, FxModel model) {
        Map<String, Object> attributes = model.asMap();
        if (attributes.isEmpty()) {
            return;
        }

        for (Field field : controller.getClass().getDeclaredFields()) {
            ModelAttribute annotation = field.getAnnotation(ModelAttribute.class);
            if (annotation == null) {
                continue;
            }

            String key = annotation.value().isEmpty() ? field.getName() : annotation.value();

            if (attributes.containsKey(key)) {
                try {
                    field.setAccessible(true);
                    field.set(controller, attributes.get(key));
                    log.trace("Injected @ModelAttribute \"{}\" → {}.{}",
                            key, controller.getClass().getSimpleName(), field.getName());
                } catch (IllegalAccessException e) {
                    throw new RoutingException(
                            "Failed to inject @ModelAttribute \"%s\" into %s.%s".formatted(
                                    key, controller.getClass().getSimpleName(), field.getName()),
                            e);
                }
            }
        }
    }

    private void invokePostModelInit(Object controller, FxModel model) {
        try {
            Method onModelReady = controller.getClass().getDeclaredMethod("onModelReady", FxModel.class);
            onModelReady.setAccessible(true);
            onModelReady.invoke(controller, model);
            log.trace("Called onModelReady() on {}", controller.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            // Optional hook — no-op if absent
        } catch (Exception e) {
            throw new RoutingException(
                    "Failed to call onModelReady() on %s".formatted(
                            controller.getClass().getSimpleName()),
                    e);
        }
    }

    private void requireRootPane() {
        if (rootPane == null) {
            throw new RoutingException(
                    "Root pane not set. Call router.setRootPane(rootPane) "
                            + "in Application.start() before navigating.");
        }
    }
}