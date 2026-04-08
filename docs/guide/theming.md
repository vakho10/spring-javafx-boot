# Theming

Spring JavaFX Boot uses a **layered CSS architecture** that separates structure from colors, making it easy to add or switch themes at runtime.

## CSS Architecture

The theming system uses three layers:

| Layer | File | Purpose |
|-------|------|---------|
| **Structure** | `css/styles.css` | All selectors with layout rules and colors via looked-up color variables |
| **Dark theme** | `css/themes/dark.css` | Defines color variable values for dark mode |
| **Light theme** | `css/themes/light.css` | Defines color variable values for light mode |

### Looked-Up Colors

The structural stylesheet references colors indirectly via JavaFX [looked-up colors](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html#lookedupcolor):

```css
/* styles.css — references variables, not literal colors */
.root {
    -fx-background-color: -app-bg;
}

.label {
    -fx-text-fill: -app-text;
}

.button {
    -fx-background-color: -app-surface;
}
```

Theme stylesheets define the actual values:

```css
/* themes/dark.css */
.root {
    -app-bg: #1e1e1e;
    -app-surface: #2d2d2d;
    -app-text: #e0e0e0;
    -app-primary: #6c9bd2;
    -app-danger: #e06c75;
    /* ... */
}
```

```css
/* themes/light.css */
.root {
    -app-bg: #f5f5f5;
    -app-surface: #ffffff;
    -app-text: #333333;
    -app-primary: #1976d2;
    -app-danger: #d32f2f;
    /* ... */
}
```

## ThemeService

`ThemeService` manages theme switching at runtime. It swaps the theme stylesheet on the scene without reloading the view:

```java
@Autowired
private ThemeService themeService;

// Switch theme
themeService.switchTheme("light");
themeService.switchTheme("dark");

// Get current theme
String current = themeService.getCurrentTheme();
```

The theme service must be initialized with the scene in `Application.start()`:

```java
ThemeService themeService = springContext.getBean(ThemeService.class);
themeService.init(scene);
```

## Adding a New Theme

1. Create a new CSS file in `css/themes/` (e.g., `solarized.css`)
2. Define all `-app-*` color variables in `.root`
3. Register it in `ThemeService` by adding an entry to the `THEME_STYLESHEETS` map

## Font Stylesheets

The theme service also manages locale-specific font stylesheets. Different languages may use different fonts for optimal readability:

```java
// Apply font stylesheet for the current locale
themeService.applyFontStylesheet(Locale.ENGLISH);  // loads fonts-en.css
themeService.applyFontStylesheet(new Locale("ka")); // loads fonts-ka.css
```

## Danger Button Style

A `.button-danger` CSS class is available for destructive actions. It uses theme-aware color variables (`-app-danger`, `-app-danger-hover`) so it adapts to the current theme automatically.

!!! tip "Windows and modals"
    New windows and modal dialogs automatically inherit stylesheets from the parent scene, so they always match the current theme.
