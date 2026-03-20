package io.github.vakho10.springjavafxboot.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class Navigator {

    private final ViewResolver viewResolver;
    private final ApplicationContext applicationContext;
    private final MessageSource messageSource;
    private Stage primaryStage;
    private BorderPane rootPane;

    private static final Locale GEORGIAN = Locale.of("ka");

    @Getter
    private Locale currentLocale = Locale.ENGLISH;
    private Class<?> currentController;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.rootPane = (BorderPane) primaryStage.getScene().getRoot();
        buildMenuBar();
    }

    public void navigateTo(Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(viewResolver.resolve(controllerClass));
            loader.setControllerFactory(applicationContext::getBean);
            loader.setResources(new MessageSourceResourceBundle(messageSource, currentLocale));
            Parent view = loader.load();
            rootPane.setCenter(view);
            currentController = controllerClass;
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to " + controllerClass.getSimpleName(), e);
        }
    }

    public void switchLocale(Locale locale) {
        this.currentLocale = locale;
        buildMenuBar();
        if (currentController != null) {
            navigateTo(currentController);
        }
    }

    private void buildMenuBar() {
        Menu languageMenu = new Menu(messageSource.getMessage("menu.language", null, currentLocale));

        ToggleGroup group = new ToggleGroup();

        RadioMenuItem englishItem = new RadioMenuItem(messageSource.getMessage("menu.language.english", null, currentLocale));
        englishItem.setToggleGroup(group);
        englishItem.setSelected(currentLocale.equals(Locale.ENGLISH));
        englishItem.setOnAction(e -> switchLocale(Locale.ENGLISH));

        RadioMenuItem georgianItem = new RadioMenuItem(messageSource.getMessage("menu.language.georgian", null, currentLocale));
        georgianItem.setToggleGroup(group);
        georgianItem.setSelected(currentLocale.equals(GEORGIAN));
        georgianItem.setOnAction(e -> switchLocale(GEORGIAN));

        languageMenu.getItems().addAll(englishItem, georgianItem);

        MenuBar menuBar = new MenuBar(languageMenu);
        rootPane.setTop(menuBar);
    }
}
