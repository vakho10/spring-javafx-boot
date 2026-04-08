# Configuration Properties

Spring JavaFX Boot is configured via standard Spring Boot `application.properties` (or `application.yml`). All properties are backed by `@ConfigurationProperties` classes, which means your IDE provides **autocompletion**, **hover documentation**, and **validation** out of the box.

## View Resolver Properties

Defined by `FxViewProperties` (`spring.javafx.view.*`):

| Property | Default | Description |
|----------|---------|-------------|
| `spring.javafx.view.prefix` | `/templates/` | Classpath prefix for FXML templates |
| `spring.javafx.view.suffix` | `.fxml` | File extension for FXML templates |

### Example

```properties
spring.javafx.view.prefix=/templates/
spring.javafx.view.suffix=.fxml
```

With these defaults, a view name `"main"` resolves to `/templates/main.fxml` on the classpath.

### Custom Template Location

To organize templates in subdirectories:

```properties
spring.javafx.view.prefix=/views/
```

Now `"main"` resolves to `/views/main.fxml`.

## Spring Boot i18n Properties

Standard Spring Boot message source properties are used for i18n:

| Property | Default | Description |
|----------|---------|-------------|
| `spring.messages.basename` | `messages` | Base name of message resource bundles |
| `spring.messages.encoding` | `UTF-8` | Message file encoding |

```properties
spring.messages.basename=messages
spring.messages.encoding=UTF-8
```

## Logging Configuration

The demo application configures logging for desktop use:

```properties
# Console logging
logging.level.root=INFO

# File logging
logging.file.name=./logs/spring-javafx-boot.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=7
logging.logback.rollingpolicy.total-size-cap=50MB
```

## Full Example

```properties
# View resolver
spring.javafx.view.prefix=/templates/
spring.javafx.view.suffix=.fxml

# i18n
spring.messages.basename=messages
spring.messages.encoding=UTF-8

# Logging
logging.level.root=INFO
logging.file.name=./logs/app.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=7
logging.logback.rollingpolicy.total-size-cap=50MB
```
