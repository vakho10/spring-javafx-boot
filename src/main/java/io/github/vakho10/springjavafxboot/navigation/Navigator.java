package io.github.vakho10.springjavafxboot.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Navigator {

    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void navigateTo(Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(viewResolver.resolve(controllerClass));
            loader.setControllerFactory(applicationContext::getBean);
            Parent view = loader.load();
            primaryStage.getScene().setRoot(view);
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to " + controllerClass.getSimpleName(), e);
        }
    }
}
