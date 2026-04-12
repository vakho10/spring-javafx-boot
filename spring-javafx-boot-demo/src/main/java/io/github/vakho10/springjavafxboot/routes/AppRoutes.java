package io.github.vakho10.springjavafxboot.routes;

import io.github.vakho10.springjavafxboot.annotation.FxMapping;
import io.github.vakho10.springjavafxboot.annotation.FxRoutes;
import io.github.vakho10.springjavafxboot.annotation.PathVariable;
import io.github.vakho10.springjavafxboot.router.FxModel;

/**
 * Application route definitions.
 *
 * <pre>
 *   "/"            → layout.fxml       (LayoutController — menu bar + outlet)
 *   "/main"        → main.fxml         (MainController — child of /)
 *   "/second"      → second.fxml       (SecondController — child of /)
 *   "/detail/{id}" → detail.fxml       (DetailController — child of /, path variable demo)
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

    @FxMapping(value = "/main", parent = "/", title = "page.title.main")
    public String main(FxModel model) {
        return "main";
    }

    @FxMapping(value = "/second", parent = "/", title = "page.title.second")
    public String second(FxModel model) {
        return "second";
    }

    @FxMapping(value = "/detail/{id}", parent = "/", title = "page.title.detail")
    public String detail(@PathVariable("id") Long id, FxModel model) {
        model.put("id", id);
        return "detail";
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
