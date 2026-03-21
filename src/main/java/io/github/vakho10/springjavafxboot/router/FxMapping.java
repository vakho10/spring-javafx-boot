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
 * <pre>{@code
 * @FxRoutes
 * public class AppRoutes {
 *
 *     @FxMapping("/main")
 *     public String main(FxModel model) {
 *         model.put("greeting", "Hello!");
 *         return "main"; // → /templates/main.fxml
 *     }
 * }
 * }</pre>
 *
 * @see FxRoutes
 * @see FxRouter
 * @see FxModel
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FxMapping {

    /**
     * The route path (e.g. {@code "/main"}, {@code "/settings"}).
     */
    String value();
}