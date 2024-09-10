package com.github.vakho10.spring_javafx_boot.controller;

import com.github.vakho10.spring_javafx_boot.service.GreetingService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppController {

    @FXML
    private Label welcomeText;

    @Autowired
    private GreetingService greetingService;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(greetingService.getGreeting());
    }
}
