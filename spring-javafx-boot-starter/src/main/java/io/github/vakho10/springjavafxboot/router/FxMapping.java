package io.github.vakho10.springjavafxboot.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a method in an {@link FxRoutes} class to a named route.
 * <p>
 * When {@link FxRouter#navigateTo(String)} is called with a matching path,
 * the annotated method is invoked. It receives a fresh {@link FxModel},
 * populates it with data, and returns a view name that the
 * {@link io.github.vakho10.springjavafxboot.navigation.ViewResolver ViewResolver}
 * resolves to an FXML template.
 *
 * <h3>Simple route:</h3>
 * <pre>{@code
 * @FxMapping("/main")
 * public String main(FxModel model) {
 *     return "main";
 * }
 * }</pre>
 *
 * <h3>Child route (rendered inside a parent layout):</h3>
 * <pre>{@code
 * @FxMapping("/")
 * public String layout(FxModel model) {
 *     return "layout";  // has a @RouterOutlet or fx:id="routerOutlet"
 * }
 *
 * @FxMapping(value = "/main", parent = "/")
 * public String main(FxModel model) {
 *     return "main";  // rendered inside layout's outlet
 * }
 * }</pre>
 *
 * @see FxRoutes
 * @see FxRouter
 * @see FxModel
 * @see RouterOutlet
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxMapping {

    /**
     * The route path (e.g. {@code "/main"}, {@code "/settings"}).
     */
    String value();

    /**
     * The parent route path. When set, this route is a child route — its view
     * is rendered inside the parent's {@link RouterOutlet} rather than replacing
     * the entire scene.
     * <p>
     * The parent route must also have an {@code @FxMapping}. If the parent is
     * already active, the router reuses its layout and only swaps the child view
     * in the outlet. Nesting is unlimited (parent → child → grandchild).
     * <p>
     * Defaults to {@code ""} (no parent — root-level route).
     */
    String parent() default "";
}