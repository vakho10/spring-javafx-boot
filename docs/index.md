# Spring JavaFX Boot

A **Spring Boot starter** for building JavaFX desktop applications with Spring MVC-inspired routing, theming, internationalization, and user preferences.

---

## Why Spring JavaFX Boot?

Building JavaFX applications with Spring Boot is powerful but often involves a lot of boilerplate — wiring up FXML loaders, managing navigation between views, handling theme switching, and bridging Spring's i18n with JavaFX. **Spring JavaFX Boot** solves this by providing a cohesive framework that brings familiar patterns from Spring MVC and Angular to the desktop.

### Key Features

- **Spring MVC-style routing** — Define routes with `@FxRoutes` and `@FxMapping`, just like `@RestController` and `@GetMapping`
- **Nested child routing** — Angular-inspired `@RouterOutlet` for composing layouts with swappable child views
- **Windows and modals** — Open routes in new windows or modal dialogs with typed return values
- **Theming** — Layered CSS with looked-up color variables for easy dark/light theme switching
- **Internationalization** — Spring `MessageSource` bridged to JavaFX's `ResourceBundle` for seamless i18n
- **User preferences** — Persist theme and locale choices via Java's Preferences API
- **Title management** — Declarative or programmatic window titles with i18n support
- **Auto-configuration** — Everything is auto-configured with `@ConditionalOnMissingBean` — override any component

### Tech Stack

| Component    | Version              |
|--------------|----------------------|
| Java         | 17+                  |
| Spring Boot  | 4.0.4                |
| JavaFX       | 21.0.5               |
| Lombok       | managed by Spring Boot |
| Maven        | 3.x (wrapper included) |

## Project Modules

The project consists of three modules:

| Module | Purpose |
|--------|---------|
| **`spring-javafx-boot-starter`** | Reusable library — add as a dependency to get routing, view resolution, i18n, and more |
| **`spring-javafx-boot-demo`** | Complete example application demonstrating all starter features |
| **`spring-javafx-boot-archetype`** | Maven archetype for generating a quickstart project with routing, layout, and i18n pre-configured |

## Quick Example

Define routes in a configuration class:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";  // loads /templates/layout.fxml
    }

    @FxMapping(value = "/main", parent = "/", title = "page.title.main")
    public String main(FxModel model) {
        model.put("greeting", "Hello!");
        return "main";  // rendered inside layout's @RouterOutlet
    }
}
```

Navigate between views:

```java
router.navigateTo("/main");
router.navigateTo("/settings", Map.of("tab", "appearance"));
```

Open modal dialogs with typed results:

```java
WindowResult<String> result = router.openModal("/picker",
    new WindowOptions().title("Select item").size(400, 300));
result.get().ifPresent(value -> label.setText(value));
```

## Getting Started

Head over to the [Installation](getting-started/installation.md) guide to add Spring JavaFX Boot to your project, or use the [Quickstart](getting-started/quickstart.md) to generate a ready-to-run project with the Maven archetype.
