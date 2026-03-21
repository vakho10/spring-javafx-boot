package io.github.vakho10.springjavafxboot.router;

import java.lang.reflect.Method;

/**
 * Represents a resolved {@link FxMapping} handler — the route configuration
 * bean and the method to invoke.
 * <p>
 * These are created during startup by {@link FxRouteRegistry} from
 * {@link FxRoutes @FxRoutes} classes and stored in the route table.
 * <p>
 * Note: the {@link #bean()} is the {@code @FxRoutes} instance, <em>not</em>
 * an FXML controller. The handler method returns a view name; the FXML
 * controller is a separate Spring-managed bean resolved by the
 * {@link io.github.vakho10.springjavafxboot.navigation.ViewResolver ViewResolver}.
 */
public record HandlerMethod(

        /** The route path (e.g. "/main"). */
        String path,

        /** The {@link FxRoutes @FxRoutes} bean that owns this handler. */
        Object bean,

        /** The method annotated with {@link FxMapping}. */
        Method method
) {

    /**
     * The {@link FxRoutes} class that declares this handler.
     */
    public Class<?> routeConfigClass() {
        return bean.getClass();
    }
}