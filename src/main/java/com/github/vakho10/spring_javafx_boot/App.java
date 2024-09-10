package com.github.vakho10.spring_javafx_boot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class App extends Application {

    private static ConfigurableApplicationContext context;

    /**
     * A SpringApplicationBuilder is used to bootstrap the Spring Boot application,
     * with getParameters().getRaw() retrieving the command-line arguments
     * and passing them to Spring Boot.
     */
    @Override
    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Called start(..) with primaryStage");
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/main.fxml"));

        // Sets the JavaFX controller factory to use Spring Boot's controller factory,
        // ensuring that Spring Boot manages the JavaFX controller's lifecycle.
        fxmlLoader.setControllerFactory(context::getBean);

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Spring Boot JavaFX Application");
        primaryStage.show();
    }

    /**
     * Ensures that the Spring application context is closed when the JavaFX application stops.
     */
    @Override
    public void stop() {
        log.info("Called stop()");
        context.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
