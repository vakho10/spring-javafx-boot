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
├── JavaFxApplication.java         # JavaFX Application — boots Spring, loads FXML scene
├── AppConfig.java                 # @SpringBootApplication config
├── controller/
│   ├── MainController.java        # FXML controller, Spring-managed @Component
│   └── SecondController.java      # Second view controller — navigation demo
├── navigation/
│   ├── MessageSourceResourceBundle.java  # Bridges Spring MessageSource → JavaFX ResourceBundle
│   ├── NavigationException.java   # Custom exception for navigation errors
│   ├── Navigator.java             # Service for navigation & language switching
│   └── ViewResolver.java          # Convention-based FXML template resolver
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
2. **`JavaFxApplication`** extends `Application`. `init()` boots the Spring context, `start()` loads fonts, creates a `BorderPane` scene (menu bar at top, views swap in center), and applies base + theme CSS stylesheets.
3. **`AppConfig`** is the `@SpringBootApplication` root — enables component scanning and auto-configuration.
4. **Controllers** are Spring `@Component`s with full access to `@Autowired`, `@Value`, and any other Spring features.
5. **Fonts** are loaded at startup via `Font.loadFont()` (JavaFX CSS does not support `@font-face`). Roboto is used for English, Noto Sans Georgian for Georgian — switched automatically via locale-specific CSS stylesheets. LCD subpixel smoothing is enabled for crisp rendering.

## 🧭 Navigation

The project includes a Spring MVC–inspired navigation system:

- **`ViewResolver`** maps controller classes to FXML templates by naming convention (e.g. `MainController` → `/templates/main.fxml`). Prefix and suffix are configurable in `application.properties`:
  ```properties
  spring.javafx.view.prefix=/templates/
  spring.javafx.view.suffix=.fxml
  ```
- **`Navigator`** is a Spring `@Service` that swaps the scene root. Inject it into any controller and call:
  ```java
  navigator.navigateTo(SecondController.class);
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
- **Language menu** — built-in `MenuBar` with radio toggle between languages, managed by `Navigator`. Switching locale rebuilds the menu and reloads the current view.

## 🚨 Error Handling

`ErrorHandler` registers a global uncaught exception handler on both the JavaFX Application Thread and background threads. When an unhandled exception occurs:

- A themed `Alert` dialog is shown with the error message
- An expandable **"Stack trace"** section reveals the full trace in a `TextArea`
- The dialog inherits the current theme stylesheets (dark/light)
- All dialog text is localized (title, header, details label)
- The exception is also logged via SLF4J

A `.button-danger` CSS class is available for destructive/error-related actions — it uses theme-aware color variables (`-app-danger`, `-app-danger-hover`, etc.).

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
