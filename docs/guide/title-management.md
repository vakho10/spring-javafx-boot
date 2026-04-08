# Title Management

`FxTitleService` manages the primary window title — analogous to Angular's `Title` service. Titles can be set declaratively on route definitions or programmatically from any controller.

## Declarative Titles

Use the `title` attribute on `@FxMapping` to set the window title automatically when a route is navigated to:

```java
@FxMapping(value = "/main", parent = "/", title = "page.title.main")
public String main(FxModel model) {
    return "main";
}
```

The `title` value is resolved as an **i18n message key** first. If no matching message is found, it falls back to the literal string.

When multiple routes in the ancestor chain declare titles, the **deepest route** wins.

## Programmatic Titles

Inject `FxTitleService` for dynamic title updates:

```java
@Autowired
private FxTitleService titleService;

// Literal title
titleService.setTitle("Dashboard");

// i18n with arguments
titleService.setTitle("page.detail.title", item.getName());
```

## Title Format

A configurable format pattern wraps every title so the application name appears consistently:

```java
titleService.setTitleFormat("%s — My App");
```

With this format:

- Navigating to a route with title "Dashboard" sets the window title to **"Dashboard — My App"**
- Calling `titleService.setTitle("Settings")` sets it to **"Settings — My App"**

### Typical Initialization

Set up the title service in `Application.start()`:

```java
FxTitleService titleService = springContext.getBean(FxTitleService.class);
titleService.init(primaryStage);
titleService.setTitleFormat("%s — " + messages.msg("app.title"));
```

## Locale-Aware Titles

Titles are automatically re-resolved when `router.reload()` is called (e.g., after a locale switch). If your route title is an i18n key like `"page.title.main"`, the window title updates to the new language automatically.

## API Summary

| Method | Description |
|--------|-------------|
| `init(Stage)` | Binds the service to the primary stage. Call before any title updates. |
| `setTitleFormat(String)` | Sets the format pattern (use `%s` for the page title placeholder). |
| `getTitleFormat()` | Returns the current format pattern. |
| `setTitle(String, Object...)` | Sets the title. Resolves as i18n key first, falls back to literal. |
| `getTitle()` | Returns the current window title string. |
