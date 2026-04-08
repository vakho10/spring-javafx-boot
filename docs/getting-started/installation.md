# Installation

## Prerequisites

- **JDK 17+** on your PATH

JavaFX and all other dependencies are pulled automatically via Maven.

## Adding the Dependency

Add the starter to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.vakho10</groupId>
    <artifactId>spring-javafx-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

The starter auto-configures all framework beans when JavaFX is on the classpath. No additional setup is required.

!!! tip "Override any component"
    Every auto-configured bean uses `@ConditionalOnMissingBean`. To replace any framework component, simply define your own `@Bean` of the same type.

## JavaFX Dependencies

The starter transitively pulls in `javafx-controls` and `javafx-fxml` (version 21.0.5). If your project already declares JavaFX dependencies, make sure the versions are compatible.

## Maven Wrapper

The project includes a Maven wrapper (`mvnw` / `mvnw.cmd`), so you don't need Maven installed globally. Use `./mvnw` (Linux/macOS) or `mvnw.cmd` (Windows) instead of `mvn`.
