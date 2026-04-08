# Quickstart

The fastest way to get started is with the **Maven archetype**, which generates a ready-to-run project with routing, layout, two views, and i18n pre-configured.

## Generate a Project

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.github.vakho10 \
  -DarchetypeArtifactId=spring-javafx-boot-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.example \
  -DartifactId=my-javafx-app
```

## What You Get

The generated project includes:

- **`Launcher`** + **`JavaFxApplication`** + **`AppConfig`** — minimal Spring Boot + JavaFX bootstrap
- Parent layout with a language menu (English / Georgian) and a `@RouterOutlet`
- Two child views with navigation between them and a counter demo
- i18n message files for both languages
- `FxTitleService` integration for localized window titles

No styling, fonts, themes, or preferences — just the essentials to start building.

## Run It

```bash
cd my-javafx-app
./mvnw clean package
java -jar target/my-javafx-app-1.0-SNAPSHOT.jar
```

## Manual Setup

If you prefer to set up manually instead of using the archetype, you need three classes:

### 1. Launcher (JVM Entry Point)

A plain class — **not** extending `Application`. This is required because JavaFX performs a module-path check on `Application` subclasses that fails in classpath-based setups like Spring Boot.

```java
public class Launcher {
    public static void main(String[] args) {
        JavaFxApplication.main(args);
    }
}
```

### 2. JavaFxApplication

Extends `Application`, boots the Spring context, and initializes the UI:

```java
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(String[]::new);
        springContext = SpringApplication.run(AppConfig.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Register Stage as a Spring bean
        springContext.getBeanFactory()
            .registerSingleton("primaryStage", primaryStage);

        // Build the scene
        BorderPane rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 800, 600);
        primaryStage.setScene(scene);

        // Initialize router and navigate
        FxRouter router = springContext.getBean(FxRouter.class);
        router.setRootPane(rootPane);
        router.navigateTo("/main");

        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}
```

### 3. AppConfig

```java
@SpringBootApplication
public class AppConfig {
}
```

### 4. Define Routes

```java
@FxRoutes
public class AppRoutes {

    @FxMapping("/")
    public String layout(FxModel model) {
        return "layout";
    }

    @FxMapping(value = "/main", parent = "/")
    public String main(FxModel model) {
        return "main";
    }
}
```

### 5. Create FXML Templates

Place templates in `src/main/resources/templates/`:

=== "layout.fxml"

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <?import javafx.scene.layout.BorderPane?>

    <BorderPane xmlns:fx="http://javafx.com/fxml"
                fx:controller="com.example.controller.LayoutController">
        <center>
            <BorderPane fx:id="routerOutlet"/>
        </center>
    </BorderPane>
    ```

=== "main.fxml"

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.layout.VBox?>

    <VBox xmlns:fx="http://javafx.com/fxml"
          fx:controller="com.example.controller.MainController"
          alignment="CENTER" spacing="10">
        <Label text="Hello, Spring JavaFX Boot!"/>
    </VBox>
    ```
