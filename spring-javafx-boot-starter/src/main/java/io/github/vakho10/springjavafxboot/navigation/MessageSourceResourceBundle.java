package io.github.vakho10.springjavafxboot.navigation;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Adapts Spring's {@link MessageSource} to Java's {@link ResourceBundle},
 * allowing FXML's {@code %key} syntax to use Spring-managed messages.
 * <p>
 * Can be used in two ways:
 * <ul>
 *   <li>As a Spring {@code @Component} singleton — locale is set via {@link #setLocale(Locale)}</li>
 *   <li>As a standalone instance via {@link #MessageSourceResourceBundle(MessageSource, Locale)}
 *       — used internally by the {@link ViewResolver} when loading FXML views</li>
 * </ul>
 * <p>
 * Thread-safety note: the mutable {@code locale} field is safe because
 * JavaFX is single-threaded — all FXML loading happens on the JavaFX Application Thread.
 */
public class MessageSourceResourceBundle extends ResourceBundle {

    private final MessageSource messageSource;

    @Setter
    private Locale locale;

    /**
     * Constructor for Spring DI — used when injected as a singleton bean.
     * Defaults to {@link Locale#ENGLISH}.
     */
    public MessageSourceResourceBundle(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = Locale.ENGLISH;
    }

    /**
     * Constructor for standalone use — used by {@link ViewResolver}
     * to create a locale-bound instance per FXML load.
     */
    public MessageSourceResourceBundle(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

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