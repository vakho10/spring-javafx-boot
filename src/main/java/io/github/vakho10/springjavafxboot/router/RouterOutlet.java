package io.github.vakho10.springjavafxboot.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link javafx.scene.layout.Pane} field in an FXML controller as
 * the outlet where child routes will be rendered.
 * <p>
 * When the router navigates to a child route, the child's view is placed
 * inside this pane. If no {@code @RouterOutlet} is found, the router
 * falls back to looking for an {@code fx:id="routerOutlet"} node in the
 * loaded FXML.
 *
 * <pre>{@code
 * @Controller
 * @Scope("prototype")
 * public class LayoutController {
 *
 *     @FXML
 *     @RouterOutlet
 *     private BorderPane contentArea;
 * }
 * }</pre>
 *
 * @see FxMapping#parent()
 * @see FxRouter
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RouterOutlet {
}