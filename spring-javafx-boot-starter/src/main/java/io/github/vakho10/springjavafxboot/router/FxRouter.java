package io.github.vakho10.springjavafxboot.router;

import io.github.vakho10.springjavafxboot.navigation.ViewResolver;
import io.github.vakho10.springjavafxboot.service.FxTitleService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The central JavaFX routing service — analogous to Spring MVC's {@code DispatcherServlet},
 * with Angular-style nested child routing and support for opening routes in separate windows.
 *
 * <h3>In-place navigation (child routing):</h3>
 * <pre>{@code
 * router.navigateTo("/main");
 * router.navigateTo("/settings", Map.of("tab", "appearance"));
 * }</pre>
 *
 * <h3>New window (modeless):</h3>
 * <pre>{@code
 * router.openWindow("/about");
 * router.openWindow("/details", Map.of("id", 42), new WindowOptions()
 *     .title("Details")
 *     .size(600, 400));
 * }</pre>
 *
 * <h3>Modal dialog (blocks parent until closed):</h3>
 * <pre>{@code
 * WindowResult<String> result = router.openModal("/picker");
 * result.get().ifPresent(value -> label.setText(value));
 *
 * // With params and options:
 * WindowResult<Item> result = router.openModal("/picker",
 *     Map.of("items", list),
 *     new WindowOptions().title("Select item").size(400, 300));
 * }</pre>
 *
 * @see FxMapping
 * @see FxRoutes
 * @see RouterOutlet
 * @see WindowOptions
 * @see WindowResult
 */
public class FxRouter {

    private static final Logger log = LoggerFactory.getLogger(FxRouter.class);

    /** Convention-based fx:id for router outlets in FXML. */
    private static final String OUTLET_FX_ID = "routerOutlet";

    private final FxRouteRegistry routeRegistry;
    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;
    private final FxTitleService titleService;

    private BorderPane rootPane;
    private String currentPath;
    private Map<String, Object> currentParams = Map.of();

    /** Cache of currently active routes, keyed by path. */
    private final Map<String, ActiveRoute> activeRoutes = new LinkedHashMap<>();

    public FxRouter(FxRouteRegistry routeRegistry,
                    ViewResolver viewResolver,
                    ApplicationContext applicationContext,
                    FxTitleService titleService) {
        this.routeRegistry = routeRegistry;
        this.viewResolver = viewResolver;
        this.applicationContext = applicationContext;
        this.titleService = titleService;
    }

    /**
     * Sets the root pane whose center region will be swapped on navigation.
     * Must be called before any navigation (typically in {@code Application.start()}).
     */
    public void setRootPane(BorderPane rootPane) {
        this.rootPane = rootPane;
    }

    // =========================================================================
    // In-place navigation (child routing)
    // =========================================================================

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
            throw new RoutingException("No @FxMapping found for path: \"%s\"".formatted(path));
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
            log.warn("navigateTo(\"{}\") called off FX Application Thread — deferring via Platform.runLater()", path);
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
        log.info("Reloading current route \"{}\"", currentPath);
        activeRoutes.clear();
        navigateTo(currentPath, currentParams);
    }

    // =========================================================================
    // Window navigation (new Stage)
    // =========================================================================

    /**
     * Opens a route in a new modeless window with default options.
     */
    public void openWindow(String path) {
        openWindow(path, Map.of(), new WindowOptions());
    }

    public void openWindow(String path, WindowOptions options) {
        openWindow(path, Map.of(), options);
    }

    /**
     * Opens a route in a new modeless window with parameters.
     */
    public void openWindow(String path, Map<String, Object> params) {
        openWindow(path, params, new WindowOptions());
    }

    /**
     * Opens a route in a new modeless window with parameters and options.
     * <p>
     * The new window is independent — the parent remains interactive.
     * Stylesheets are inherited from the parent scene.
     *
     * @param path    the route path
     * @param params  model parameters
     * @param options window configuration (title, size, resizable)
     */
    public void openWindow(String path, Map<String, Object> params, WindowOptions options) {
        options.modality(Modality.NONE);
        launchStage(path, params, options, null);
    }

    /**
     * Opens a route as a modal dialog with default options.
     * Blocks the parent window until the dialog is closed.
     *
     * @param <T> the expected result type
     * @return a {@link WindowResult} containing the value set by the modal's controller
     */
    public <T> WindowResult<T> openModal(String path) {
        return openModal(path, Map.of(), new WindowOptions());
    }

    public <T> WindowResult<T> openModal(String path, WindowOptions options) {
        return openModal(path, Map.of(), options);
    }

    /**
     * Opens a route as a modal dialog with parameters.
     *
     * @param <T>    the expected result type
     * @param path   the route path
     * @param params model parameters
     * @return a {@link WindowResult} containing the value set by the modal's controller
     */
    public <T> WindowResult<T> openModal(String path, Map<String, Object> params) {
        return openModal(path, params, new WindowOptions());
    }

    /**
     * Opens a route as a modal dialog with parameters and options.
     * <p>
     * The parent window is blocked until the dialog closes. The modal's
     * FXML controller can set a result via {@link WindowResult#set(Object)},
     * which the caller retrieves after the method returns.
     *
     * <pre>{@code
     * // Caller:
     * WindowResult<String> result = router.openModal("/picker",
     *     Map.of("items", itemList),
     *     new WindowOptions().title("Pick one").size(400, 300));
     * result.get().ifPresent(selected -> nameLabel.setText(selected));
     *
     * // Modal controller:
     * @ModelAttribute private WindowResult<String> windowResult;
     *
     * @FXML private void onConfirm() {
     *     windowResult.set(selectedItem);
     *     closeWindow();
     * }
     * }</pre>
     *
     * @param <T>     the expected result type
     * @param path    the route path
     * @param params  model parameters
     * @param options window configuration (title, size, resizable)
     * @return a {@link WindowResult} containing the value set by the modal's controller
     */
    public <T> WindowResult<T> openModal(String path, Map<String, Object> params, WindowOptions options) {
        options.modality(Modality.APPLICATION_MODAL);
        WindowResult<T> result = new WindowResult<>();
        launchStage(path, params, options, result);
        return result;
    }

    // =========================================================================
    // Core navigation engine
    // =========================================================================

    private void executeNavigation(HandlerMethod handler, Map<String, Object> params) {
        List<HandlerMethod> chain = buildRouteChain(handler);
        Pane currentOutlet = rootPane;

        for (int i = 0; i < chain.size(); i++) {
            HandlerMethod current = chain.get(i);
            boolean isTarget = (i == chain.size() - 1);

            ActiveRoute active = activeRoutes.get(current.path());
            if (active != null && !isTarget) {
                log.debug("Reusing active parent: \"{}\"", current.path());
                if (active.outlet() != null) {
                    currentOutlet = active.outlet();
                }
                continue;
            }

            FxModel model = new FxModel();
            if (isTarget) {
                params.forEach(model::put);
            }
            String viewName = invokeHandler(current, model);
            log.debug("Handler for \"{}\" returned view: \"{}\"", current.path(), viewName);

            ViewResolver.ViewResult result = loadAndPrepareView(viewName, model);
            Parent view = result.view();
            Object controller = result.controller();

            Pane outlet = null;
            if (!isTarget || hasChildRoutes(current.path())) {
                outlet = findOutlet(controller, view);
            }

            invalidateFrom(current.path());
            placeView(currentOutlet, view);
            activeRoutes.put(current.path(), new ActiveRoute(current.path(), view, controller, outlet));
            log.info("Navigated to \"{}\" → view \"{}\"", current.path(), viewName);

            if (outlet != null) {
                currentOutlet = outlet;
            }
        }

        // Update window title from the deepest route that declares one
        applyRouteTitle(chain);
    }

    /**
     * Applies the title from the deepest route in the chain that declares one.
     * If no route in the chain has a title, the window title is left unchanged.
     */
    private void applyRouteTitle(List<HandlerMethod> chain) {
        for (int i = chain.size() - 1; i >= 0; i--) {
            HandlerMethod route = chain.get(i);
            if (route.hasTitle()) {
                titleService.setTitle(route.title());
                return;
            }
        }
    }

    /**
     * Launches a new Stage for a route. Used by both openWindow() and openModal().
     */
    private <T> void launchStage(String path, Map<String, Object> params,
                                 WindowOptions options, WindowResult<T> windowResult) {
        log.debug("Opening window for: {} (modal: {})", path,
                options.getModality() != Modality.NONE);

        HandlerMethod handler = routeRegistry.resolve(path);
        if (handler == null) {
            throw new RoutingException("No @FxMapping found for path: \"%s\"".formatted(path));
        }

        // Prepare model
        FxModel model = new FxModel();
        params.forEach(model::put);
        if (windowResult != null) {
            model.put("windowResult", windowResult);
        }

        // Invoke handler → view name
        String viewName = invokeHandler(handler, model);
        log.debug("Handler returned view name: \"{}\" for window", viewName);

        // Load FXML + inject @ModelAttribute
        ViewResolver.ViewResult result = loadAndPrepareView(viewName, model);

        // Build the Stage
        Stage stage = new Stage();

        if (options.getModality() != Modality.NONE) {
            stage.initModality(options.getModality());
            if (rootPane != null && rootPane.getScene() != null
                    && rootPane.getScene().getWindow() != null) {
                stage.initOwner(rootPane.getScene().getWindow());
            }
        }

        // Create scene
        Scene scene;
        if (options.getWidth() > 0 && options.getHeight() > 0) {
            scene = new Scene(result.view(), options.getWidth(), options.getHeight());
        } else {
            scene = new Scene(result.view());
        }

        // Inherit stylesheets from parent scene
        if (rootPane != null && rootPane.getScene() != null) {
            scene.getStylesheets().addAll(rootPane.getScene().getStylesheets());
        }

        stage.setScene(scene);
        stage.setResizable(options.isResizable());

        if (options.getTitle() != null) {
            stage.setTitle(options.getTitle());
        } else {
            stage.setTitle(viewName);
        }

        log.info("Opened {} window for \"{}\" → view \"{}\"",
                options.getModality() != Modality.NONE ? "modal" : "modeless",
                path, viewName);

        if (options.getModality() != Modality.NONE) {
            stage.showAndWait();  // blocks until closed
        } else {
            stage.show();
        }
    }

    // =========================================================================
    // Route chain & outlet resolution
    // =========================================================================

    private List<HandlerMethod> buildRouteChain(HandlerMethod target) {
        LinkedList<HandlerMethod> chain = new LinkedList<>();
        HandlerMethod current = target;

        Set<String> visited = new HashSet<>();
        while (current != null) {
            if (!visited.add(current.path())) {
                throw new RoutingException("Circular parent reference detected at: \"%s\"".formatted(current.path()));
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

    private boolean hasChildRoutes(String path) {
        String normalized = routeRegistry.normalizePath(path);
        return routeRegistry.getRoutes().values().stream()
                .anyMatch(h -> normalized.equals(h.parent()));
    }

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
                                "Cannot access @RouterOutlet field %s".formatted(field.getName()), e);
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

    private void placeView(Pane outlet, Parent view) {
        if (outlet instanceof BorderPane borderPane) {
            borderPane.setCenter(view);
        } else {
            outlet.getChildren().setAll(view);
        }
    }

    private void invalidateFrom(String path) {
        activeRoutes.entrySet().removeIf(entry -> {
            String activePath = entry.getKey();
            if (activePath.equals(path)) {
                return true;
            }
            HandlerMethod handler = routeRegistry.resolve(activePath);
            if (handler != null && handler.hasParent()) {
                return isDescendantOf(activePath, path);
            }
            return false;
        });
    }

    private boolean isDescendantOf(String path, String ancestorPath) {
        HandlerMethod handler = routeRegistry.resolve(path);
        Set<String> visited = new HashSet<>();
        while (handler != null && handler.hasParent()) {
            if (!visited.add(handler.path())) {
                return false;
            }
            if (handler.parent().equals(ancestorPath)) {
                return true;
            }
            handler = routeRegistry.resolve(handler.parent());
        }
        return false;
    }

    // =========================================================================
    // Handler invocation & view loading
    // =========================================================================

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