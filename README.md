# 🚀 Spring JavaFX Boot

A **Spring Boot starter** for building JavaFX desktop applications with Spring MVC-inspired routing, theming, i18n, and user preferences. The project is split into three modules:

- **`spring-javafx-boot-starter`** — reusable library with routing, view resolution, theme switching, i18n bridging, and preferences persistence. Add it as a dependency to get started.
- **`spring-javafx-boot-demo`** — example application demonstrating all starter features.
- **`spring-javafx-boot-archetype`** — Maven archetype for generating a minimal quickstart project with routing, layout, and i18n out of the box.

> 📖 **[Full Documentation](https://vakho10.github.io/spring-javafx-boot/)** — guides, reference, and examples.

## 🛠️ Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17+ |
| Spring Boot | 4.0.4 |
| JavaFX | 21.0.5 |
| Lombok | managed by Spring Boot |
| Maven | 3.x (wrapper included) |

## 📁 Project Structure

```
spring-javafx-boot/                            # Parent POM (multi-module)
├── spring-javafx-boot-starter/                # Reusable starter library
│   └── src/main/java/.../
│       ├── autoconfigure/
│       │   └── SpringJavaFxAutoConfiguration.java  # Auto-configures all starter beans
│       ├── navigation/
│       │   ├── MessageSourceResourceBundle.java    # Bridges Spring MessageSource → JavaFX ResourceBundle
│       │   └── ViewResolver.java                   # Convention-based FXML template resolver
│       ├── router/
│       │   ├── ActiveRoute.java           # Cached state of a loaded route (view, controller, outlet)
│       │   ├── FxMapping.java             # @FxMapping — maps a method to a route path
│       │   ├── FxModel.java               # Model object carrying data from routes to controllers
│       │   ├── FxRouter.java              # Central routing service (analogous to DispatcherServlet)
│       │   ├── FxRouteRegistry.java       # Scans @FxRoutes beans and builds route table at startup
│       │   ├── FxRoutes.java              # @FxRoutes — marks a class as a route configuration
│       │   ├── HandlerMethod.java         # Resolved reference to a @FxMapping method
│       │   ├── ModelAttribute.java        # @ModelAttribute — injects model data into controller fields
│       │   ├── RouterOutlet.java          # @RouterOutlet — marks a Pane as the target for child views
│       │   ├── RoutingException.java      # Custom exception for routing errors
│       │   ├── WindowOptions.java         # Builder for window configuration (title, size, modality)
│       │   └── WindowResult.java          # Holds modal return value
│       └── service/
│           ├── FxTitleService.java       # Manages window title (i18n, format, route-driven)
│           └── LocalizedMessageSource.java # Convenience wrapper for locale-aware i18n access
│
├── spring-javafx-boot-demo/                   # Demo application
│   └── src/main/java/.../
│       ├── Launcher.java                  # JVM entry point
│       ├── JavaFxApplication.java         # JavaFX Application — boots Spring, loads scene
│       ├── AppConfig.java                 # @SpringBootApplication config
│       ├── controller/
│       │   ├── DemoModalController.java   # Modal dialog controller
│       │   ├── DemoWindowController.java  # Modeless window controller
│       │   ├── LayoutController.java      # Layout shell — menu bar + @RouterOutlet
│       │   ├── MainController.java        # Main view controller
│       │   └── SecondController.java      # Second view controller
│       ├── routes/
│       │   └── AppRoutes.java             # Application route definitions
│       └── service/
│           ├── ErrorHandler.java          # Global error handler with themed alerts
│           ├── ThemeService.java          # Manages theme & font stylesheets
│           └── UserPreferencesService.java # Persists theme & locale via Java Preferences API
│   └── src/main/resources/
│       ├── application.properties         # Spring Boot + view resolver configuration
│       ├── messages.properties            # i18n messages (English)
│       ├── messages_ka.properties         # i18n messages (Georgian)
│       ├── css/                           # Stylesheets + themes
│       ├── fonts/                         # Roboto + Noto Sans Georgian
│       ├── icons/                         # App icons
│       └── templates/                     # FXML views
```

## 🚀 Using the Starter

Add the starter dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.vakho10</groupId>
    <artifactId>spring-javafx-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

The starter auto-configures all framework beans when JavaFX is on the classpath. Configurable properties:

```properties
spring.javafx.view.prefix=/templates/   # FXML template location (default)
spring.javafx.view.suffix=.fxml         # FXML file extension (default)
```

Every auto-configured bean uses `@ConditionalOnMissingBean` — override any component by defining your own `@Bean`.

### Quickstart with the Archetype

Generate a ready-to-run project with routing, layout, two views, and i18n pre-configured:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.github.vakho10 \
  -DarchetypeArtifactId=spring-javafx-boot-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example \
  -DartifactId=my-javafx-app
```

The generated project includes:
- `Launcher` + `JavaFxApplication` + `AppConfig` — minimal Spring Boot + JavaFX bootstrap
- Parent layout with a language menu (English / Georgian) and a `@RouterOutlet`
- Two child views with navigation between them and a counter demo
- i18n message files for both languages
- `FxTitleService` integration for localized window titles

No styling, fonts, themes, or preferences — just the essentials to start building.

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
| — | `FxTitleService` | Manages window title (i18n-aware, format pattern, route-driven) |
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

    @FxMapping(value = "/main", parent = "/", title = "page.title.main")
    public String main(FxModel model) {
        model.put("greeting", "Hello!");
        return "main";  // rendered inside layout's outlet
    }

    @FxMapping(value = "/second", parent = "/", title = "page.title.second")
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

### Window Title (`FxTitleService`)

`FxTitleService` manages the primary window title — analogous to Angular's `Title` service. Titles can be set **declaratively** via the `title` attribute on `@FxMapping`, or **programmatically** from any controller.

**Declarative** — the `title` value is resolved as an i18n message key (falls back to literal string if no key is found). After navigation, the deepest route in the chain that declares a title wins:

```java
@FxMapping(value = "/main", parent = "/", title = "page.title.main")
```

**Programmatic** — inject `FxTitleService` for dynamic title updates:

```java
@Autowired private FxTitleService titleService;

titleService.setTitle("Dashboard");                      // literal
titleService.setTitle("page.detail.title", item.getName()); // i18n with args
```

**Title format** — a configurable pattern wraps every title so the app name appears consistently:

```java
titleService.setTitleFormat("%s — My App");
// navigating to "/main" (title = "Dashboard") → "Dashboard — My App"
```

Initialize the service in `Application.start()`:

```java
FxTitleService titleService = springContext.getBean(FxTitleService.class);
titleService.init(primaryStage);
titleService.setTitleFormat("%s — " + messages.msg("app.title"));
```

Titles are automatically re-resolved on `router.reload()` (e.g. after a locale switch), so the window title stays in sync with the current language.

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

- **`styles.css`** — all selectors with structure + colors via JavaFX [looked-up colors](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html#lookedupcolor) (`-app-bg`, `-app-surface`, `-app-text`, etc.)
- **`themes/dark.css`** — 🌙 defines color variables (based on [JavaFX-Dark-Theme](https://github.com/antoniopelusi/JavaFX-Dark-Theme))
- **`themes/light.css`** — ☀️ defines color variables

Theme switching is managed by `ThemeService` — it swaps the theme stylesheet at runtime without reloading the view. A **Theme** menu in the menu bar lets users toggle between dark and light. To add a new theme, create a CSS file in `css/themes/` defining the `-app-*` color variables and register it in `ThemeService`.

## 💾 User Preferences

Theme and locale choices are persisted via `UserPreferencesService` using Java's [Preferences API](https://docs.oracle.com/en/java/javase/17/docs/api/java.prefs/java/util/prefs/Preferences.html). Values are stored in the OS-native backing store (Windows Registry / macOS plist / Linux `~/.java`) and restored automatically on next launch.

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

- **JDK 17+** on your PATH

JavaFX and all other dependencies are pulled automatically via Maven.

## ▶️ Running the Demo

**From IDE** — run `io.github.vakho10.springjavafxboot.Launcher` in the `spring-javafx-boot-demo` module.

**From command line:**

```bash
./mvnw clean package
java -jar spring-javafx-boot-demo/target/spring-javafx-boot-demo-1.1.0-SNAPSHOT.jar
```

## 📦 Bundling a Native App Image

Build a self-contained executable with a bundled JRE (no Java installation required for end users):

```bash
./mvnw clean package -Pbundle -pl spring-javafx-boot-demo -am
```

The output is in `spring-javafx-boot-demo/target/dist/spring-javafx-boot-demo/` — run the `.exe` directly.

## 🚢 Publishing to Maven Central

The project is set up for automated publishing via GitHub Actions. When you push a version tag, the `release.yml` workflow builds, signs, and publishes the **starter** and **archetype** to Maven Central (the demo is excluded).

### One-time setup

1. **Verify your namespace** on the [Sonatype Central Portal](https://central.sonatype.com/):
   - Log in → Namespaces → Add `io.github.vakho10`
   - Sonatype will ask you to create a temporary repo (e.g. a specific repo name) to prove ownership — follow their instructions

2. **Generate a Central Portal token** (Central Portal → Account → Generate User Token) — this gives you a username/password pair

3. **Generate a GPG key** for artifact signing:
   ```bash
   gpg --full-generate-key          # RSA 4096, no expiry is fine
   gpg --list-secret-keys           # note the key ID
   gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
   gpg --armor --export-secret-keys <KEY_ID>   # copy this output
   ```

4. **Add GitHub repository secrets** (Settings → Secrets → Actions):

   | Secret | Value |
   |--------|-------|
   | `MAVEN_CENTRAL_USERNAME` | Token username from step 2 |
   | `MAVEN_CENTRAL_PASSWORD` | Token password from step 2 |
   | `GPG_PRIVATE_KEY` | Full armored private key from step 3 |
   | `GPG_PASSPHRASE` | Passphrase you set in step 3 |

### Releasing a version

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will:
1. Set all module versions to `1.0.0` (strips the `v` prefix)
2. Build with the `release` profile (attaches sources JAR, javadoc JAR, GPG signatures)
3. Publish to Maven Central via the Sonatype Central Publishing plugin
4. Create a GitHub Release with auto-generated release notes

After publishing (~10–30 min for Central to sync), users can simply add:
```xml
<dependency>
    <groupId>io.github.vakho10</groupId>
    <artifactId>spring-javafx-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🙏 Credits

- App icons from [icon-icons.com](https://icon-icons.com/) (free icons)