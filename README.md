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
├── JavaFxApplication.java         # JavaFX Application — boots Spring, loads scene, navigates to initial route
├── AppConfig.java                 # @SpringBootApplication config
├── controller/
│   ├── DemoModalController.java   # Modal dialog controller — returns value via WindowResult
│   ├── DemoWindowController.java  # Modeless window controller
│   ├── LayoutController.java      # Layout shell — menu bar + @RouterOutlet for child views
│   ├── MainController.java        # FXML controller — Spring-managed @Controller (prototype)
│   └── SecondController.java      # Second view controller — navigation demo
├── navigation/
│   ├── MessageSourceResourceBundle.java  # Bridges Spring MessageSource → JavaFX ResourceBundle
│   └── ViewResolver.java          # Convention-based FXML template resolver
├── router/
│   ├── ActiveRoute.java           # Cached state of a loaded route (view, controller, outlet)
│   ├── FxMapping.java             # @FxMapping — maps a method to a route path (with optional parent)
│   ├── FxModel.java               # Model object carrying data from routes to controllers
│   ├── FxRouter.java              # Central routing service (analogous to DispatcherServlet)
│   ├── FxRouteRegistry.java       # Scans @FxRoutes beans and builds the route table at startup
│   ├── FxRoutes.java              # @FxRoutes — marks a class as a route configuration
│   ├── HandlerMethod.java         # Resolved reference to a @FxMapping method + parent relationship
│   ├── ModelAttribute.java        # @ModelAttribute — injects model data into controller fields
│   ├── RouterOutlet.java          # @RouterOutlet — marks a Pane as the target for child views
│   ├── RoutingException.java      # Custom exception for routing errors
│   ├── WindowOptions.java         # Builder for window configuration (title, size, modality)
│   └── WindowResult.java          # Holds modal return value (set by modal, read by caller)
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
    ├── demo-modal.fxml            # Modal dialog view
    ├── demo-window.fxml           # Modeless window view
    ├── layout.fxml                # Application shell (menu bar + router outlet)
    ├── main.fxml                  # Main view layout
    └── second.fxml                # Second view layout
```

## ⚙️ How It Works

1. **`Launcher`** is the JVM entry point. It delegates to `JavaFxApplication.main()`. A plain class (not extending `Application`) is required because JavaFX performs a module-path check on `Application` subclasses that fails in classpath-based setups like Spring Boot.
2. **`JavaFxApplication`** extends `Application`. `init()` boots the Spring context, `start()` loads fonts, initializes the theme, registers the `Stage` as a Spring bean, and calls `router.navigateTo("/main")` — which automatically loads the parent layout first, then the child view inside it.
3. **`AppConfig`** is the `@SpringBootApplication` root — enables component scanning and auto-configuration.
4. **`LayoutController`** owns the application shell — the menu bar with language and theme toggles. It declares a `@RouterOutlet` where child views render. This keeps layout concerns out of `JavaFxApplication`.
5. **Controllers** are Spring `@Controller`s with `@Scope("prototype")` — each navigation creates a fresh instance with full access to `@Autowired`, `@Value`, and any other Spring features.
6. **Fonts** are loaded at startup via `Font.loadFont()` (JavaFX CSS does not support `@font-face`). Roboto is used for English, Noto Sans Georgian for Georgian — switched automatically via locale-specific CSS stylesheets. LCD subpixel smoothing is enabled for crisp rendering.

## 🧭 Routing

The project features a Spring MVC–inspired routing system with Angular-style nested child routing and a clean separation between **route configuration** and **FXML view controllers**.

### Architecture

The routing system mirrors Spring MVC's request handling model, adapted for a desktop UI:

| Spring MVC | JavaFX Router | Role |
|------------|---------------|------|
| `DispatcherServlet` | `FxRouter` | Central dispatcher — orchestrates the navigation lifecycle |
| `RequestMappingHandlerMapping` | `FxRouteRegistry` | Scans and registers routes at startup |
| `@RestController` | `@FxRoutes` | Route configuration class (singleton) |
| `@GetMapping` | `@FxMapping` | Maps a method to a route path (with optional `parent`) |
| `Model` | `FxModel` | Carries data from handler to view |
| `ViewResolver` | `ViewResolver` | Resolves view name → FXML template |
| — | `@ModelAttribute` | Injects model data into FXML controller fields |
| — | `@RouterOutlet` | Marks a pane as the target for child views |
| — | `ActiveRoute` | Caches loaded parent layouts for reuse |
| — | `WindowOptions` | Builder for window/modal configuration (title, size, modality) |
| — | `WindowResult` | Holds modal return value (set by modal controller, read by caller) |

### Route Configuration

Routes are defined in `@FxRoutes` classes. The `parent` attribute on `@FxMapping` establishes the hierarchy — child views render inside the parent's `@RouterOutlet`:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";  // → /templates/layout.fxml (menu bar + outlet)
    }

    @FxMapping(value = "/main", parent = "/")
    public String main(FxModel model) {
        model.put("greeting", "Hello!");
        return "main";  // rendered inside layout's outlet
    }

    @FxMapping(value = "/second", parent = "/")
    public String second(FxModel model) {
        return "second";  // rendered inside layout's outlet
    }
}
```

### Child Routing & Layouts

Parent routes define layout templates with a `@RouterOutlet` where child views render — similar to Angular's `<router-outlet>`:

```java
@Controller
@Scope("prototype")
public class LayoutController {

    @FXML
    @RouterOutlet
    private BorderPane contentArea;  // child views render here

    @FXML
    private MenuBar menuBar;

    @FXML
    private void initialize() {
        // build menu bar...
    }
}
```

The corresponding FXML:
```xml
<BorderPane fx:controller="...LayoutController">
    <top>
        <MenuBar fx:id="menuBar"/>
    </top>
    <center>
        <BorderPane fx:id="contentArea"/>
    </center>
</BorderPane>
```

The outlet can be identified in two ways (both are supported):
- **`@RouterOutlet`** annotation on a controller field (explicit)
- **`fx:id="routerOutlet"`** in the FXML (convention-based)

Nesting is unlimited — a child route can itself be a parent with its own outlet (parent → child → grandchild).

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
    private void onGoToSecond() {
        router.navigateTo("/second");
    }
}
```

### Navigation Lifecycle

When `router.navigateTo("/main")` is called:

1. `FxRouteRegistry` resolves `"/main"` and sees `parent = "/"`
2. The router builds the ancestor chain: `["/", "/main"]`
3. For each level in the chain, starting from the root:
  - If the parent `"/"` is **already active** → reuse its layout (menu bar stays)
  - If not → invoke the `@FxMapping` handler, load the FXML, cache as `ActiveRoute`
4. The handler is invoked — it populates the `FxModel` and returns a view name
5. `ViewResolver` resolves the view name to an FXML template
6. The FXML is loaded with Spring's `ApplicationContext` as the controller factory
7. `@ModelAttribute` fields are injected into the controller from the model
8. The optional `onModelReady(FxModel)` hook is called if the controller defines it
9. `@FXML initialize()` runs — all model data is available
10. The child view is placed into the parent's `@RouterOutlet`

When navigating from `"/main"` to `"/second"`, the router detects that the parent `"/"` layout is already active and **reuses it** — only the child view is swapped in the outlet.

### View Resolution

`ViewResolver` maps view names to FXML templates by convention. Prefix and suffix are configurable in `application.properties`:

```properties
spring.javafx.view.prefix=/templates/
spring.javafx.view.suffix=.fxml
```

Routes are logged at startup:
```
Mapped "/"            → AppRoutes.layout()
Mapped "/main"        → AppRoutes.main()        [parent: /]
Mapped "/second"      → AppRoutes.second()       [parent: /]
Mapped "/demo/window" → AppRoutes.demoWindow()
Mapped "/demo/modal"  → AppRoutes.demoModal()
Registered 5 FxMapping route(s)
```

### Windows & Modal Dialogs

The router supports opening routes in separate windows — both **modeless** (independent) and **modal** (blocks parent until closed).

**Modeless window** — opens independently, parent stays interactive:
```java
router.openWindow("/demo/window", new WindowOptions()
    .title("Demo Window")
    .size(400, 300));
```

**Modal dialog** — blocks the parent and returns a result via `WindowResult`:
```java
WindowResult<String> result = router.openModal("/demo/modal", new WindowOptions()
    .title("Demo Modal")
    .size(400, 250)
    .resizable(false));

result.get().ifPresent(value -> label.setText(value));
```

The modal's FXML controller receives a `WindowResult` via `@ModelAttribute` and sets the result before closing:

```java
@ModelAttribute
private WindowResult<String> windowResult;

@FXML
private void onConfirm() {
    windowResult.set(inputField.getText());
    closeWindow(inputField);
}
```

Window and modal routes are defined like any other route in `@FxRoutes` — they just don't need a `parent`:

```java
@FxMapping("/demo/window")
public String demoWindow(FxModel model) {
    return "demo-window";
}

@FxMapping("/demo/modal")
public String demoModal(FxModel model) {
    return "demo-modal";
}
```

New windows inherit the parent's stylesheets (theme + fonts) automatically.

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
- **Language menu** — built-in `MenuBar` in `LayoutController` with radio toggle between languages. Switching locale calls `router.reload()` which rebuilds the layout and reloads the current child view with new translations.

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