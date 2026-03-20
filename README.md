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
├── Main.java                      # JavaFX Application — boots Spring, loads FXML scene
├── AppConfig.java                 # @SpringBootApplication config
├── controller/
│   ├── MainController.java        # FXML controller, Spring-managed @Component
│   └── SecondController.java      # Second view controller — navigation demo
└── navigation/
    ├── MessageSourceResourceBundle.java  # Bridges Spring MessageSource → JavaFX ResourceBundle
    ├── Navigator.java             # Service for navigating between views (+ language menu)
    └── ViewResolver.java          # Convention-based FXML template resolver

src/main/resources/
├── application.properties         # Spring Boot + view resolver configuration
├── messages.properties            # i18n messages (English — default)
├── messages_ka.properties         # i18n messages (Georgian)
├── css/
│   ├── styles.css                 # Global JavaFX stylesheet (sizing, smoothing)
│   ├── fonts-en.css               # English font (Roboto)
│   └── fonts-ka.css               # Georgian font (Noto Sans Georgian)
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

1. **`Launcher`** is the JVM entry point. It delegates to `Main.main()`. A plain class (not extending `Application`) is required because JavaFX performs a module-path check on `Application` subclasses that fails in classpath-based setups like Spring Boot.
2. **`Main`** extends `Application`. `init()` boots the Spring context, `start()` loads fonts, creates a `BorderPane` scene (menu bar at top, views swap in center), and applies the CSS stylesheet.
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

## 🌍 Localization (i18n)

Uses Spring Boot's `MessageSource` bridged to JavaFX via `MessageSourceResourceBundle`:

- **Message files** — `messages.properties` (English) and `messages_ka.properties` (Georgian) in standard Spring Boot location
- **FXML `%key` syntax** — reference message keys directly in FXML:
  ```xml
  <Button text="%main.button.hello"/>
  ```
- **Programmatic access** — use `MessageSource` in controllers:
  ```java
  messageSource.getMessage("main.welcome", null, navigator.getCurrentLocale());
  ```
- **Language menu** — built-in `MenuBar` with radio toggle between languages, managed by `Navigator`. Switching locale rebuilds the menu and reloads the current view.

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
