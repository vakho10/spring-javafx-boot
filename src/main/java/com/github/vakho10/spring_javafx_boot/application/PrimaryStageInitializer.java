package com.github.vakho10.spring_javafx_boot.application;

import com.github.vakho10.spring_javafx_boot.controller.MainWindow;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    @Autowired
    public PrimaryStageInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    /**
     * Consume StageReadyEvent, which contains the applications primary stage as payload.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.stage;
        // Use FxWeaver to obtain a View based on the @FxmlView annotation found in MainController
        Scene scene = new Scene(fxWeaver.loadView(MainWindow.class), 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
