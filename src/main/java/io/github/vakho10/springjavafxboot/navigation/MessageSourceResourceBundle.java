package io.github.vakho10.springjavafxboot.navigation;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Adapts Spring's MessageSource to Java's ResourceBundle,
 * allowing FXML's %key syntax to use Spring-managed messages.
 */
@Component
@RequiredArgsConstructor
public class MessageSourceResourceBundle extends ResourceBundle {

    private final MessageSource messageSource;

    @Setter
    private Locale locale = Locale.ENGLISH;

    @Override
    protected Object handleGetObject(String key) {
        return messageSource.getMessage(key, null, key, locale);
    }

    @Override
    public boolean containsKey(String key) {
        return handleGetObject(key) != null;
    }

    @Override
    public Enumeration<String> getKeys() {
        return java.util.Collections.emptyEnumeration();
    }
}
