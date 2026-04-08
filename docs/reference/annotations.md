# Annotations

Complete reference for all annotations provided by Spring JavaFX Boot.

---

## @FxRoutes

Marks a class as a JavaFX route configuration. Scanned at startup by `FxRouteRegistry`.

| Property | Value |
|----------|-------|
| **Target** | `TYPE` (class) |
| **Retention** | `RUNTIME` |
| **Includes** | `@Component` — automatically a Spring-managed bean |

```java
@FxRoutes
public class AppRoutes {
    // @FxMapping methods go here
}
```

!!! note "Not an FXML controller"
    `@FxRoutes` classes are **singletons** responsible for preparing data and returning view names. FXML controllers are separate, prototype-scoped beans focused on the view lifecycle.

---

## @FxMapping

Maps a method to a named route path. When `FxRouter.navigateTo(path)` is called with a matching path, the annotated method is invoked.

| Property | Value |
|----------|-------|
| **Target** | `METHOD` |
| **Retention** | `RUNTIME` |

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | *(required)* | The route path (e.g., `"/main"`, `"/settings"`) |
| `parent` | `String` | `""` | Parent route path. When set, this route is a child — its view renders inside the parent's outlet |
| `title` | `String` | `""` | Window title to set on navigation. Resolved as i18n key first, falls back to literal string |

### Requirements

- Must be inside an `@FxRoutes` class
- Must return `String` (the view name)
- Parameters can be `FxModel` or `Map<String, Object>`

```java
@FxMapping(value = "/main", parent = "/", title = "page.title.main")
public String main(FxModel model) {
    model.put("greeting", "Hello!");
    return "main";
}
```

---

## @ModelAttribute

Marks a field in an FXML controller for injection from the `FxModel`. Injection happens **before** `@FXML initialize()` runs.

| Property | Value |
|----------|-------|
| **Target** | `FIELD` |
| **Retention** | `RUNTIME` |

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The model key. Defaults to the field name if empty |

```java
@Controller
@Scope("prototype")
public class DetailsController {

    @ModelAttribute
    private String greeting;  // key = "greeting" (matches field name)

    @ModelAttribute("itemId")
    private Integer id;  // key = "itemId" (explicit override)

    @FXML
    private void initialize() {
        // Both fields are already injected here
    }
}
```

---

## @RouterOutlet

Marks a `Pane` field in an FXML controller as the target where child route views are rendered.

| Property | Value |
|----------|-------|
| **Target** | `FIELD` |
| **Retention** | `RUNTIME` |

### Requirements

- The field must be a `Pane` (or subclass like `BorderPane`, `StackPane`, etc.)
- The field must also have `@FXML` so it's injected by the `FXMLLoader`

```java
@Controller
@Scope("prototype")
public class LayoutController {

    @FXML
    @RouterOutlet
    private BorderPane contentArea;
}
```

### Convention Alternative

Instead of `@RouterOutlet`, you can use `fx:id="routerOutlet"` in the FXML:

```xml
<BorderPane fx:id="routerOutlet"/>
```

The router checks `@RouterOutlet` first, then falls back to the `fx:id` convention.
