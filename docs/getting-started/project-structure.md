# Project Structure

Spring JavaFX Boot is a multi-module Maven project. Here's how it's organized:

```
spring-javafx-boot/
├── spring-javafx-boot-starter/        # Reusable library (published to Maven Central)
│   └── src/main/java/.../
│       ├── autoconfigure/             # Spring Boot auto-configuration
│       ├── navigation/                # View resolution & i18n bridging
│       ├── router/                    # Routing system (core)
│       └── service/                   # Title & message services
│
├── spring-javafx-boot-demo/           # Reference application
│   └── src/main/java/.../
│       ├── controller/                # FXML controllers
│       ├── routes/                    # Route definitions
│       └── service/                   # App-specific services
│   └── src/main/resources/
│       ├── templates/                 # FXML views
│       ├── css/                       # Stylesheets & themes
│       ├── fonts/                     # Custom fonts
│       └── messages*.properties       # i18n message files
│
└── spring-javafx-boot-archetype/      # Maven archetype for scaffolding
```

## Module Responsibilities

### Starter (`spring-javafx-boot-starter`)

The core library that you add as a dependency. Contains:

| Package | Contents |
|---------|----------|
| `autoconfigure` | `SpringJavaFxAutoConfiguration` — registers all framework beans |
| `navigation` | `ViewResolver` — resolves view names to FXML templates; `MessageSourceResourceBundle` — bridges Spring i18n to JavaFX |
| `router` | `FxRouter`, `FxRouteRegistry`, annotations (`@FxRoutes`, `@FxMapping`, `@ModelAttribute`, `@RouterOutlet`), `FxModel`, `WindowOptions`, `WindowResult` |
| `service` | `FxTitleService` — window title management; `LocalizedMessageSource` — convenient i18n access |

### Demo (`spring-javafx-boot-demo`)

A complete working application that demonstrates every feature of the starter. Use it as a reference for your own projects.

### Archetype (`spring-javafx-boot-archetype`)

A Maven archetype that generates a minimal project with routing, layout, and i18n pre-configured.

## Your Application Structure

A typical application built with Spring JavaFX Boot follows this layout:

```
my-javafx-app/
├── src/main/java/com/example/
│   ├── Launcher.java              # JVM entry point (plain class)
│   ├── JavaFxApplication.java     # Extends Application, boots Spring
│   ├── AppConfig.java             # @SpringBootApplication
│   ├── controller/                # FXML controllers (@Controller @Scope("prototype"))
│   ├── routes/                    # Route definitions (@FxRoutes)
│   └── service/                   # Application services
├── src/main/resources/
│   ├── application.properties     # Spring Boot + view resolver config
│   ├── messages.properties        # i18n messages (default locale)
│   ├── messages_xx.properties     # i18n messages (additional locales)
│   └── templates/                 # FXML view templates
│       ├── layout.fxml
│       ├── main.fxml
│       └── ...
└── pom.xml
```

!!! info "Controller scope"
    FXML controllers must be `@Controller @Scope("prototype")` — each navigation creates a fresh instance with full Spring dependency injection (`@Autowired`, `@Value`, etc.).
