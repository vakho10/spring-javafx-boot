# User Preferences

Spring JavaFX Boot persists user preferences (theme, locale) using Java's [Preferences API](https://docs.oracle.com/en/java/javase/17/docs/api/java.prefs/java/util/prefs/Preferences.html). Values are stored in the OS-native backing store and restored automatically on next launch.

## Storage Location

The Preferences API stores data in the platform-native location:

| OS | Storage |
|----|---------|
| Windows | Windows Registry |
| macOS | plist files |
| Linux | `~/.java/.userPrefs/` |

## UserPreferencesService

The demo application includes a `UserPreferencesService` that wraps the Preferences API:

```java
@Service
public class UserPreferencesService {

    private static final String KEY_THEME = "theme";
    private static final String KEY_LOCALE = "locale";

    private static final String DEFAULT_THEME = "dark";
    private static final String DEFAULT_LOCALE = "en";

    private final Preferences prefs =
        Preferences.userNodeForPackage(AppConfig.class);

    public String getTheme() {
        return prefs.get(KEY_THEME, DEFAULT_THEME);
    }

    public void setTheme(String theme) {
        prefs.put(KEY_THEME, theme);
        flush();
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(
            prefs.get(KEY_LOCALE, DEFAULT_LOCALE));
    }

    public void setLocale(Locale locale) {
        prefs.put(KEY_LOCALE, locale.toLanguageTag());
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
```

## Restoring Preferences on Launch

In `Application.start()`, restore saved preferences before initializing the UI:

```java
UserPreferencesService prefs = springContext.getBean(UserPreferencesService.class);
LocalizedMessageSource messages = springContext.getBean(LocalizedMessageSource.class);

// Restore locale
Locale savedLocale = prefs.getLocale();
Locale.setDefault(savedLocale);
messages.setLocale(savedLocale);

// Restore theme (after scene is created)
ThemeService themeService = springContext.getBean(ThemeService.class);
themeService.init(scene);  // loads theme from preferences automatically
```

## Adding Custom Preferences

To persist additional settings (e.g., window size, sidebar state), add new key-value pairs to your preferences service:

```java
private static final String KEY_WINDOW_WIDTH = "window.width";
private static final String KEY_WINDOW_HEIGHT = "window.height";

public double getWindowWidth() {
    return prefs.getDouble(KEY_WINDOW_WIDTH, 800.0);
}

public void setWindowWidth(double width) {
    prefs.putDouble(KEY_WINDOW_WIDTH, width);
    flush();
}
```

!!! note
    `UserPreferencesService` is part of the demo application, not the starter library. Copy and adapt it for your own project. The Preferences API node is tied to a class (via `Preferences.userNodeForPackage()`), so use your own `@SpringBootApplication` class to keep preferences scoped to your app.
