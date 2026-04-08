# Spring JavaFX Boot

A **Spring Boot starter** for building JavaFX desktop applications with Spring MVC-inspired routing, i18n, and window management.

> **[Full Documentation](https://vakho10.github.io/spring-javafx-boot/)** — guides, reference, and examples.

## Features

- **Spring MVC-style routing** — `@FxRoutes` + `@FxMapping`, just like `@RestController` + `@GetMapping`
- **Nested child routing** — Angular-inspired `@RouterOutlet` for layouts with swappable child views
- **Windows and modals** — Open routes in new windows or modal dialogs with typed return values
- **Internationalization** — Spring `MessageSource` bridged to JavaFX's `ResourceBundle`
- **Title management** — Declarative or programmatic i18n-aware window titles
- **Auto-configuration** — Everything wired up automatically, override any component with `@ConditionalOnMissingBean`

## Tech Stack

| Component    | Version                |
|--------------|------------------------|
| Java         | 17+                    |
| Spring Boot  | 4.0.4                  |
| JavaFX       | 21.0.5                 |
| Lombok       | managed by Spring Boot |
| Maven        | 3.x (wrapper included) |

## Quick Start

### Add the dependency

```xml
<dependency>
    <groupId>io.github.vakho10</groupId>
    <artifactId>spring-javafx-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Or generate a project with the archetype

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.github.vakho10 \
  -DarchetypeArtifactId=spring-javafx-boot-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example \
  -DartifactId=my-javafx-app
```

### Define routes

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";
    }

    @FxMapping(value = "/main", parent = "/", title = "page.title.main")
    public String main(FxModel model) {
        model.put("greeting", "Hello!");
        return "main";
    }
}
```

### Navigate

```java
router.navigateTo("/main");
router.navigateTo("/settings", Map.of("tab", "appearance"));

// Modal with typed result
WindowResult<String> result = router.openModal("/picker",
    new WindowOptions().title("Select item").size(400, 300));
result.get().ifPresent(value -> label.setText(value));
```

See the [Quickstart guide](https://vakho10.github.io/spring-javafx-boot/getting-started/quickstart/) for a full walkthrough.

## Project Modules

| Module | Purpose |
|--------|---------|
| **`spring-javafx-boot-starter`** | Core library — routing, view resolution, i18n bridging, title management |
| **`spring-javafx-boot-demo`** | Reference app demonstrating all features including theming, preferences, and error handling |
| **`spring-javafx-boot-archetype`** | Maven archetype for scaffolding new projects |

## Running the Demo

```bash
./mvnw clean package
java -jar spring-javafx-boot-demo/target/spring-javafx-boot-demo-1.1.0-SNAPSHOT.jar
```

Or run `io.github.vakho10.springjavafxboot.Launcher` directly from your IDE.

## Documentation

| Section | Topics |
|---------|--------|
| [Getting Started](https://vakho10.github.io/spring-javafx-boot/getting-started/installation/) | Installation, quickstart, project structure |
| [Guide](https://vakho10.github.io/spring-javafx-boot/guide/routing/) | Routing, child routing, windows & modals, i18n, title management |
| [Reference](https://vakho10.github.io/spring-javafx-boot/reference/annotations/) | Annotations, configuration properties, auto-configuration |
| [Examples](https://vakho10.github.io/spring-javafx-boot/examples/demo-app/) | Demo application walkthrough |

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup, release process, and Maven Central publishing instructions.

## Credits

- Dark theme based on [JavaFX-Dark-Theme](https://github.com/antoniopelusi/JavaFX-Dark-Theme) by antoniopelusi
- App icons from [icon-icons.com](https://icon-icons.com/) (free icons)

## License

This project is licensed under the [MIT License](LICENSE).
