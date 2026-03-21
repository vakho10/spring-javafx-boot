package io.github.vakho10.springjavafxboot.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field in an FXML controller for injection from the {@link FxModel}.
 * <p>
 * After the router invokes the {@link FxMapping} handler and obtains a populated
 * model, it loads the FXML view, retrieves the controller, and injects model
 * attributes into fields annotated with {@code @ModelAttribute} — all before
 * the FXML {@code initialize()} method runs.
 *
 * <pre>{@code
 * @Component
 * @Scope("prototype")
 * public class SettingsController {
 *
 *     @ModelAttribute
 *     private String activeTab;   // injected from model.put("activeTab", ...)
 *
 *     @FXML private TabPane tabPane;
 *
 *     @FXML
 *     public void initialize() {
 *         // activeTab is already set here
 *         tabPane.getSelectionModel().select(findTab(activeTab));
 *     }
 * }
 * }</pre>
 * <p>
 * By default, the model key matches the field name. Use {@link #value()} to
 * override with a custom key.
 *
 * @see FxModel
 * @see FxRouter
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelAttribute {

    /**
     * The model attribute key. Defaults to the field name if empty.
     */
    String value() default "";
}