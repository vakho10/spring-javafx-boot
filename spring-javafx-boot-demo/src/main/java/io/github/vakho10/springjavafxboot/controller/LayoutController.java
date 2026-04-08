package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.annotation.RouterOutlet;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import io.github.vakho10.springjavafxboot.service.ThemeService;
import io.github.vakho10.springjavafxboot.service.UserPreferencesService;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Locale;

/**
 * Layout controller — defines the application shell (menu bar + content outlet).
 * <p>
 * This is the parent route's controller. Child routes ("/main", "/second")
 * render inside the {@link #contentArea} outlet. The menu bar with
 * language and theme toggles lives here, not in {@code JavaFxApplication}.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class LayoutController {

    private static final Locale GEORGIAN = new Locale("ka");

    private final FxRouter router;
    private final LocalizedMessageSource messages;
    private final ThemeService themeService;
    private final UserPreferencesService preferencesService;

    @FXML
    @RouterOutlet
    private BorderPane contentArea;

    @FXML
    private MenuBar menuBar;

    private RadioMenuItem darkItem;
    private RadioMenuItem lightItem;

    @FXML
    private void initialize() {
        buildMenuBar();
    }

    private void switchLocale(Locale locale) {
        Locale.setDefault(locale);
        messages.setLocale(locale);
        preferencesService.setLocale(locale);
        themeService.applyFontStylesheet(locale);
        router.reload(); // Reloads layout + current child with new translations
    }

    private void switchTheme(String theme) {
        themeService.switchTheme(theme);
        darkItem.setSelected("dark".equals(theme));
        lightItem.setSelected("light".equals(theme));
    }

    private void buildMenuBar() {
        Locale currentLocale = preferencesService.getLocale();

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

        menuBar.getMenus().setAll(languageMenu, themeMenu);
    }
}