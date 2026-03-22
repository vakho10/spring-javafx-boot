package io.github.vakho10.springjavafxboot.autoconfigure;

import io.github.vakho10.springjavafxboot.navigation.ViewResolver;
import io.github.vakho10.springjavafxboot.router.FxRouteRegistry;
import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.service.FxTitleService;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Spring JavaFX Boot.
 * <p>
 * Registers core framework beans (router, view resolver, i18n) when JavaFX is on the
 * classpath. Each bean uses {@code @ConditionalOnMissingBean} so applications can
 * override any component with their own implementation.
 * <p>
 * Configurable properties:
 * <pre>
 *   spring.javafx.view.prefix=/templates/   # FXML template location (default)
 *   spring.javafx.view.suffix=.fxml         # FXML file extension (default)
 * </pre>
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(javafx.application.Application.class)
public class SpringJavaFxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalizedMessageSource localizedMessageSource(MessageSource messageSource) {
        return new LocalizedMessageSource(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewResolver fxViewResolver(
            MessageSource messageSource,
            @Value("${spring.javafx.view.prefix:/templates/}") String prefix,
            @Value("${spring.javafx.view.suffix:.fxml}") String suffix) {
        log.info("Configuring ViewResolver with prefix=\"{}\" suffix=\"{}\"", prefix, suffix);
        return new ViewResolver(messageSource, prefix, suffix);
    }

    @Bean
    @ConditionalOnMissingBean
    public FxRouteRegistry fxRouteRegistry(ApplicationContext applicationContext) {
        return new FxRouteRegistry(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public FxTitleService fxTitleService(LocalizedMessageSource localizedMessageSource) {
        return new FxTitleService(localizedMessageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public FxRouter fxRouter(FxRouteRegistry fxRouteRegistry,
                             ViewResolver viewResolver,
                             ApplicationContext applicationContext,
                             FxTitleService fxTitleService) {
        return new FxRouter(fxRouteRegistry, viewResolver, applicationContext, fxTitleService);
    }
}
