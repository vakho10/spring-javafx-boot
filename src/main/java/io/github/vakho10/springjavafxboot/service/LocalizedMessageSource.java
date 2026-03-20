package io.github.vakho10.springjavafxboot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Convenience wrapper around Spring's {@link MessageSource} that tracks the
 * current locale. Provides short {@code msg()} methods so callers don't have
 * to pass the locale every time.
 * <p>
 * The locale is updated by {@link io.github.vakho10.springjavafxboot.navigation.Navigator}
 * whenever the user switches language.
 */
@Service
@RequiredArgsConstructor
public class LocalizedMessageSource {

    private final MessageSource messageSource;

    @Getter
    @Setter
    private Locale locale = Locale.ENGLISH;

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
