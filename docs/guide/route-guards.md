# Route Guards

Route guards let you **prevent or confirm navigation** — for example, blocking navigation away from a form with unsaved changes, or restricting access to authenticated users.

## How Guards Work

Before every navigation, `FxRouter` runs a guard check:

1. **Controller `canDeactivate`** — if the current controller implements `FxRouteGuard`, its `canDeactivate()` is called
2. **Global `canDeactivate`** — all global guard beans are checked
3. **Global `canActivate`** — all global guard beans are checked for the target route

If **any** guard returns `false`, navigation is cancelled and the current view stays in place.

## The FxRouteGuard Interface

```java
public interface FxRouteGuard {

    default boolean canDeactivate(String currentPath, String targetPath) {
        return true;
    }

    default boolean canActivate(String targetPath, Map<String, Object> params) {
        return true;
    }
}
```

Both methods are `default`, so you only need to override the one you care about.

## Controller Guards

Any FXML controller can implement `FxRouteGuard` to protect its own route. The most common use case is confirming unsaved changes:

```java
@Controller
@Scope("prototype")
public class FormController implements FxRouteGuard {

    private boolean hasUnsavedChanges = false;

    @Override
    public boolean canDeactivate(String currentPath, String targetPath) {
        if (hasUnsavedChanges) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "You have unsaved changes. Discard?");
            return alert.showAndWait()
                    .filter(r -> r == ButtonType.OK)
                    .isPresent();
        }
        return true;
    }

    @FXML
    private void onTextChanged() {
        hasUnsavedChanges = true;
    }

    @FXML
    private void onSave() {
        // ... save logic
        hasUnsavedChanges = false;
    }
}
```

!!! note "Scope matters"
    Controller guards are checked on the **currently active** controller instance. Since controllers are `@Scope("prototype")`, each navigation creates a fresh instance — the guard state lives with the view.

## Global Guards

Global guards are Spring beans that implement `FxRouteGuard`. They are checked on **every** navigation, making them ideal for cross-cutting concerns like authentication:

```java
@Component
public class AuthGuard implements FxRouteGuard {

    private final AuthService authService;

    public AuthGuard(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean canActivate(String targetPath, Map<String, Object> params) {
        if (authService.isLoggedIn() || targetPath.equals("/login")) {
            return true;
        }
        // Redirect to login instead of silently blocking
        Platform.runLater(() -> router.navigateTo("/login"));
        return false;
    }
}
```

Global guards are automatically discovered — any Spring bean implementing `FxRouteGuard` is registered as a global guard. The auto-configuration collects all `FxRouteGuard` beans and passes them to `FxRouter`.

!!! tip "Controller vs Global"
    - Use a **controller guard** when the logic is specific to one view (e.g. unsaved changes)
    - Use a **global guard** when the logic applies across routes (e.g. authentication, logging)

## Guard Execution Order

```
navigateTo("/target")
  │
  ├─ 1. Active controller's canDeactivate(current, target)
  │     └─ blocked? → navigation cancelled
  │
  ├─ 2. Global guards' canDeactivate(current, target)
  │     └─ any blocked? → navigation cancelled
  │
  └─ 3. Global guards' canActivate(target, params)
        └─ any blocked? → navigation cancelled
        
  ✓ All passed → proceed with navigation
```

## Overriding the Default

Since `FxRouter` is registered with `@ConditionalOnMissingBean`, you can provide your own `FxRouter` bean if you need to customize guard behavior beyond what the default implementation offers.
