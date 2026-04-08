package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.annotation.ModelAttribute;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MainController {

    private final FxRouter router;
    private final LocalizedMessageSource messages;

    @ModelAttribute
    private String greeting;

    @FXML
    private Label welcomeText;

    @FXML
    private Label counterText;

    private int counter = 0;

    @FXML
    private void initialize() {
        if (greeting != null) {
            welcomeText.setText(greeting);
        }
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
        router.navigateTo("/second");
    }

    @FXML
    private void onThrowErrorClick() {
        throw new RuntimeException(messages.msg("main.error.test"));
    }

    private void updateCounterText() {
        counterText.setText(messages.msg("main.counter", counter));
    }
}