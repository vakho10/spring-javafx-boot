package io.github.vakho10.springjavafxboot.navigation;

import io.github.vakho10.springjavafxboot.service.UserPreferencesService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Navigator {

    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;
    private final MessageSource messageSource;
    private final MessageSourceResourceBundle resourceBundle;
    private final UserPreferencesService preferencesService;
    private Stage primaryStage;
    private BorderPane rootPane;

    private static final Locale GEORGIAN = Locale.of("ka");
    private static final Map<Locale, String> FONT_STYLESHEETS = Map.of(
            Locale.ENGLISH, "/css/fonts-en.css",
            GEORGIAN, "/css/fonts-ka.css"
    );

    private static final String DARK_THEME = "/css/themes/dark.css";
    private static final String LIGHT_THEME = "/css/themes/light.css";
    private static final Map<String, String> THEME_STYLESHEETS = Map.of(
            "light", LIGHT_THEME,
            "dark", DARK_THEME
    );

    @Getter
    private Locale currentLocale;
    @Getter
    private String currentTheme;
    private Class<?> currentController;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.rootPane = (BorderPane) primaryStage.getScene().getRoot();

        // Restore saved preferences
        this.currentTheme = preferencesService.getTheme();
        this.currentLocale = preferencesService.getLocale();

        applyFontStylesheet();
        applyThemeStylesheet();
        buildMenuBar();
    }

    public void navigateTo(Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(viewResolver.resolve(controllerClass));
            loader.setControllerFactory(applicationContext::getBean);
            resourceBundle.setLocale(currentLocale);
            loader.setResources(resourceBundle);
            Parent view = loader.load();
            rootPane.setCenter(view);
            currentController = controllerClass;
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to " + controllerClass.getSimpleName(), e);
        }
    }

    public void switchLocale(Locale locale) {
        this.currentLocale = locale;
        preferencesService.setLocale(locale);
        applyFontStylesheet();
        buildMenuBar();
        if (currentController != null) {
            navigateTo(currentController);
        }
    }

    public void switchTheme(String theme) {
        this.currentTheme = theme;
        preferencesService.setTheme(theme);
        applyThemeStylesheet();
        buildMenuBar();
    }

    private void applyFontStylesheet() {
        List<String> stylesheets = primaryStage.getScene().getStylesheets();
        FONT_STYLESHEETS.values().stream()
                .map(path -> getClass().getResource(path).toExternalForm())
                .forEach(stylesheets::remove);
        String fontCss = FONT_STYLESHEETS.get(currentLocale);
        if (fontCss != null) {
            stylesheets.add(getClass().getResource(fontCss).toExternalForm());
        }
    }

    private void applyThemeStylesheet() {
        List<String> stylesheets = primaryStage.getScene().getStylesheets();
        THEME_STYLESHEETS.values().stream()
                .map(path -> getClass().getResource(path).toExternalForm())
                .forEach(stylesheets::remove);
        String themeCss = THEME_STYLESHEETS.get(currentTheme);
        if (themeCss != null) {
            stylesheets.add(getClass().getResource(themeCss).toExternalForm());
        }
    }

    private void buildMenuBar() {
        // Language menu
        Menu languageMenu = new Menu(messageSource.getMessage("menu.language", null, currentLocale));

        ToggleGroup langGroup = new ToggleGroup();

        RadioMenuItem englishItem = new RadioMenuItem(messageSource.getMessage("menu.language.english", null, currentLocale));
        englishItem.setStyle("-fx-font-family: 'Roboto'");
        englishItem.setToggleGroup(langGroup);
        englishItem.setSelected(currentLocale.equals(Locale.ENGLISH));
        englishItem.setOnAction(e -> switchLocale(Locale.ENGLISH));

        RadioMenuItem georgianItem = new RadioMenuItem(messageSource.getMessage("menu.language.georgian", null, currentLocale));
        georgianItem.setStyle("-fx-font-family: 'Noto Sans Georgian'");
        georgianItem.setToggleGroup(langGroup);
        georgianItem.setSelected(currentLocale.equals(GEORGIAN));
        georgianItem.setOnAction(e -> switchLocale(GEORGIAN));

        languageMenu.getItems().addAll(englishItem, georgianItem);

        // Theme menu
        Menu themeMenu = new Menu(messageSource.getMessage("menu.theme", null, currentLocale));

        ToggleGroup themeGroup = new ToggleGroup();

        RadioMenuItem darkItem = new RadioMenuItem(messageSource.getMessage("menu.theme.dark", null, currentLocale));
        darkItem.setToggleGroup(themeGroup);
        darkItem.setSelected("dark".equals(currentTheme));
        darkItem.setOnAction(e -> switchTheme("dark"));

        RadioMenuItem lightItem = new RadioMenuItem(messageSource.getMessage("menu.theme.light", null, currentLocale));
        lightItem.setToggleGroup(themeGroup);
        lightItem.setSelected("light".equals(currentTheme));
        lightItem.setOnAction(e -> switchTheme("light"));

        themeMenu.getItems().addAll(darkItem, lightItem);

        MenuBar menuBar = new MenuBar(languageMenu, themeMenu);
        rootPane.setTop(menuBar);
    }
}
