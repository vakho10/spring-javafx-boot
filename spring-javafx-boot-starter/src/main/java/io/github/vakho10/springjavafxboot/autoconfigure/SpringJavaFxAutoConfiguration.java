package io.github.vakho10.springjavafxboot.autoconfigure;

import io.github.vakho10.springjavafxboot.router.FxRouteGuard;
import io.github.vakho10.springjavafxboot.router.FxRouteRegistry;
import io.github.vakho10.springjavafxboot.router.FxRouter;
import io.github.vakho10.springjavafxboot.service.FxTitleService;
import io.github.vakho10.springjavafxboot.service.LocalizedMessageSource;
import io.github.vakho10.springjavafxboot.view.ViewResolver;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Spring JavaFX Boot.
 * <p>
 * Registers core framework beans (router, view resolver, i18n) when JavaFX is on the
 * classpath. Each bean uses {@code @ConditionalOnMissingBean} so applications can
 * override any component with their own implementation.
 *
 * @see FxViewProperties
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(javafx.application.Application.class)
@EnableConfigurationProperties(FxViewProperties.class)
public class SpringJavaFxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalizedMessageSource localizedMessageSource(MessageSource messageSource) {
        return new LocalizedMessageSource(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewResolver fxViewResolver(MessageSource messageSource, FxViewProperties properties) {
        log.info("Configuring ViewResolver with prefix=\"{}\" suffix=\"{}\"",
                properties.getPrefix(), properties.getSuffix());
        return new ViewResolver(messageSource, properties.getPrefix(), properties.getSuffix());
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
                             FxTitleService fxTitleService,
                             List<FxRouteGuard> routeGuards,
                             ApplicationEventPublisher eventPublisher) {
        return new FxRouter(fxRouteRegistry, viewResolver, applicationContext, fxTitleService, routeGuards, eventPublisher);
    }
}
