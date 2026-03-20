package io.github.vakho10.springjavafxboot.service;

import io.github.vakho10.springjavafxboot.Main;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Persists user preferences (theme, locale) using the Java Preferences API.
 * Values are stored in the OS-native backing store (Windows Registry / macOS plist / Linux ~/.java).
 */
@Slf4j
@Service
public class UserPreferencesService {

    private static final String KEY_THEME = "theme";
    private static final String KEY_LOCALE = "locale";

    private static final String DEFAULT_THEME = "light";
    private static final String DEFAULT_LOCALE = "en";

    private final Preferences prefs = Preferences.userNodeForPackage(Main.class);

    public String getTheme() {
        return prefs.get(KEY_THEME, DEFAULT_THEME);
    }

    public void setTheme(String theme) {
        prefs.put(KEY_THEME, theme);
        flush();
    }

    public Locale getLocale() {
        return Locale.of(prefs.get(KEY_LOCALE, DEFAULT_LOCALE));
    }

    public void setLocale(Locale locale) {
        prefs.put(KEY_LOCALE, locale.getLanguage());
        flush();
    }

    private void flush() {
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            log.warn("Failed to flush user preferences", e);
        }
    }
}
