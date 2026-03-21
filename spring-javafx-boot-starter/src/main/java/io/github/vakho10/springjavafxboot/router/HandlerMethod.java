package io.github.vakho10.springjavafxboot.router;

import java.lang.reflect.Method;

/**
 * Represents a resolved {@link FxMapping} handler — the route configuration
 * bean, the method to invoke, and the parent relationship.
 * <p>
 * Created during startup by {@link FxRouteRegistry} from
 * {@link FxRoutes @FxRoutes} classes and stored in the route table.
 */
public record HandlerMethod(

        /** The route path (e.g. "/main"). */
        String path,

        /** The parent route path, or {@code ""} if this is a root route. */
        String parent,

        /** The title (message key or literal), or {@code ""} if not set. */
        String title,

        /** The {@link FxRoutes @FxRoutes} bean that owns this handler. */
        Object bean,

        /** The method annotated with {@link FxMapping}. */
        Method method
) {

    /**
     * Whether this route has a parent (i.e. is a child route).
     */
    public boolean hasParent() {
        return parent != null && !parent.isEmpty();
    }

    /**
     * Whether this route declares a title.
     */
    public boolean hasTitle() {
        return title != null && !title.isEmpty();
    }

    /**
     * The {@link FxRoutes} class that declares this handler.
     */
    public Class<?> routeConfigClass() {
        return bean.getClass();
    }
}