package com.example.fxdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class Main extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args); // This starts JavaFX and keeps the app alive
    }

    @Override
    public void init() {
        // Boot Spring before the UI loads
        springContext = SpringApplication.run(AppConfig.class);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Use Spring's context to get your main controller or build the scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/main.fxml"));
        loader.setControllerFactory(springContext::getBean); // Spring injects controllers
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Spring Boot + JavaFX");
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close(); // Clean shutdown
    }
}
