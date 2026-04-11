package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.annotation.ModelAttribute;
import io.github.vakho10.springjavafxboot.guard.DemoAccessGuard;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private final DemoAccessGuard accessGuard;

    @ModelAttribute
    private String greeting;

    @FXML
    private Label welcomeText;

    @FXML
    private Label counterText;

    @FXML
    private Label guardStatusLabel;

    @FXML
    private Button toggleGuardButton;

    private int counter = 0;

    @FXML
    private void initialize() {
        if (greeting != null) {
            welcomeText.setText(greeting);
        }
        updateCounterText();
        updateGuardStatus();
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
    private void onViewDetailClick() {
        router.navigateTo("/detail/" + counter);
    }

    @FXML
    private void onThrowErrorClick() {
        throw new RuntimeException(messages.msg("main.error.test"));
    }

    @FXML
    private void onToggleGuardClick() {
        accessGuard.setAccessEnabled(!accessGuard.isAccessEnabled());
        updateGuardStatus();
    }

    private void updateCounterText() {
        counterText.setText(messages.msg("main.counter", counter));
    }

    private void updateGuardStatus() {
        boolean enabled = accessGuard.isAccessEnabled();
        guardStatusLabel.setText(messages.msg(
                enabled ? "guard.status.enabled" : "guard.status.disabled"));
        toggleGuardButton.setText(messages.msg(
                enabled ? "guard.button.disable" : "guard.button.enable"));
    }
}