#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controller;

import io.github.vakho10.springjavafxboot.router.FxRouter;
import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SecondController {

    private final FxRouter router;

    @FXML
    private void onBackButtonClick() {
        router.navigateTo("/main");
    }
}
