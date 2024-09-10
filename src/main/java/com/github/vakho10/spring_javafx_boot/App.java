package com.github.vakho10.spring_javafx_boot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class App extends Application {

    private ConfigurableApplicationContext springContext;

    /**
     * A SpringApplicationBuilder is used to bootstrap the Spring Boot application,
     * with getParameters().getRaw() retrieving the command-line arguments
     * and passing them to Spring Boot.
     */
    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(App.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Called start(..) with primaryStage");

        Scene primaryScene = springContext.getBean("primaryScene", Scene.class);

        primaryStage.setScene(primaryScene);
        primaryStage.setTitle("Spring Boot JavaFX Application");
        primaryStage.show();
    }

    /**
     * Ensures that the Spring application context is closed when the JavaFX application stops.
     */
    @Override
    public void stop() {
        log.info("Called stop()");
        springContext.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
