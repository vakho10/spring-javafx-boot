package io.github.vakho10.springjavafxboot.controller;

import io.github.vakho10.springjavafxboot.navigation.Navigator;
import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecondController {

    private final Navigator navigator;

    @FXML
    protected void onBackButtonClick() {
        navigator.navigateTo(MainController.class);
    }

}
