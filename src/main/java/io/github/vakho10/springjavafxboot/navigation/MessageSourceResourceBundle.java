package io.github.vakho10.springjavafxboot.navigation;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Adapts Spring's MessageSource to Java's ResourceBundle,
 * allowing FXML's %key syntax to use Spring-managed messages.
 * <p>
 * Thread-safety note: the mutable {@code locale} field is safe because
 * JavaFX is single-threaded — all FXML loading happens on the JavaFX Application Thread.
 */
@Component
@RequiredArgsConstructor
public class MessageSourceResourceBundle extends ResourceBundle {

    private final MessageSource messageSource;

    @Setter
    private Locale locale = Locale.ENGLISH;

    @Override
    protected Object handleGetObject(String key) {
        // Return the key itself as fallback so FXML renders the key name instead of crashing
        return messageSource.getMessage(key, null, key, locale);
    }

    @Override
    public boolean containsKey(String key) {
        try {
            messageSource.getMessage(key, null, locale);
            return true;
        } catch (NoSuchMessageException e) {
            return false;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        // Spring's MessageSource does not expose its key set
        return java.util.Collections.emptyEnumeration();
    }
}
