package io.github.vakho10.springjavafxboot.navigation;

import io.github.vakho10.springjavafxboot.service.ThemeService;
import io.github.vakho10.springjavafxboot.service.UserPreferencesService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class Navigator {

    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;
    private final MessageSource messageSource;
    private final MessageSourceResourceBundle resourceBundle;
    private final UserPreferencesService preferencesService;
    private final ThemeService themeService;

    private static final Locale GEORGIAN = Locale.of("ka");

    private Stage primaryStage;
    private BorderPane rootPane;

    @Getter
    private Locale currentLocale;
    private Class<?> currentController;

    // Keep references to theme menu items to avoid full rebuild on theme switch
    private RadioMenuItem darkItem;
    private RadioMenuItem lightItem;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.rootPane = (BorderPane) primaryStage.getScene().getRoot();

        // Restore saved preferences
        this.currentLocale = preferencesService.getLocale();

        // Initialize theme service with the scene
        themeService.init(primaryStage.getScene());
        themeService.applyFontStylesheet(currentLocale);

        buildMenuBar();
    }

    public void navigateTo(Class<?> controllerClass) {
        if (rootPane == null) {
            throw new NavigationException("Navigator not initialized — call setPrimaryStage() first");
        }
        try {
            FXMLLoader loader = new FXMLLoader(viewResolver.resolve(controllerClass));
            loader.setControllerFactory(applicationContext::getBean);
            resourceBundle.setLocale(currentLocale);
            loader.setResources(resourceBundle);
            Parent view = loader.load();
            rootPane.setCenter(view);
            currentController = controllerClass;
        } catch (IOException e) {
            throw new NavigationException("Failed to navigate to " + controllerClass.getSimpleName(), e);
        }
    }

    public void switchLocale(Locale locale) {
        this.currentLocale = locale;
        preferencesService.setLocale(locale);
        themeService.applyFontStylesheet(locale);
        buildMenuBar(); // Labels are localized, must rebuild
        if (currentController != null) {
            navigateTo(currentController);
        }
    }

    public void switchTheme(String theme) {
        themeService.switchTheme(theme);
        // Update radio selection without rebuilding the entire menu
        darkItem.setSelected("dark".equals(theme));
        lightItem.setSelected("light".equals(theme));
    }

    @PreDestroy
    private void cleanup() {
        primaryStage = null;
        rootPane = null;
        currentController = null;
    }

    private void buildMenuBar() {
        // Language menu
        Menu languageMenu = new Menu(msg("menu.language"));
        ToggleGroup langGroup = new ToggleGroup();

        RadioMenuItem englishItem = new RadioMenuItem(msg("menu.language.english"));
        englishItem.getStyleClass().add("font-en");
        englishItem.setToggleGroup(langGroup);
        englishItem.setSelected(currentLocale.equals(Locale.ENGLISH));
        englishItem.setOnAction(e -> switchLocale(Locale.ENGLISH));

        RadioMenuItem georgianItem = new RadioMenuItem(msg("menu.language.georgian"));
        georgianItem.getStyleClass().add("font-ka");
        georgianItem.setToggleGroup(langGroup);
        georgianItem.setSelected(currentLocale.equals(GEORGIAN));
        georgianItem.setOnAction(e -> switchLocale(GEORGIAN));

        languageMenu.getItems().addAll(englishItem, georgianItem);

        // Theme menu
        Menu themeMenu = new Menu(msg("menu.theme"));
        ToggleGroup themeGroup = new ToggleGroup();
        String currentTheme = themeService.getCurrentTheme();

        darkItem = new RadioMenuItem(msg("menu.theme.dark"));
        darkItem.setToggleGroup(themeGroup);
        darkItem.setSelected("dark".equals(currentTheme));
        darkItem.setOnAction(e -> switchTheme("dark"));

        lightItem = new RadioMenuItem(msg("menu.theme.light"));
        lightItem.setToggleGroup(themeGroup);
        lightItem.setSelected("light".equals(currentTheme));
        lightItem.setOnAction(e -> switchTheme("light"));

        themeMenu.getItems().addAll(darkItem, lightItem);

        MenuBar menuBar = new MenuBar(languageMenu, themeMenu);
        rootPane.setTop(menuBar);
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, currentLocale);
    }
}
