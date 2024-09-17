package com.github.vakho10.spring_javafx_boot.controller;

import com.github.vakho10.spring_javafx_boot.service.GreetingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FxmlView // equal to: @FxmlView("MainController.fxml")
public class MainWindow {

    @Autowired
    private GreetingService greetingService;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        log.info("Clicked on button. Action event object: {}", event);
        welcomeText.setText(greetingService.getGreeting());
    }
}
