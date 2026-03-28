package io.github.vakho10.springjavafxboot.service;

import javafx.scene.Scene;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Manages visual appearance: theme stylesheets and locale-specific font stylesheets.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final UserPreferencesService preferencesService;

    private static final Map<String, String> THEME_STYLESHEETS = Map.of(
            "dark", "/css/themes/dark.css",
            "light", "/css/themes/light.css"
    );

    private static final Map<Locale, String> FONT_STYLESHEETS = Map.of(
            Locale.ENGLISH, "/css/fonts-en.css",
            new Locale("ka"), "/css/fonts-ka.css"
    );

    @Getter
    private String currentTheme;
    private Scene scene;

    public void init(Scene scene) {
        this.scene = scene;
        this.currentTheme = preferencesService.getTheme();
        applyThemeStylesheet();
    }

    public void switchTheme(String theme) {
        this.currentTheme = theme;
        preferencesService.setTheme(theme);
        applyThemeStylesheet();
    }

    public void applyFontStylesheet(Locale locale) {
        List<String> stylesheets = scene.getStylesheets();
        FONT_STYLESHEETS.values().stream()
                .map(this::toExternalForm)
                .forEach(stylesheets::remove);
        String fontCss = FONT_STYLESHEETS.get(locale);
        if (fontCss != null) {
            String url = toExternalForm(fontCss);
            if (url != null) {
                stylesheets.add(url);
            }
        }
    }

    private void applyThemeStylesheet() {
        List<String> stylesheets = scene.getStylesheets();
        THEME_STYLESHEETS.values().stream()
                .map(this::toExternalForm)
                .forEach(stylesheets::remove);
        String themeCss = THEME_STYLESHEETS.get(currentTheme);
        if (themeCss != null) {
            String url = toExternalForm(themeCss);
            if (url != null) {
                stylesheets.add(url);
            }
        }
    }

    private String toExternalForm(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            log.warn("Stylesheet not found: {}", path);
            return null;
        }
        return resource.toExternalForm();
    }
}
