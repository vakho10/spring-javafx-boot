package io.github.vakho10.springjavafxboot.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a JavaFX route configuration.
 * <p>
 * Classes annotated with {@code @FxRoutes} contain {@link FxMapping} methods
 * that define navigation routes. These are <em>not</em> FXML controllers — they
 * are Spring-managed singletons responsible only for preparing the
 * {@link io.github.vakho10.springjavafxboot.router.FxModel FxModel}
 * and returning a view name.
 * <p>
 * This separation keeps FXML controllers focused on the view lifecycle ({@code @FXML}
 * fields, {@code initialize()}, UI event handlers), while route configuration
 * handles navigation logic and data preparation.
 *
 * <pre>{@code
 * @FxRoutes
 * public class AppRoutes {
 *
 *     @Autowired
 *     private UserService userService;
 *
 *     @FxMapping("/main")
 *     public String main(FxModel model) {
 *         model.put("username", userService.getCurrentUser().getName());
 *         return "main";
 *     }
 *
 *     @FxMapping("/settings")
 *     public String settings(FxModel model) {
 *         model.put("theme", userService.getPreferredTheme());
 *         return "settings";
 *     }
 * }
 * }</pre>
 *
 * @see FxMapping
 * @see io.github.vakho10.springjavafxboot.router.FxRouter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component  // Makes it a Spring-managed bean automatically
public @interface FxRoutes {
}
