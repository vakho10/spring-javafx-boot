package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainController {

    private final Navigator navigator;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("კეთილი იყოს თქვენი მობრძანება — Welcome to JavaFX!");
    }

    @FXML
    protected void onGoToSecondClick() {
        navigator.navigateTo(SecondController.class);
    }
}
