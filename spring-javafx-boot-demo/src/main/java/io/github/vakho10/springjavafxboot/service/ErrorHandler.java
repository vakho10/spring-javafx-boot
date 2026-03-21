package io.github.vakho10.springjavafxboot.service;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Global error handler that shows themed, localized error alerts
 * with expandable stack trace details.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorHandler {

    private final LocalizedMessageSource messages;

    private Stage primaryStage;

    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Catch uncaught exceptions on the JavaFX Application Thread
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> showError(e));

        // Catch uncaught exceptions on all other threads
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
                Platform.runLater(() -> showError(e)));
    }

    public void showError(Throwable throwable) {
        log.error("Uncaught exception", throwable);

        // Use a custom OK button with localized text
        ButtonType okButton = new ButtonType(messages.msg("error.button.ok"), ButtonBar.ButtonData.OK_DONE);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getButtonTypes().setAll(okButton);
        alert.setTitle(messages.msg("error.title"));
        alert.setHeaderText(messages.msg("error.header"));
        alert.setContentText(throwable.getMessage());

        // Expandable stack trace
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));

        Label detailsLabel = new Label(messages.msg("error.details"));
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

        // Localize the "Show Details" / "Hide Details" toggle text
        alert.getDialogPane().expandedProperty().addListener((obs, wasExpanded, isExpanded) ->
                updateDetailsButtonText(alert));
        // Set initial text (collapsed state)
        Platform.runLater(() -> updateDetailsButtonText(alert));

        alert.showAndWait();
    }

    private void updateDetailsButtonText(Alert alert) {
        Hyperlink detailsButton = (Hyperlink) alert.getDialogPane().lookup(".details-button");
        if (detailsButton != null) {
            boolean expanded = alert.getDialogPane().isExpanded();
            String key = expanded ? "error.hideDetails" : "error.showDetails";
            detailsButton.setText(messages.msg(key));
        }
    }
}
