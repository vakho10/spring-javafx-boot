package io.github.vakho10.springjavafxboot.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Controller for the modeless window demo.
 * <p>
 * Demonstrates a simple independent window that does not block the parent.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DemoWindowController {

    @FXML
    private void onCloseClick(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}