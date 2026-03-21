package io.github.vakho10.springjavafxboot.routes;

import io.github.vakho10.springjavafxboot.router.FxMapping;
import io.github.vakho10.springjavafxboot.router.FxModel;
import io.github.vakho10.springjavafxboot.router.FxRoutes;

/**
 * Application route definitions.
 *
 * <pre>
 *   "/"            → layout.fxml       (LayoutController — menu bar + outlet)
 *   "/main"        → main.fxml         (MainController — child of /)
 *   "/second"      → second.fxml       (SecondController — child of /)
 *   "/demo/window" → demo-window.fxml  (DemoWindowController — opened as modeless window)
 *   "/demo/modal"  → demo-modal.fxml   (DemoModalController — opened as modal dialog)
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

    @FxMapping("/demo/window")
    public String demoWindow(FxModel model) {
        return "demo-window";
    }

    @FxMapping("/demo/modal")
    public String demoModal(FxModel model) {
        return "demo-modal";
    }
}