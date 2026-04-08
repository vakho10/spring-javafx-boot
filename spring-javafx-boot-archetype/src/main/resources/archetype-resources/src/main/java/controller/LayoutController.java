#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.annotation.RouterOutlet;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Locale;

/**
 * Layout controller — defines the application shell (menu bar + content outlet).
 * <p>
 * Child routes ("/main", "/second") render inside the {@link ${symbol_pound}contentArea} outlet.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class LayoutController {

    private static final Locale GEORGIAN = new Locale("ka");

    private final FxRouter router;
    private final LocalizedMessageSource messages;

    @FXML
    @RouterOutlet
    private BorderPane contentArea;

    @FXML
    private MenuBar menuBar;

    @FXML
    private void initialize() {
        buildMenuBar();
    }

    private void switchLocale(Locale locale) {
        Locale.setDefault(locale);
        messages.setLocale(locale);
        router.reload();
    }

    private void buildMenuBar() {
        Locale currentLocale = Locale.getDefault();

        Menu languageMenu = new Menu(messages.msg("menu.language"));
        ToggleGroup langGroup = new ToggleGroup();

        RadioMenuItem englishItem = new RadioMenuItem(messages.msg("menu.language.english"));
        englishItem.setToggleGroup(langGroup);
        englishItem.setSelected(currentLocale.equals(Locale.ENGLISH));
        englishItem.setOnAction(e -> switchLocale(Locale.ENGLISH));

        RadioMenuItem georgianItem = new RadioMenuItem(messages.msg("menu.language.georgian"));
        georgianItem.setToggleGroup(langGroup);
        georgianItem.setSelected(currentLocale.equals(GEORGIAN));
        georgianItem.setOnAction(e -> switchLocale(GEORGIAN));

        languageMenu.getItems().addAll(englishItem, georgianItem);

        menuBar.getMenus().setAll(languageMenu);
    }
}
