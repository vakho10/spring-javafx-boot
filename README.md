# 🚀 Spring JavaFX Boot

A desktop application template integrating **Spring Boot 4.0.4** with **JavaFX 25.0.2**. Spring manages the application context, dependency injection, and configuration while JavaFX handles the UI with FXML views and CSS styling.

## 🛠️ Tech Stack

| Component | Version |
|-----------|---------|
| Java | 25 |
| Spring Boot | 4.0.4 |
| JavaFX | 25.0.2 |
| Lombok | managed by Spring Boot |
| Maven | 3.x (wrapper included) |

## 📁 Project Structure

```
src/main/java/io/github/vakho10/springjavafxboot/
├── Launcher.java                  # JVM entry point — bypasses JavaFX module-path check
├── JavaFxApplication.java         # JavaFX Application — boots Spring, loads scene, builds menu bar
├── AppConfig.java                 # @SpringBootApplication config
├── controller/
│   ├── MainController.java        # FXML controller — Spring-managed @Controller (prototype)
│   └── SecondController.java      # Second view controller — navigation demo
├── navigation/
│   ├── MessageSourceResourceBundle.java  # Bridges Spring MessageSource → JavaFX ResourceBundle
│   └── ViewResolver.java          # Convention-based FXML template resolver
├── router/
│   ├── FxMapping.java             # @FxMapping — maps a method to a route path
│   ├── FxModel.java               # Model object carrying data from routes to controllers
│   ├── FxRouter.java              # Central routing service (analogous to DispatcherServlet)
│   ├── FxRouteRegistry.java       # Scans @FxRoutes beans and builds the route table at startup
│   ├── FxRoutes.java              # @FxRoutes — marks a class as a route configuration
│   ├── HandlerMethod.java         # Resolved reference to a @FxMapping method
│   ├── ModelAttribute.java        # @ModelAttribute — injects model data into controller fields
│   └── RoutingException.java      # Custom exception for routing errors
├── routes/
│   └── AppRoutes.java             # Application route definitions (@FxRoutes)
└── service/
    ├── ErrorHandler.java          # Global uncaught exception handler with themed alerts
    ├── LocalizedMessageSource.java # Convenience wrapper for locale-aware i18n access
    ├── ThemeService.java          # Manages theme & font stylesheets
    └── UserPreferencesService.java # Persists theme & locale via Java Preferences API

src/main/resources/
├── application.properties         # Spring Boot + view resolver configuration
├── messages.properties            # i18n messages (English — default)
├── messages_ka.properties         # i18n messages (Georgian)
├── css/
│   ├── styles.css                 # Structure + theme-aware colors via looked-up color variables
│   ├── fonts-en.css               # English font (Roboto)
│   ├── fonts-ka.css               # Georgian font (Noto Sans Georgian)
│   └── themes/
│       ├── dark.css               # 🌙 Dark color palette (looked-up color definitions)
│       └── light.css              # ☀️ Light color palette (looked-up color definitions)
├── fonts/
│   ├── roboto/                    # Roboto (Light, Regular, Medium, Bold)
│   └── noto-sans-georgian/        # Noto Sans Georgian (Light, Regular, Medium, SemiBold, Bold)
├── icons/
│   ├── app.ico                    # Application icon (jpackage / Windows)
│   └── app.png                    # Application icon (JavaFX window)
└── templates/
    ├── main.fxml                  # Main view layout
    └── second.fxml                # Second view layout
```

## ⚙️ How It Works

1. **`Launcher`** is the JVM entry point. It delegates to `JavaFxApplication.main()`. A plain class (not extending `Application`) is required because JavaFX performs a module-path check on `Application` subclasses that fails in classpath-based setups like Spring Boot.
2. **`JavaFxApplication`** extends `Application`. `init()` boots the Spring context, `start()` loads fonts, creates a `BorderPane` scene (menu bar at top, views swap in center), initializes the theme, builds the language/theme menu bar, and navigates to the initial route via `FxRouter`.
3. **`AppConfig`** is the `@SpringBootApplication` root — enables component scanning and auto-configuration.
4. **Controllers** are Spring `@Controller`s with `@Scope("prototype")` — each navigation creates a fresh instance with full access to `@Autowired`, `@Value`, and any other Spring features.
5. **Fonts** are loaded at startup via `Font.loadFont()` (JavaFX CSS does not support `@font-face`). Roboto is used for English, Noto Sans Georgian for Georgian — switched automatically via locale-specific CSS stylesheets. LCD subpixel smoothing is enabled for crisp rendering.

## 🧭 Routing

The project features a Spring MVC–inspired routing system with a clean separation between **route configuration** and **FXML view controllers**.

### Architecture

The routing system mirrors Spring MVC's request handling model, adapted for a desktop UI:

| Spring MVC | JavaFX Router | Role |
|------------|---------------|------|
| `DispatcherServlet` | `FxRouter` | Central dispatcher — orchestrates the navigation lifecycle |
| `RequestMappingHandlerMapping` | `FxRouteRegistry` | Scans and registers routes at startup |
| `@RestController` | `@FxRoutes` | Route configuration class (singleton) |
| `@GetMapping` | `@FxMapping` | Maps a method to a route path |
| `Model` | `FxModel` | Carries data from handler to view |
| `ViewResolver` | `ViewResolver` | Resolves view name → FXML template |
| — | `@ModelAttribute` | Injects model data into FXML controller fields |

### Route Configuration

Routes are defined in `@FxRoutes` classes — Spring-managed singletons responsible for preparing data and returning a view name:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/main")
    public String main(FxModel model) {
        model.put("greeting", "Hello!");
        return "main";  // → /templates/main.fxml
    }

    @FxMapping("/settings")
    public String settings(FxModel model) {
        model.put("activeTab", "appearance");
        return "settings";  // → /templates/settings.fxml
    }
}
```

### FXML Controllers

Controllers are prototype-scoped Spring beans focused purely on the view — they receive model data via `@ModelAttribute` and handle UI events:

```java
@Controller
@Scope("prototype")
@RequiredArgsConstructor
public class MainController {

    private final FxRouter router;

    @ModelAttribute
    private String greeting;  // injected from model before initialize()

    @FXML private Label greetingLabel;

    @FXML
    private void initialize() {
        if (greeting != null) {
            greetingLabel.setText(greeting);
        }
    }

    @FXML
    private void onGoToSettings() {
        router.navigateTo("/settings", Map.of("activeTab", "appearance"));
    }
}
```

### Navigation Lifecycle

When `router.navigateTo("/main")` is called:

1. `FxRouteRegistry` resolves the path to a `HandlerMethod`
2. A fresh `FxModel` is created and populated with navigation parameters
3. The `@FxMapping` handler on the `@FxRoutes` bean is invoked — it populates the model and returns a view name
4. `ViewResolver` resolves the view name to an FXML template (e.g. `"main"` → `/templates/main.fxml`)
5. The FXML is loaded with Spring's `ApplicationContext` as the controller factory (creating a fresh prototype controller)
6. `@ModelAttribute` fields are injected into the controller from the model
7. The optional `onModelReady(FxModel)` hook is called if the controller defines it
8. `@FXML initialize()` runs — all model data is available
9. The view is swapped into `rootPane.setCenter()`

### View Resolution

`ViewResolver` maps view names to FXML templates by convention. Prefix and suffix are configurable in `application.properties`:

```properties
spring.javafx.view.prefix=/templates/
spring.javafx.view.suffix=.fxml
```

Routes are logged at startup:
```
Mapped "/main"   → AppRoutes.main()
Mapped "/second" → AppRoutes.second()
Registered 2 FxMapping route(s)
```

## 🎨 Theming

The app separates structure from colors using layered CSS:

- **`styles.css`** — all selectors with structure + colors via JavaFX [looked-up colors](https://openjfx.io/javadoc/25/javafx.graphics/javafx/scene/doc-files/cssref.html#lookedupcolor) (`-app-bg`, `-app-surface`, `-app-text`, etc.)
- **`themes/dark.css`** — 🌙 defines color variables (based on [JavaFX-Dark-Theme](https://github.com/antoniopelusi/JavaFX-Dark-Theme))
- **`themes/light.css`** — ☀️ defines color variables

Theme switching is managed by `ThemeService` — it swaps the theme stylesheet at runtime without reloading the view. A **Theme** menu in the menu bar lets users toggle between dark and light. To add a new theme, create a CSS file in `css/themes/` defining the `-app-*` color variables and register it in `ThemeService`.

## 💾 User Preferences

Theme and locale choices are persisted via `UserPreferencesService` using Java's [Preferences API](https://docs.oracle.com/en/java/javase/25/docs/api/java.prefs/java/util/prefs/Preferences.html). Values are stored in the OS-native backing store (Windows Registry / macOS plist / Linux `~/.java`) and restored automatically on next launch.

## 🌍 Localization (i18n)

Uses Spring Boot's `MessageSource` bridged to JavaFX via `MessageSourceResourceBundle`:

- **Message files** — `messages.properties` (English) and `messages_ka.properties` (Georgian) in standard Spring Boot location
- **FXML `%key` syntax** — reference message keys directly in FXML:
  ```xml
  <Button text="%main.button.hello"/>
  ```
- **Programmatic access** — inject `LocalizedMessageSource` for convenient locale-aware access:
  ```java
  messages.msg("main.welcome");
  ```
- **Parameterized messages** — use `{0}`, `{1}`, etc. placeholders in message files:
  ```properties
  main.counter=Counter value: {0}
  ```
  ```java
  messages.msg("main.counter", counter);
  ```
- **Language menu** — built-in `MenuBar` with radio toggle between languages. Switching locale rebuilds the menu and calls `router.reload()` to reload the current view with new translations.

## 🚨 Error Handling

`ErrorHandler` registers a global uncaught exception handler on both the JavaFX Application Thread and background threads. When an unhandled exception occurs:

- A themed `Alert` dialog is shown with the error message
- An expandable **"Stack trace"** section reveals the full trace in a `TextArea`
- The dialog inherits the current theme stylesheets (dark/light)
- All dialog text is localized (title, header, details label)
- The exception is also logged via SLF4J

A `.button-danger` CSS class is available for destructive/error-related actions — it uses theme-aware color variables (`-app-danger`, `-app-danger-hover`, etc.).

## 📝 Logging

Logs are written to both the console and a file at `./logs/spring-javafx-boot.log` (relative to the working directory). Configured via `application.properties` with sensible desktop-app defaults:

- **Level**: `INFO` for both console and file
- **Rotation**: 10 MB per file, 7 days of history, 50 MB total cap
- **Archives**: compressed automatically (`*.gz`)

## 📋 Prerequisites

- **JDK 25+** on your PATH

JavaFX and all other dependencies are pulled automatically via Maven.

## ▶️ Running

**From IDE** — run `io.github.vakho10.springjavafxboot.Launcher` as the main class.

**From command line:**

```bash
./mvnw clean package
java -jar target/spring-javafx-boot-1.0-SNAPSHOT.jar
```

## 📦 Bundling a Native App Image

Build a self-contained executable with a bundled JRE (no Java installation required for end users):

```bash
./mvnw clean package -Pbundle
```

The output is in `target/dist/spring-javafx-boot/` — run the `.exe` directly.

## 🙏 Credits

- App icons from [icon-icons.com](https://icon-icons.com/) (free icons)