package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.navigation.Navigator;
import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SecondController {

    private final Navigator navigator;

    @FXML
    private void onBackButtonClick() {
        navigator.navigateTo(MainController.class);
    }
}
