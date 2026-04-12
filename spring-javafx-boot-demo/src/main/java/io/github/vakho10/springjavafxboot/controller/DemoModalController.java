package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.annotation.ModelAttribute;
import io.github.vakho10.springjavafxboot.router.WindowResult;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Controller for the modal dialog demo.
 * <p>
 * Demonstrates returning a value to the caller via {@link WindowResult}.
 * The user types something, clicks confirm, and the result is passed
 * back to the {@code SecondController} that opened the modal.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DemoModalController {

    @ModelAttribute
    private WindowResult<String> windowResult;

    @FXML
    private TextField inputField;

    @FXML
    private void onConfirmClick() {
        if (windowResult != null) {
            windowResult.set(inputField.getText());
        }
        closeWindow(inputField);
    }

    @FXML
    private void onCancelClick() {
        closeWindow(inputField);
    }

    private void closeWindow(Node node) {
        Scene scene = node.getScene();
        if (scene == null) return;
        Window window = scene.getWindow();
        if (window != null) {
            window.hide();
        }
    }
}
