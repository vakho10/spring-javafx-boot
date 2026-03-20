package io.github.vakho10.springjavafxboot;

import io.github.vakho10.springjavafxboot.controller.MainController;
import io.github.vakho10.springjavafxboot.navigation.Navigator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
    public void start(Stage primaryStage) {
        // Load font variants so they can be referenced in CSS
        Font.loadFont(getClass().getResourceAsStream("/fonts/roboto/Roboto-Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/roboto/Roboto-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/roboto/Roboto-Medium.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/roboto/Roboto-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/noto-sans-georgian/NotoSansGeorgian-Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/noto-sans-georgian/NotoSansGeorgian-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/noto-sans-georgian/NotoSansGeorgian-Medium.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/noto-sans-georgian/NotoSansGeorgian-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/noto-sans-georgian/NotoSansGeorgian-Bold.ttf"), 14);

        // Create scene with BorderPane root: menu bar at top, Navigator swaps center
        Scene scene = new Scene(new BorderPane(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Spring Boot + JavaFX");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app.png")));

        // Wire the Navigator and navigate to the initial view
        Navigator navigator = springContext.getBean(Navigator.class);
        navigator.setPrimaryStage(primaryStage);
        navigator.navigateTo(MainController.class);

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close(); // Clean shutdown
    }
}
