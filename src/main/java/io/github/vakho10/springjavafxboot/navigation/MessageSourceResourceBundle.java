package io.github.vakho10.springjavafxboot.navigation;

import org.springframework.context.MessageSource;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Adapts Spring's MessageSource to Java's ResourceBundle,
 * allowing FXML's %key syntax to use Spring-managed messages.
 */
public class MessageSourceResourceBundle extends ResourceBundle {

    private final MessageSource messageSource;
    private final Locale locale;

    public MessageSourceResourceBundle(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

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
