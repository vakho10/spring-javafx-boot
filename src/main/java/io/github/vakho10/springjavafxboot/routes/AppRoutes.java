package io.github.vakho10.springjavafxboot.routes;

import io.github.vakho10.springjavafxboot.router.FxMapping;
import io.github.vakho10.springjavafxboot.router.FxModel;
import io.github.vakho10.springjavafxboot.router.FxRoutes;

/**
 * Application route definitions.
 * <p>
 * Each {@link FxMapping} method prepares data for its target view and
 * returns the logical view name. The FXML controllers are separate
 * prototype-scoped beans that receive the model data via
 * {@link io.github.vakho10.springjavafxboot.router.ModelAttribute @ModelAttribute}.
 *
 * <pre>
 *   "/main"   → main.fxml   (MainController)
 *   "/second" → second.fxml (SecondController)
 * </pre>
 */
@FxRoutes
public class AppRoutes {

    @FxMapping("/main")
    public String main(FxModel model) {
        return "main";
    }

    @FxMapping("/second")
    public String second(FxModel model) {
        return "second";
    }
}