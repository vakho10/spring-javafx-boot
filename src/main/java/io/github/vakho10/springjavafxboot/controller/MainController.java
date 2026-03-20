package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.navigation.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MainController {

    private final Navigator navigator;
    private final MessageSource messageSource;

    @FXML
    private Label welcomeText;

    @FXML
    private Label counterText;

    private int counter = 0;

    @FXML
    protected void initialize() {
        updateCounterText();
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(messageSource.getMessage("main.welcome", null, navigator.getCurrentLocale()));
    }

    @FXML
    protected void onIncrementClick() {
        counter++;
        updateCounterText();
    }

    @FXML
    protected void onDecrementClick() {
        counter--;
        updateCounterText();
    }

    @FXML
    protected void onGoToSecondClick() {
        navigator.navigateTo(SecondController.class);
    }

    private void updateCounterText() {
        counterText.setText(messageSource.getMessage("main.counter", new Object[]{counter}, navigator.getCurrentLocale()));
    }
}
