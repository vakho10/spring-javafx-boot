package io.github.vakho10.springjavafxboot.service;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Global error handler that shows themed, localized error alerts
 * with expandable stack trace details.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorHandler {

    private final MessageSource messageSource;

    private Stage primaryStage;
    private Supplier<Locale> localeSupplier;

    public void init(Stage primaryStage, Supplier<Locale> localeSupplier) {
        this.primaryStage = primaryStage;
        this.localeSupplier = localeSupplier;

        // Catch uncaught exceptions on the JavaFX Application Thread
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> showError(e));

        // Catch uncaught exceptions on all other threads
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
                Platform.runLater(() -> showError(e)));
    }

    public void showError(Throwable throwable) {
        log.error("Uncaught exception", throwable);

        Locale locale = localeSupplier != null ? localeSupplier.get() : Locale.ENGLISH;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(msg("error.title", locale));
        alert.setHeaderText(msg("error.header", locale));
        alert.setContentText(throwable.getMessage());

        // Expandable stack trace
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));

        Label detailsLabel = new Label(msg("error.details", locale));
        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(detailsLabel, 0, 0);
        content.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(content);

        // Apply current theme stylesheets to the alert dialog
        if (primaryStage != null && primaryStage.getScene() != null) {
            Scene ownerScene = primaryStage.getScene();
            alert.getDialogPane().getStylesheets().addAll(ownerScene.getStylesheets());
            alert.initOwner(primaryStage);
        }

        alert.showAndWait();
    }

    private String msg(String key, Locale locale) {
        return messageSource.getMessage(key, null, key, locale);
    }
}
