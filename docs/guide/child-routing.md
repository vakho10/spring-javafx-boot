# Child Routing & Layouts

Spring JavaFX Boot supports **Angular-style nested child routing**. Parent routes define layout templates with a designated outlet area where child views are rendered. When navigating between siblings, only the child view is swapped — the parent layout stays in place.

## How It Works

The `parent` attribute on `@FxMapping` establishes the hierarchy:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";  // parent layout with menu bar + outlet
    }

    @FxMapping(value = "/main", parent = "/")
    public String main(FxModel model) {
        return "main";  // rendered inside layout's outlet
    }

    @FxMapping(value = "/second", parent = "/")
    public String second(FxModel model) {
        return "second";  // also rendered inside layout's outlet
    }
}
```

When navigating from `/main` to `/second`, the router detects that the parent `/` layout is already active and **reuses it** — only the child view inside the outlet is swapped.

## Defining a Router Outlet

The parent's FXML controller declares an outlet — a `Pane` where child views render. There are two ways to identify it:

### Option 1: `@RouterOutlet` Annotation (Explicit)

```java
@Controller
@Scope("prototype")
public class LayoutController {

    @FXML
    @RouterOutlet
    private BorderPane contentArea;  // child views render here

    @FXML
    private MenuBar menuBar;
}
```

### Option 2: `fx:id="routerOutlet"` Convention

No annotation needed — just name the pane `routerOutlet` in the FXML:

```xml
<BorderPane xmlns:fx="http://javafx.com/fxml"
            fx:controller="com.example.controller.LayoutController">
    <top>
        <MenuBar fx:id="menuBar"/>
    </top>
    <center>
        <BorderPane fx:id="routerOutlet"/>
    </center>
</BorderPane>
```

!!! tip
    Both approaches are supported simultaneously. The router first checks for `@RouterOutlet`, then falls back to `fx:id="routerOutlet"`.

## Outlet Placement

The outlet can be any `Pane` subclass. The router places child views differently depending on the type:

- **`BorderPane`** — child is set as the `center` content
- **Any other `Pane`** — child replaces all children via `getChildren().setAll(view)`

## Nested Layouts

Nesting is unlimited. A child route can itself be a parent with its own outlet:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String appShell(FxModel model) {
        return "app-shell";  // top-level shell with sidebar + outlet
    }

    @FxMapping(value = "/settings", parent = "/")
    public String settingsLayout(FxModel model) {
        return "settings-layout";  // settings shell with tabs + outlet
    }

    @FxMapping(value = "/settings/appearance", parent = "/settings")
    public String appearance(FxModel model) {
        return "appearance";  // rendered inside settings-layout's outlet
    }

    @FxMapping(value = "/settings/account", parent = "/settings")
    public String account(FxModel model) {
        return "account";  // also inside settings-layout's outlet
    }
}
```

This creates a three-level hierarchy:

```
app-shell (/)
└── settings-layout (/settings)
    ├── appearance (/settings/appearance)
    └── account (/settings/account)
```

## Route Chain Resolution

When navigating to a deeply nested route, the router automatically builds the full ancestor chain and processes each level:

1. **Already active parents are reused** — the router caches each loaded layout as an `ActiveRoute`
2. **Only changed levels are reloaded** — navigating between siblings at the same depth only swaps the leaf view
3. **Circular references are detected** — the router throws a `RoutingException` if a route's parent chain forms a cycle

!!! warning "Circular parent references"
    The router validates parent chains at navigation time. If `A → B → A` is detected, a `RoutingException` is thrown immediately.
