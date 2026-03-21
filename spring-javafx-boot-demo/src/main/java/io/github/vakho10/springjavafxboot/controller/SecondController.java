package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.router.WindowOptions;
import io.github.vakho10.springjavafxboot.router.WindowResult;
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
public class SecondController {

    private final FxRouter router;
    private final LocalizedMessageSource messages;

    @FXML
    private Label modalResultLabel;

    @FXML
    private void onBackButtonClick() {
        router.navigateTo("/main");
    }

    @FXML
    private void onOpenWindowClick() {
        router.openWindow("/demo/window", new WindowOptions()
                .title("Demo Window")
                .size(400, 300));
    }

    @FXML
    private void onOpenModalClick() {
        WindowResult<String> result = router.openModal("/demo/modal", new WindowOptions()
                .title("Demo Modal")
                .size(400, 250)
                .resizable(false));

        result.get().ifPresent(value ->
                modalResultLabel.setText(messages.msg("second.modal.result", value)));
    }
}