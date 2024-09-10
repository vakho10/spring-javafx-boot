package com.github.vakho10.spring_javafx_boot.configuration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class BeanConfiguration {

    @Bean
    public Scene primaryScene(FXMLLoader fxmlLoader) throws IOException {
        try (InputStream fxmlStream = getClass().getResourceAsStream("/fxml/main.fxml")) {
            return new Scene(fxmlLoader.load(fxmlStream), 600, 400);
        }
    }

    @Bean
    public FXMLLoader fxmlLoader(ApplicationContext springContext) {
        FXMLLoader loader = new FXMLLoader();
        // Sets the JavaFX controller factory to use Spring Boot's controller factory,
        // ensuring that Spring Boot manages the JavaFX controller's lifecycle.
        loader.setControllerFactory(springContext::getBean);
        return loader;
    }
}
