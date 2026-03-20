package io.github.vakho10.springjavafxboot.navigation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * Resolves controller classes to FXML template paths by naming convention.
 * e.g. MainController → /templates/main.fxml, SecondController → /templates/second.fxml
 */
@Component
public class ViewResolver {

    @Value("${spring.javafx.view.prefix:/templates/}")
    private String prefix;

    @Value("${spring.javafx.view.suffix:.fxml}")
    private String suffix;

    public URL resolve(Class<?> controllerClass) {
        String viewName = controllerClass.getSimpleName()
                .replaceAll("Controller$", "")
                .toLowerCase();
        String path = prefix + viewName + suffix;
        URL resource = getClass().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("No FXML template found for " + controllerClass.getSimpleName() + " at " + path);
        }
        return resource;
    }
}
