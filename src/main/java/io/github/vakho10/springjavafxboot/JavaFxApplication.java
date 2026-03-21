package io.github.vakho10.springjavafxboot;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.service.ErrorHandler;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import io.github.vakho10.springjavafxboot.service.ThemeService;
import io.github.vakho10.springjavafxboot.service.UserPreferencesService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    private static final Locale GEORGIAN = Locale.of("ka");

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

    // Keep references for theme switching without full menu rebuild
    private RadioMenuItem darkItem;
    private RadioMenuItem lightItem;

    // Services (resolved from Spring context in start())
    private FxRouter router;
    private LocalizedMessageSource messages;
    private ThemeService themeService;
    private UserPreferencesService preferencesService;
    private BorderPane rootPane;
    private Locale currentLocale;

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

        // Resolve Spring beans
        router = springContext.getBean(FxRouter.class);
        messages = springContext.getBean(LocalizedMessageSource.class);
        themeService = springContext.getBean(ThemeService.class);
        preferencesService = springContext.getBean(UserPreferencesService.class);

        // Restore saved locale
        currentLocale = preferencesService.getLocale();
        Locale.setDefault(currentLocale);
        messages.setLocale(currentLocale);

        // Build scene
        rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 800, 600);
        addStylesheet(scene, "/css/styles.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Spring Boot + JavaFX");

        InputStream iconStream = getClass().getResourceAsStream("/icons/app.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }

        // Initialize theme (loads dark.css or light.css based on saved preference)
        themeService.init(scene);
        themeService.applyFontStylesheet(currentLocale);

        // Build menu bar
        buildMenuBar();

        // Set up global error handler
        ErrorHandler errorHandler = springContext.getBean(ErrorHandler.class);
        errorHandler.init(primaryStage);

        // Initialize router and navigate to initial view
        router.setRootPane(rootPane);
        router.navigateTo("/main");

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }

    private void switchLocale(Locale locale) {
        currentLocale = locale;
        Locale.setDefault(locale);
        messages.setLocale(locale);
        preferencesService.setLocale(locale);
        themeService.applyFontStylesheet(locale);
        buildMenuBar();
        router.reload();
    }

    private void switchTheme(String theme) {
        themeService.switchTheme(theme);
        darkItem.setSelected("dark".equals(theme));
        lightItem.setSelected("light".equals(theme));
    }

    private void buildMenuBar() {
        // Language menu
        Menu languageMenu = new Menu(messages.msg("menu.language"));
        ToggleGroup langGroup = new ToggleGroup();

        RadioMenuItem englishItem = new RadioMenuItem(messages.msg("menu.language.english"));
        englishItem.getStyleClass().add("font-en");
        englishItem.setToggleGroup(langGroup);
        englishItem.setSelected(currentLocale.equals(Locale.ENGLISH));
        englishItem.setOnAction(e -> switchLocale(Locale.ENGLISH));

        RadioMenuItem georgianItem = new RadioMenuItem(messages.msg("menu.language.georgian"));
        georgianItem.getStyleClass().add("font-ka");
        georgianItem.setToggleGroup(langGroup);
        georgianItem.setSelected(currentLocale.equals(GEORGIAN));
        georgianItem.setOnAction(e -> switchLocale(GEORGIAN));

        languageMenu.getItems().addAll(englishItem, georgianItem);

        // Theme menu
        Menu themeMenu = new Menu(messages.msg("menu.theme"));
        ToggleGroup themeGroup = new ToggleGroup();
        String currentTheme = themeService.getCurrentTheme();

        darkItem = new RadioMenuItem(messages.msg("menu.theme.dark"));
        darkItem.setToggleGroup(themeGroup);
        darkItem.setSelected("dark".equals(currentTheme));
        darkItem.setOnAction(e -> switchTheme("dark"));

        lightItem = new RadioMenuItem(messages.msg("menu.theme.light"));
        lightItem.setToggleGroup(themeGroup);
        lightItem.setSelected("light".equals(currentTheme));
        lightItem.setOnAction(e -> switchTheme("light"));

        themeMenu.getItems().addAll(darkItem, lightItem);

        MenuBar menuBar = new MenuBar(languageMenu, themeMenu);
        rootPane.setTop(menuBar);
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