# FxDemo — Spring Boot + JavaFX

A desktop application template integrating **Spring Boot 4.0.4** with **JavaFX 25.0.2**. Spring manages the application context, dependency injection, and configuration while JavaFX handles the UI with FXML views and CSS styling.

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 25 |
| Spring Boot | 4.0.4 |
| JavaFX | 25.0.2 |
| Lombok | managed by Spring Boot |
| Maven | 3.x (wrapper included) |

## Project Structure

```
src/main/java/com/example/fxdemo/
├── Launcher.java                  # JVM entry point — bypasses JavaFX module-path check
├── Main.java                      # JavaFX Application — boots Spring, loads FXML scene
├── AppConfig.java                 # @SpringBootApplication config
└── controller/
    └── MainController.java        # FXML controller, Spring-managed @Component

src/main/resources/
├── application.properties         # Spring Boot configuration
├── css/
│   └── styles.css                 # Global JavaFX stylesheet (fonts, sizing)
├── fonts/
│   ├── Roboto-*.ttf               # Roboto (Regular, Bold, Italic, BoldItalic)
│   └── NotoSansGeorgian-*.ttf     # Noto Sans Georgian (Light, Regular, Medium, SemiBold, Bold)
└── templates/
    └── main.fxml                  # Main view layout
```

## How It Works

1. **`Launcher`** is the JVM entry point. It delegates to `Main.main()`. A plain class (not extending `Application`) is required because JavaFX performs a module-path check on `Application` subclasses that fails in classpath-based setups like Spring Boot.
2. **`Main`** extends `Application`. `init()` boots the Spring context, `start()` loads fonts, the FXML view, and applies the CSS stylesheet. Controllers are wired via `springContext::getBean`.
3. **`AppConfig`** is the `@SpringBootApplication` root — enables component scanning and auto-configuration.
4. **Controllers** are Spring `@Component`s with full access to `@Autowired`, `@Value`, and any other Spring features.
5. **Fonts** are loaded at startup via `Font.loadFont()` (JavaFX CSS does not support `@font-face`) and referenced globally in `styles.css`. LCD subpixel smoothing is enabled for crisp rendering.

## Prerequisites

- **JDK 25+** on your PATH

JavaFX and all other dependencies are pulled automatically via Maven.

## Running

**From IDE** — run `com.example.fxdemo.Launcher` as the main class.

**From command line:**

```bash
./mvnw clean package
java -jar target/fxdemo-1.0-SNAPSHOT.jar
```
