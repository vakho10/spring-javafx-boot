package io.github.vakho10.springjavafxboot.router;

import io.github.vakho10.springjavafxboot.navigation.ViewResolver;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * The central JavaFX routing service — analogous to Spring MVC's {@code DispatcherServlet}.
 * <p>
 * Orchestrates the full navigation lifecycle with a clean separation between
 * route handlers ({@link FxRoutes @FxRoutes} classes) and FXML view controllers
 * ({@code @Controller @Scope("prototype")} classes):
 *
 * <ol>
 *   <li>Resolve the path to a {@link HandlerMethod} via {@link FxRouteRegistry}</li>
 *   <li>Create a fresh {@link FxModel} and populate it with navigation parameters</li>
 *   <li>Invoke the handler on the {@code @FxRoutes} bean — it populates the model
 *       and returns a view name</li>
 *   <li>Pass the view name to {@link ViewResolver} — it loads the FXML, creates a
 *       fresh controller instance via Spring (prototype scope), and wires in
 *       {@code MessageSource} for i18n</li>
 *   <li>Inject {@link ModelAttribute @ModelAttribute} fields into the FXML controller
 *       from the populated model</li>
 *   <li>Trigger the controller's {@code @FXML initialize()} by completing the
 *       FXMLLoader lifecycle</li>
 *   <li>Swap the loaded view into the scene's root pane</li>
 * </ol>
 *
 * <h3>Usage from FXML controllers:</h3>
 * <pre>{@code
 * @Autowired private FxRouter router;
 *
 * @FXML
 * private void onSettingsClick() {
 *     router.navigateTo("/settings", Map.of("tab", "appearance"));
 * }
 * }</pre>
 *
 * <h3>Usage for initial view (in Application.start()):</h3>
 * <pre>{@code
 * router.setRootPane(rootPane);
 * router.navigateTo("/main");
 * }</pre>
 *
 * @see FxRoutes
 * @see FxMapping
 * @see FxRouteRegistry
 * @see ModelAttribute
 */
@Service
public class FxRouter {

    private static final Logger log = LoggerFactory.getLogger(FxRouter.class);

    private final FxRouteRegistry routeRegistry;
    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;

    /**
     * -- SETTER --
     *  Sets the root pane whose center region will be swapped on navigation.
     *  Must be called before any navigation (typically in
     * ).
     */
    @Setter
    private BorderPane rootPane;
    /**
     * -- GETTER --
     *  Returns the currently active route path, or
     *  before first navigation.
     */
    @Getter
    private String currentPath;
    private Map<String, Object> currentParams = Map.of();

    public FxRouter(FxRouteRegistry routeRegistry,
                    ViewResolver viewResolver,
                    ApplicationContext applicationContext) {
        this.routeRegistry = routeRegistry;
        this.viewResolver = viewResolver;
        this.applicationContext = applicationContext;
    }

    /**
     * Navigate to a route with no parameters.
     *
     * @param path the route path (e.g. "/main")
     * @throws RoutingException if the route is not found or navigation fails
     */
    public void navigateTo(String path) {
        navigateTo(path, Map.of());
    }

    /**
     * Navigate to a route with parameters.
     * <p>
     * Parameters are added to the {@link FxModel} before the handler is invoked,
     * and are available for {@link ModelAttribute} injection in the FXML controller.
     *
     * @param path   the route path (e.g. "/settings")
     * @param params key-value pairs to pre-populate in the model
     * @throws RoutingException if the route is not found or navigation fails
     */
    public void navigateTo(String path, Map<String, Object> params) {
        log.debug("Navigating to: {} with params: {}", path, params.keySet());

        // 1. Resolve route → handler
        HandlerMethod handler = routeRegistry.resolve(path);
        if (handler == null) {
            throw new RoutingException("No @FxMapping found for path: \"" + path + "\"");
        }

        // 2. Create model, pre-populate with navigation params
        FxModel model = new FxModel();
        params.forEach(model::put);

        // 3. Invoke the @FxRoutes handler → returns view name
        String viewName = invokeHandler(handler, model);
        log.debug("Handler returned view name: \"{}\"", viewName);

        // 4–6. Load FXML, inject @ModelAttribute, get view
        Parent view = loadAndPrepareView(viewName, model);

        // 7. Swap the view into the root pane
        Runnable swap = () -> {
            requireRootPane();
            rootPane.setCenter(view);
            currentPath = path;
            currentParams = params;
            log.info("Navigated to \"{}\" → view \"{}\"", path, viewName);
        };

        if (Platform.isFxApplicationThread()) {
            swap.run();
        } else {
            Platform.runLater(swap);
        }
    }

    /**
     * Re-navigates to the current route with the same parameters.
     * Useful after a locale change to reload the view with new translations.
     *
     * @throws RoutingException if no current path is set
     */
    public void reload() {
        if (currentPath == null) {
            throw new RoutingException("Cannot reload — no current route.");
        }
        navigateTo(currentPath, currentParams);
    }

    // -------------------------------------------------------------------------
    // Internal
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

    private Parent loadAndPrepareView(String viewName, FxModel model) {
        try {
            // Load FXML with Spring controller factory + MessageSource i18n
            ViewResolver.ViewResult result = viewResolver.loadView(
                    viewName, applicationContext::getBean);

            // Inject @ModelAttribute fields into the FXML controller
            Object controller = result.controller();
            if (controller != null) {
                injectModelAttributes(controller, model);
                // Trigger post-model initialization if the controller defines it
                invokePostModelInit(controller, model);
            }

            return result.view();

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

    /**
     * If the FXML controller has a method {@code void onModelReady(FxModel)},
     * invoke it after model attribute injection. This provides a hook for
     * controllers that need the full model (not just individual fields).
     */
    private void invokePostModelInit(Object controller, FxModel model) {
        try {
            Method onModelReady = controller.getClass().getDeclaredMethod("onModelReady", FxModel.class);
            onModelReady.setAccessible(true);
            onModelReady.invoke(controller, model);
            log.trace("Called onModelReady() on {}", controller.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            // No onModelReady method — that's fine, it's optional
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