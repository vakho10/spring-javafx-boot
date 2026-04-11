package io.github.vakho10.springjavafxboot.router;

import java.util.Map;

/**
 * Navigation guard that can prevent or allow route transitions.
 * <p>
 * Guards are checked by the {@link FxRouter} before navigation proceeds.
 * There are two types of guards:
 * <ul>
 *   <li><b>Controller guards</b> — FXML controllers that implement this interface.
 *       The active controller's {@link #canDeactivate} is checked before navigating away.</li>
 *   <li><b>Global guards</b> — Spring beans that implement this interface.
 *       All global guards are checked on every navigation (both {@link #canActivate}
 *       and {@link #canDeactivate}).</li>
 * </ul>
 *
 * <h3>Controller guard (e.g. unsaved changes):</h3>
 * <pre>{@code
 * @Controller
 * @Scope("prototype")
 * public class FormController implements FxRouteGuard {
 *
 *     private boolean hasUnsavedChanges = false;
 *
 *     @Override
 *     public boolean canDeactivate(String currentPath, String targetPath) {
 *         if (hasUnsavedChanges) {
 *             return confirmDiscard(); // show confirmation dialog
 *         }
 *         return true;
 *     }
 * }
 * }</pre>
 *
 * <h3>Global guard (e.g. authentication):</h3>
 * <pre>{@code
 * @Component
 * public class AuthGuard implements FxRouteGuard {
 *
 *     @Override
 *     public boolean canActivate(String targetPath, Map<String, Object> params) {
 *         return authService.isLoggedIn() || targetPath.equals("/login");
 *     }
 * }
 * }</pre>
 *
 * @see FxRouter
 */
public interface FxRouteGuard {

    /**
     * Called before navigating away from the current route.
     * Return {@code false} to cancel navigation.
     *
     * @param currentPath the route being left
     * @param targetPath  the route being navigated to
     * @return {@code true} to allow navigation, {@code false} to block it
     */
    default boolean canDeactivate(String currentPath, String targetPath) {
        return true;
    }

    /**
     * Called before navigating to a route.
     * Return {@code false} to cancel navigation.
     *
     * @param targetPath the route being navigated to
     * @param params     the navigation parameters
     * @return {@code true} to allow navigation, {@code false} to block it
     */
    default boolean canActivate(String targetPath, Map<String, Object> params) {
        return true;
    }
}
