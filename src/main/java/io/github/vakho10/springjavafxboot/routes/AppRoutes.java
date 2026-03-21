package io.github.vakho10.springjavafxboot.routes;

import io.github.vakho10.springjavafxboot.router.FxMapping;
import io.github.vakho10.springjavafxboot.router.FxModel;
import io.github.vakho10.springjavafxboot.router.FxRoutes;

/**
 * Application route definitions.
 * <p>
 * The root route {@code "/"} loads the application layout (menu bar + content area).
 * Child routes render inside the layout's {@code @RouterOutlet}.
 *
 * <pre>
 *   "/"       → layout.fxml    (LayoutController — menu bar + outlet)
 *   "/main"   → main.fxml      (MainController — child of /)
 *   "/second" → second.fxml    (SecondController — child of /)
 * </pre>
 */
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";
    }

    @FxMapping(value = "/main", parent = "/")
    public String main(FxModel model) {
        return "main";
    }

    @FxMapping(value = "/second", parent = "/")
    public String second(FxModel model) {
        return "second";
    }
}