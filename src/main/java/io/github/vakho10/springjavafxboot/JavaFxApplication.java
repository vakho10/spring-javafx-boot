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

import java.io.InputStream;
import java.net.URL;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    private static final String[] FONT_PATHS = {
            "/fonts/roboto/Roboto-Light.ttf",
            "/fonts/roboto/Roboto-Regular.ttf",
            "/fonts/roboto/Roboto-Medium.ttf",
            "/fonts/roboto/Roboto-Bold.ttf",
            "/fonts/noto-sans-georgian/NotoSansGeorgian-Light.ttf",
            "/fonts/noto-sans-georgian/NotoSansGeorgian-Regular.ttf",
            "/fonts/noto-sans-georgian/NotoSansGeorgian-Medium.ttf",
            "/fonts/noto-sans-georgian/NotoSansGeorgian-SemiBold.ttf",
            "/fonts/noto-sans-georgian/NotoSansGeorgian-Bold.ttf"
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        // Boot Spring before the UI loads, forwarding CLI args
        String[] args = getParameters().getRaw().toArray(String[]::new);
        springContext = SpringApplication.run(AppConfig.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadFonts();

        Scene scene = new Scene(new BorderPane(), 800, 600);
        addStylesheet(scene, "/css/styles.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Spring Boot + JavaFX");

        InputStream iconStream = getClass().getResourceAsStream("/icons/app.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }

        Navigator navigator = springContext.getBean(Navigator.class);
        navigator.setPrimaryStage(primaryStage);
        navigator.navigateTo(MainController.class);

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }

    private void loadFonts() {
        for (String path : FONT_PATHS) {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                Font.loadFont(stream, 14);
            }
        }
    }

    private void addStylesheet(Scene scene, String path) {
        URL resource = getClass().getResource(path);
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        }
    }
}
