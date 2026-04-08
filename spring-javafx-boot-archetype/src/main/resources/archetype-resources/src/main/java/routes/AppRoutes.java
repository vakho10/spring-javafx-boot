#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.routes;

import io.github.vakho10.springjavafxboot.annotation.FxMapping;
import io.github.vakho10.springjavafxboot.annotation.FxRoutes;
import io.github.vakho10.springjavafxboot.router.FxModel;

/**
 * Application route definitions.
 *
 * <pre>
 *   "/"       → layout.fxml   (LayoutController — menu bar + outlet)
 *   "/main"   → main.fxml     (MainController — child of /)
 *   "/second" → second.fxml   (SecondController — child of /)
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
}
