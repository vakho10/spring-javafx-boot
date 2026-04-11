package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.annotation.ModelAttribute;
import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Controller for the detail view — demonstrates path variable usage.
 * <p>
 * Navigated to via {@code router.navigateTo("/detail/42")}, where {@code 42}
 * is extracted as the {@code id} path variable.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class DetailController {

    private final FxRouter router;
    private final LocalizedMessageSource messages;

    @ModelAttribute
    private String id;

    @FXML
    private Label detailLabel;

    @FXML
    private void initialize() {
        detailLabel.setText(messages.msg("detail.label", id));
    }

    @FXML
    private void onBackButtonClick() {
        router.navigateTo("/main");
    }
}
