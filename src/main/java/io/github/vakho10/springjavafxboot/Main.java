package io.github.vakho10.springjavafxboot;

import io.github.vakho10.springjavafxboot.controller.MainController;
import io.github.vakho10.springjavafxboot.navigation.Navigator;
import io.github.vakho10.springjavafxboot.navigation.ViewResolver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
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
        // Load font variants so they can be referenced in CSS
        Font.loadFont(getClass().getResourceAsStream("/fonts/NotoSansGeorgian-Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NotoSansGeorgian-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NotoSansGeorgian-Medium.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NotoSansGeorgian-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NotoSansGeorgian-Bold.ttf"), 14);

        // Load initial view using ViewResolver
        ViewResolver viewResolver = springContext.getBean(ViewResolver.class);
        FXMLLoader loader = new FXMLLoader(viewResolver.resolve(MainController.class));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Spring Boot + JavaFX");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app.png")));

        // Wire the Navigator with the primary stage
        Navigator navigator = springContext.getBean(Navigator.class);
        navigator.setPrimaryStage(primaryStage);

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close(); // Clean shutdown
    }
}
