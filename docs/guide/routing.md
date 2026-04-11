# Routing

Spring JavaFX Boot features a **Spring MVC-inspired routing system** that cleanly separates route configuration from FXML view controllers. If you've worked with Spring MVC or Angular, the patterns will feel familiar.

## Architecture Overview

The routing system mirrors Spring MVC's request handling model, adapted for a desktop UI:

| Spring MVC               | Spring JavaFX Boot  | Role                                              |
|--------------------------|---------------------|----------------------------------------------------|
| `DispatcherServlet`      | `FxRouter`          | Central dispatcher — orchestrates navigation       |
| `RequestMappingHandlerMapping` | `FxRouteRegistry` | Scans and registers routes at startup        |
| `@RestController`        | `@FxRoutes`         | Route configuration class (singleton)              |
| `@GetMapping`            | `@FxMapping`        | Maps a method to a route path                      |
| `Model`                  | `FxModel`           | Carries data from handler to view                  |
| `ViewResolver`           | `ViewResolver`      | Resolves view name to FXML template                |

## Defining Routes

Routes are defined in classes annotated with `@FxRoutes`. Each method annotated with `@FxMapping` maps a path to a view:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";  // resolves to /templates/layout.fxml
    }

    @FxMapping(value = "/main", parent = "/", title = "page.title.main")
    public String main(FxModel model) {
        model.put("greeting", "Hello!");
        return "main";  // resolves to /templates/main.fxml
    }

    @FxMapping(value = "/second", parent = "/", title = "page.title.second")
    public String second(FxModel model) {
        return "second";
    }
}
```

!!! note "Return type"
    `@FxMapping` methods must return a `String` (the view name). They can accept `FxModel`, `Map<String, Object>`, or `@PathVariable` parameters.

## Navigating

Inject `FxRouter` into any Spring-managed bean and call `navigateTo()`:

```java
@Controller
@Scope("prototype")
@RequiredArgsConstructor
public class MainController {

    private final FxRouter router;

    @FXML
    private void onGoToSecond() {
        router.navigateTo("/second");
    }
}
```

### Passing Parameters

Pass data to the target view via a parameter map:

```java
router.navigateTo("/details", Map.of("itemId", 42, "mode", "edit"));
```

Parameters are added to the `FxModel` and injected into the target controller's `@ModelAttribute` fields:

```java
@Controller
@Scope("prototype")
public class DetailsController {

    @ModelAttribute
    private Integer itemId;

    @ModelAttribute
    private String mode;

    @FXML
    private void initialize() {
        // itemId = 42, mode = "edit"
    }
}
```

### Path Variables

Routes can include `{name}` placeholders that are extracted from the navigation path:

```java
@FxMapping(value = "/detail/{id}", parent = "/")
public String detail(@PathVariable("id") Long id, FxModel model) {
    model.put("item", itemService.findById(id));
    return "detail";
}
```

Navigate using a concrete path:

```java
router.navigateTo("/detail/42");
```

The router matches `/detail/42` against the `/detail/{id}` pattern, extracts `id = 42`, converts it to the declared type (`Long`), and passes it to the handler. Path variables are also added to the `FxModel` automatically, so they're available via `@ModelAttribute` in controllers.

Exact routes always take priority over parameterized routes. Supported `@PathVariable` types: `String`, `Integer`, `Long`, `Double`, `Boolean`.

## View Resolution

`ViewResolver` maps logical view names to FXML templates using a configurable prefix and suffix:

```
"main"     → /templates/main.fxml
"settings" → /templates/settings.fxml
```

Configure in `application.properties`:

```properties
spring.javafx.view.prefix=/templates/   # default
spring.javafx.view.suffix=.fxml         # default
```

## Route Logging

Routes are logged at startup so you can verify the route table:

```
Mapped "/"            → AppRoutes.layout()
Mapped "/main"        → AppRoutes.main()        [parent: /]
Mapped "/second"      → AppRoutes.second()       [parent: /]
Mapped "/demo/window" → AppRoutes.demoWindow()
Mapped "/demo/modal"  → AppRoutes.demoModal()
Registered 5 FxMapping route(s)
```

## Navigation Lifecycle

When `router.navigateTo("/main")` is called:

1. `FxRouteRegistry` resolves `"/main"` and finds `parent = "/"`
2. [Route guards](route-guards.md) are checked — if any guard returns `false`, navigation is cancelled
3. The router builds the ancestor chain: `["/", "/main"]`
4. For each level in the chain, starting from the root:
    - If the parent `"/"` is **already active** — reuse its cached layout
    - If not — invoke the `@FxMapping` handler, load the FXML, cache as `ActiveRoute`
5. The handler is invoked — it populates `FxModel` and returns a view name
6. `ViewResolver` resolves the view name to an FXML template path
7. The FXML is loaded with Spring's `ApplicationContext` as the controller factory
8. `@ModelAttribute` fields are injected into the controller from the model
9. The optional `onModelReady(FxModel)` hook is called (if defined on the controller)
10. `@FXML initialize()` runs — all model data is available
11. The child view is placed into the parent's `@RouterOutlet`

## Route Guards

You can prevent or confirm navigation using **route guards**. Controllers can implement `FxRouteGuard` to block navigation away (e.g. unsaved changes), and global guard beans can restrict access to routes (e.g. authentication).

See the [Route Guards](route-guards.md) guide for full details.

## Reloading

Call `router.reload()` to force a full reload of the current route and all its parents. This is useful after a locale change:

```java
Locale.setDefault(newLocale);
messages.setLocale(newLocale);
router.reload();  // rebuilds all layouts with new translations
```
