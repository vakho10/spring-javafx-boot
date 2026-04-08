package io.github.vakho10.springjavafxboot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the JavaFX view resolver.
 * <p>
 * These properties control how logical view names are resolved to FXML
 * template files on the classpath. For example, with the default settings,
 * the view name {@code "main"} resolves to {@code /templates/main.fxml}.
 *
 * <pre>
 *   spring.javafx.view.prefix=/templates/
 *   spring.javafx.view.suffix=.fxml
 * </pre>
 *
 * @see io.github.vakho10.springjavafxboot.navigation.ViewResolver
 */
@ConfigurationProperties(prefix = "spring.javafx.view")
public class FxViewProperties {

    /**
     * Classpath prefix for FXML templates.
     */
    private String prefix = "/templates/";

    /**
     * File extension for FXML templates.
     */
    private String suffix = ".fxml";

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
