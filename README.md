# FxDemo — Spring Boot + JavaFX

A demo application that integrates **Spring Boot** with **JavaFX**, allowing you to build desktop GUI applications with full Spring dependency injection, component scanning, and configuration support.

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.4**
- **JavaFX 25.0.2**
- **Lombok**
- **Maven** (with Maven Wrapper)

## Project Structure

```
src/main/java/com/example/fxdemo/
├── Launcher.java              # JVM entry point (avoids JavaFX module-path issues)
├── Main.java                  # JavaFX Application — boots Spring, loads FXML
├── AppConfig.java             # @SpringBootApplication config
└── controller/
    └── MainController.java    # FXML controller, managed by Spring

src/main/resources/
├── application.properties     # Spring Boot config
└── templates/
    └── main.fxml              # Main UI layout
```

## How It Works

1. **`Launcher`** is the JVM entry point. It delegates to `Main.main()`. Using a plain class (not extending `Application`) avoids JavaFX's module-path check that fails in classpath-based setups.
2. **`Main`** extends `javafx.application.Application`. In `init()`, it boots the Spring context. In `start()`, it loads the FXML and wires controllers through Spring's `getBean()`.
3. **`AppConfig`** is the `@SpringBootApplication` class that enables component scanning and auto-configuration.
4. **Controllers** (e.g., `MainController`) are Spring `@Component`s, so they support `@Autowired`, `@Value`, and all other Spring features.

## Running the App

### From IDE

Run `com.example.fxdemo.Launcher` as the main class.

### From Maven

```bash
./mvnw javafx:run
```

## Prerequisites

- **JDK 25+** installed and on your PATH
- No additional JavaFX SDK installation needed — dependencies are pulled via Maven

## Future Plans

<!-- TODO: Update this section as the project evolves -->
- Database integration
- Multiple views / navigation
- Packaging as a native installer
