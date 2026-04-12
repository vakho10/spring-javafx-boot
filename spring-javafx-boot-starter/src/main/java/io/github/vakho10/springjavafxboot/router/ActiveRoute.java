package io.github.vakho10.springjavafxboot.router;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * Represents an active (loaded) route in the route hierarchy.
 * <p>
 * When a parent route is loaded, an {@code ActiveRoute} is created and cached
 * so that child navigations can reuse the parent's layout and outlet without
 * reloading it.
 *
 * @param path       the route path (e.g. "/")
 * @param view       the loaded FXML root node
 * @param controller the FXML controller instance (may be {@code null})
 * @param outlet     the outlet pane for child views (may be {@code null} for leaf routes)
 */
public record ActiveRoute(
        String path,
        Parent view,
        Object controller,
        Pane outlet
) {
}
