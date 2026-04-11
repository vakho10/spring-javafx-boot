# Auto-Configuration

Spring JavaFX Boot uses Spring Boot's auto-configuration mechanism to register all framework beans automatically when JavaFX is on the classpath.

## Activation

The auto-configuration is activated by `SpringJavaFxAutoConfiguration`, which is triggered by:

```java
@AutoConfiguration
@ConditionalOnClass(javafx.application.Application.class)
@EnableConfigurationProperties(FxViewProperties.class)
```

This means all beans are registered **only** when JavaFX is present on the classpath. The `@EnableConfigurationProperties` annotation binds `spring.javafx.view.*` properties to the `FxViewProperties` class.

## Registered Beans

| Bean | Type | Purpose |
|------|------|---------|
| `localizedMessageSource` | `LocalizedMessageSource` | Convenience wrapper for locale-aware i18n access |
| `fxViewResolver` | `ViewResolver` | Resolves view names to FXML templates |
| `fxRouteRegistry` | `FxRouteRegistry` | Scans `@FxRoutes` beans and builds the route table |
| `fxTitleService` | `FxTitleService` | Manages the primary window title |
| `fxRouter` | `FxRouter` | Central routing service (auto-injects all `FxRouteGuard` beans) |

## Overriding Beans

Every auto-configured bean uses `@ConditionalOnMissingBean`. To replace any component, define your own `@Bean` of the same type:

```java
@Configuration
public class CustomConfig {

    @Bean
    public ViewResolver fxViewResolver(MessageSource messageSource) {
        // Custom view resolver with different prefix
        return new ViewResolver(messageSource, "/views/", ".fxml");
    }
}
```

The auto-configuration will skip registering `fxViewResolver` since yours already exists.

## Bean Dependencies

```
FxRouter
├── FxRouteRegistry ← ApplicationContext
├── ViewResolver ← MessageSource, FxViewProperties
├── ApplicationContext
├── FxTitleService ← LocalizedMessageSource ← MessageSource
└── List<FxRouteGuard> ← all FxRouteGuard beans (global guards)
```

## Initialization Order

1. **Spring context starts** — auto-configuration registers all beans
2. **`FxRouteRegistry.@PostConstruct`** — scans for `@FxRoutes` beans, registers all `@FxMapping` methods, validates parent references, logs the route table
3. **`Application.start()`** — your code calls `router.setRootPane()` and `router.navigateTo()` to begin navigation

!!! warning "Required initialization"
    You must call `router.setRootPane(rootPane)` in `Application.start()` before any navigation. The router throws a `RoutingException` if this is not done.
