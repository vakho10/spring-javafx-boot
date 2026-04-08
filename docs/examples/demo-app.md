# Demo Application

The `spring-javafx-boot-demo` module is a complete reference application that demonstrates every feature of the starter. Use it as a learning resource and starting point for your own projects.

## Running the Demo

**From IDE** — run `io.github.vakho10.springjavafxboot.Launcher` in the `spring-javafx-boot-demo` module.

**From command line:**

```bash
./mvnw clean package
java -jar spring-javafx-boot-demo/target/spring-javafx-boot-demo-1.1.0-SNAPSHOT.jar
```

## Features Demonstrated

### Route Configuration

`AppRoutes` defines five routes:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";            // Shell with menu bar + outlet
    }

    @FxMapping(value = "/main", parent = "/", title = "page.title.main")
    public String main(FxModel model) {
        return "main";              // Main child view
    }

    @FxMapping(value = "/second", parent = "/", title = "page.title.second")
    public String second(FxModel model) {
        return "second";            // Second child view
    }

    @FxMapping("/demo/window")
    public String demoWindow(FxModel model) {
        return "demo-window";       // Modeless window
    }

    @FxMapping("/demo/modal")
    public String demoModal(FxModel model) {
        return "demo-modal";        // Modal dialog
    }
}
```

### Layout with Router Outlet

`LayoutController` owns the application shell — a menu bar with language and theme toggles, and a `@RouterOutlet` where child views render:

```java
@Controller
@Scope("prototype")
public class LayoutController {

    @FXML
    @RouterOutlet
    private BorderPane contentArea;

    @FXML
    private MenuBar menuBar;
}
```

### Theme Switching

The `ThemeService` manages dark/light themes. The menu bar includes a Theme toggle that calls:

```java
themeService.switchTheme("dark");  // or "light"
```

Themes are layered CSS files using looked-up color variables.

### Language Switching

The menu bar includes language radio buttons (English / Georgian). Switching locale:

1. Updates `Locale.setDefault()`
2. Updates `LocalizedMessageSource`
3. Saves the choice via `UserPreferencesService`
4. Applies locale-specific font stylesheet
5. Calls `router.reload()` to rebuild views with new translations

### Windows and Modals

The main view has buttons that demonstrate:

- **Modeless window** — `router.openWindow("/demo/window", ...)`
- **Modal dialog** — `router.openModal("/demo/modal", ...)` with a `WindowResult<String>` return value

### Error Handling

`ErrorHandler` registers a global uncaught exception handler. When an unhandled exception occurs:

- A themed `Alert` dialog shows the error message
- An expandable "Stack trace" section reveals the full trace
- The dialog inherits the current theme
- All text is localized

### Preferences Persistence

`UserPreferencesService` persists theme and locale via Java's Preferences API. On next launch, the saved choices are restored automatically.

### Custom Fonts

Fonts are loaded at startup via `Font.loadFont()`:

- **Roboto** — English text
- **Noto Sans Georgian** — Georgian text

Font selection is managed by locale-specific CSS stylesheets, switched automatically when the language changes.

## Bundling a Native App Image

Build a self-contained executable with a bundled JRE (no Java installation required for end users):

```bash
./mvnw clean package -Pbundle -pl spring-javafx-boot-demo -am
```

The output is in `spring-javafx-boot-demo/target/dist/spring-javafx-boot-demo/` — run the `.exe` directly.
