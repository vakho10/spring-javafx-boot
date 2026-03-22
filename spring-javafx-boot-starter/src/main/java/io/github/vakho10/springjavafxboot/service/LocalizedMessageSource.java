package io.github.vakho10.springjavafxboot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Convenience wrapper around Spring's {@link MessageSource} that tracks the
 * current locale. Provides short {@code msg()} methods so callers don't have
 * to pass the locale every time.
 * <p>
 * The locale is typically updated when the user switches language.
 */
@RequiredArgsConstructor
public class LocalizedMessageSource {

    private static final Logger log = LoggerFactory.getLogger(LocalizedMessageSource.class);

    private final MessageSource messageSource;

    @Getter
    private Locale locale = Locale.ENGLISH;

    /**
     * Update the current locale. Typically called when the user switches language.
     */
    public void setLocale(Locale locale) {
        log.debug("Locale changed: {} → {}", this.locale, locale);
        this.locale = locale;
    }

    /**
     * Get a message by key using the current locale.
     */
    public String msg(String key) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Get a message by key with arguments using the current locale.
     */
    public String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
