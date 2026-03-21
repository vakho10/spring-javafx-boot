package io.github.vakho10.springjavafxboot.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.function.Function;

/**
 * Convention-based FXML view resolver inspired by Spring MVC's
 * {@code InternalResourceViewResolver}.
 * <p>
 * Resolves a logical view name to a classpath FXML resource using
 * configurable prefix and suffix:
 * <pre>
 *   "main"     → /templates/main.fxml
 *   "settings" → /templates/settings.fxml
 * </pre>
 * <p>
 * Configuration in {@code application.properties}:
 * <pre>
 *   spring.javafx.view.prefix=/templates/
 *   spring.javafx.view.suffix=.fxml
 * </pre>
 */
@Component
public class ViewResolver {

    private static final Logger log = LoggerFactory.getLogger(ViewResolver.class);

    @Value("${spring.javafx.view.prefix:/templates/}")
    private String prefix;

    @Value("${spring.javafx.view.suffix:.fxml}")
    private String suffix;

    private final MessageSource messageSource;

    public ViewResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Holds the result of loading an FXML view.
     *
     * @param view       the root {@link Parent} node
     * @param controller the FXML controller instance (may be {@code null})
     */
    public record ViewResult(Parent view, Object controller) {
    }

    /**
     * Resolves a view name to a classpath resource path.
     *
     * @param viewName the logical view name (e.g. "main")
     * @return the resource path (e.g. "/templates/main.fxml")
     */
    public String resolveViewPath(String viewName) {
        return prefix + viewName + suffix;
    }

    /**
     * Resolves and loads an FXML view by name.
     * <p>
     * The controller factory delegates to Spring so that the FXML controller
     * is a Spring-managed bean (with {@code @Autowired}, {@code @Value}, etc.).
     * The {@link MessageSource} is bridged to a {@link java.util.ResourceBundle}
     * for JavaFX's {@code %key} i18n syntax in FXML.
     *
     * @param viewName          the logical view name
     * @param controllerFactory typically {@code applicationContext::getBean}
     * @return a {@link ViewResult} with the view and its controller
     * @throws IOException if the FXML resource is not found or cannot be loaded
     */
    public ViewResult loadView(String viewName,
                               Function<Class<?>, Object> controllerFactory) throws IOException {
        String path = resolveViewPath(viewName);
        URL resource = getClass().getResource(path);

        if (resource == null) {
            throw new IOException("FXML template not found: " + path);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory::apply);
        loader.setResources(new MessageSourceResourceBundle(messageSource, Locale.getDefault()));

        Parent view = loader.load();
        Object controller = loader.getController();

        log.debug("Loaded view \"{}\" from {} (controller: {})",
                viewName, path,
                controller != null ? controller.getClass().getSimpleName() : "none");

        return new ViewResult(view, controller);
    }
}