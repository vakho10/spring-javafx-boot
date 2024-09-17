package com.github.vakho10.spring_javafx_boot.application;

import com.github.vakho10.spring_javafx_boot.BootStarter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class BootJavaFXApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        // Programmatically create a Spring Boot context in the Application#init() method.
        this.context = new SpringApplicationBuilder()
                .sources(BootStarter.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        // Kick off application logic by sending a StageReadyEvent containing the primary Stage as payload.
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    /**
     * Support graceful shutdown for both Spring context and JavaFX platform.
     */
    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
