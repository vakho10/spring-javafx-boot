# Windows & Modals

The router supports opening routes in **separate windows** — both modeless (independent) and modal (blocks the parent until closed).

## Modeless Windows

A modeless window opens independently. The parent window stays interactive:

```java
router.openWindow("/about");
```

With parameters and options:

```java
router.openWindow("/details", Map.of("id", 42), new WindowOptions()
    .title("Details")
    .size(600, 400));
```

## Modal Dialogs

A modal dialog blocks the parent window and returns a typed result:

```java
WindowResult<String> result = router.openModal("/picker",
    new WindowOptions()
        .title("Select Item")
        .size(400, 300)
        .resizable(false));

result.get().ifPresent(value -> label.setText(value));
```

The modal controller receives a `WindowResult` via `@ModelAttribute` and sets the result before closing:

```java
@Controller
@Scope("prototype")
public class PickerController {

    @ModelAttribute
    private WindowResult<String> windowResult;

    @FXML
    private TextField inputField;

    @FXML
    private void onConfirm() {
        windowResult.set(inputField.getText());
        closeWindow(inputField);
    }

    @FXML
    private void onCancel() {
        // Don't set windowResult — result.get() returns Optional.empty()
        closeWindow(inputField);
    }

    private void closeWindow(Node node) {
        node.getScene().getWindow().hide();
    }
}
```

## Route Definition

Window and modal routes are defined like any other route — they just don't need a `parent`:

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/demo/window")
    public String demoWindow(FxModel model) {
        return "demo-window";
    }

    @FxMapping("/demo/modal")
    public String demoModal(FxModel model) {
        return "demo-modal";
    }
}
```

## WindowOptions

`WindowOptions` is a builder for configuring the new window:

| Method | Description | Default |
|--------|-------------|---------|
| `.title(String)` | Window title | View name |
| `.size(double w, double h)` | Window dimensions | Determined by content |
| `.resizable(boolean)` | Allow resizing | `true` |

```java
new WindowOptions()
    .title("My Window")
    .size(600, 400)
    .resizable(false);
```

## WindowResult

`WindowResult<T>` is a typed container for modal return values:

- **Modal controller** calls `windowResult.set(value)` before closing
- **Caller** reads the result with `result.get()` which returns `Optional<T>`
- If the modal is closed without setting a result, `get()` returns `Optional.empty()`

## Stylesheet Inheritance

New windows automatically inherit all stylesheets from the parent scene (theme + fonts), so they match the application's current visual appearance.
