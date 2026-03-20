package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.navigation.Navigator;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MainController {

    private final Navigator navigator;
    private final LocalizedMessageSource messages;

    @FXML
    private Label welcomeText;

    @FXML
    private Label counterText;

    private int counter = 0;

    @FXML
    private void initialize() {
        updateCounterText();
    }

    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText(messages.msg("main.welcome"));
    }

    @FXML
    private void onIncrementClick() {
        counter++;
        updateCounterText();
    }

    @FXML
    private void onDecrementClick() {
        counter--;
        updateCounterText();
    }

    @FXML
    private void onGoToSecondClick() {
        navigator.navigateTo(SecondController.class);
    }

    @FXML
    private void onThrowErrorClick() {
        throw new RuntimeException(messages.msg("main.error.test"));
    }

    private void updateCounterText() {
        counterText.setText(messages.msg("main.counter", counter));
    }
}
