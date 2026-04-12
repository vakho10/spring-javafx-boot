package io.github.vakho10.springjavafxboot;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.service.ErrorHandler;
import io.github.vakho10.springjavafxboot.service.FxTitleService;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import io.github.vakho10.springjavafxboot.service.ThemeService;
import io.github.vakho10.springjavafxboot.service.UserPreferencesService;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
        String[] args = getParameters().getRaw().toArray(String[]::new);
        springContext = SpringApplication.run(AppConfig.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadFonts();

        // Register Stage as a bean for late-created components (e.g. prototype controllers).
        // Eager singletons like ErrorHandler receive it via init() instead.
        springContext.getBeanFactory().registerSingleton("primaryStage", primaryStage);

        // Restore saved locale
        UserPreferencesService prefs = springContext.getBean(UserPreferencesService.class);
        LocalizedMessageSource messages = springContext.getBean(LocalizedMessageSource.class);
        Locale currentLocale = prefs.getLocale();
        Locale.setDefault(currentLocale);
        messages.setLocale(currentLocale);

        // Build scene
        BorderPane rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 800, 600);
        addStylesheet(scene, "/css/styles.css");

        primaryStage.setScene(scene);

        // Initialize title service with app-name format: "Page — App Name"
        FxTitleService titleService = springContext.getBean(FxTitleService.class);
        titleService.init(primaryStage);
        titleService.setTitleFormat("%s \u2014 " + messages.msg("app.title"));

        try (InputStream iconStream = getClass().getResourceAsStream("/icons/app.png")) {
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            // Non-critical — app works without an icon
        }

        // Initialize theme (loads dark.css or light.css based on saved preference)
        ThemeService themeService = springContext.getBean(ThemeService.class);
        themeService.init(scene);
        themeService.applyFontStylesheet(currentLocale);

        // Set up global error handler
        ErrorHandler errorHandler = springContext.getBean(ErrorHandler.class);
        errorHandler.init(primaryStage);

        // Initialize router and navigate to initial child route.
        // This loads the "/" layout (with menu bar) then "/main" inside its outlet.
        FxRouter router = springContext.getBean(FxRouter.class);
        router.setRootPane(rootPane);
        router.navigateTo("/main");

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
