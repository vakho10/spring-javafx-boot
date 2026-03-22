#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.service.FxTitleService;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

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
        // Register Stage as a bean for late-created components (e.g. prototype controllers)
        springContext.getBeanFactory().registerSingleton("primaryStage", primaryStage);

        LocalizedMessageSource messages = springContext.getBean(LocalizedMessageSource.class);

        // Build scene
        BorderPane rootPane = new BorderPane();
        Scene scene = new Scene(rootPane, 800, 600);
        primaryStage.setScene(scene);

        // Initialize title service with app-name format: "Page — App Name"
        FxTitleService titleService = springContext.getBean(FxTitleService.class);
        titleService.init(primaryStage);
        titleService.setTitleFormat("%s \u2014 " + messages.msg("app.title"));

        // Initialize router and navigate to initial child route.
        // This loads the "/" layout (with menu bar) then "/main" inside its outlet.
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
