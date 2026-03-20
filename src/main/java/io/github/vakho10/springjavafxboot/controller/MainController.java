package io.github.vakho10.springjavafxboot.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("კეთილი იყოს თქვენი მობრძანება — Welcome to JavaFX!");
    }
}
