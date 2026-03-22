package io.github.vakho10.springjavafxboot.service;

import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;

/**
 * Manages the window title of the primary {@link Stage}, analogous to
 * Angular's {@code Title} service.
 * <p>
 * Titles can be set programmatically from controllers, or declaratively
 * via the {@code title} attribute on
 * {@link io.github.vakho10.springjavafxboot.router.FxMapping @FxMapping}.
 * Declarative titles are applied automatically by the router after navigation.
 * <p>
 * An optional title format (e.g. {@code "%s - My App"}) can wrap every title
 * so a consistent suffix/prefix is applied without repeating it in every route.
 *
 * <h3>Programmatic usage (in a controller):</h3>
 * <pre>{@code
 * @Autowired private FxTitleService titleService;
 *
 * titleService.setTitle("Dashboard");
 * titleService.setTitle("page.detail.title", item.getName()); // i18n with args
 * }</pre>
 *
 * <h3>Declarative usage (on a route):</h3>
 * <pre>{@code
 * @FxMapping(value = "/main", title = "page.main.title")
 * }</pre>
 *
 * @see io.github.vakho10.springjavafxboot.router.FxMapping
 * @see io.github.vakho10.springjavafxboot.router.FxRouter
 */
public class FxTitleService {

    private static final Logger log = LoggerFactory.getLogger(FxTitleService.class);

    private final LocalizedMessageSource messages;

    private Stage primaryStage;
    private String titleFormat = "%s";

    public FxTitleService(LocalizedMessageSource messages) {
        this.messages = messages;
    }

    /**
     * Binds this service to the primary stage. Must be called before any
     * title updates (typically in {@code Application.start()}).
     */
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
        log.debug("Initialized with primary stage");
    }

    /**
     * Sets a title format pattern applied to every title. Use {@code %s} as
     * the placeholder for the page title.
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code "%s - My App"} → "Dashboard - My App"</li>
     *   <li>{@code "My App | %s"} → "My App | Dashboard"</li>
     *   <li>{@code "%s"} → "Dashboard" (default, no wrapping)</li>
     * </ul>
     */
    public void setTitleFormat(String format) {
        this.titleFormat = format;
        log.debug("Title format set to: \"{}\"", format);
    }

    /**
     * Returns the current title format pattern.
     */
    public String getTitleFormat() {
        return titleFormat;
    }

    /**
     * Returns the current window title.
     */
    public String getTitle() {
        requireStage();
        return primaryStage.getTitle();
    }

    /**
     * Sets the window title. The value is first resolved as a message key
     * via {@link LocalizedMessageSource}. If no message is found, the value
     * is used as a literal string. The configured title format is then applied.
     *
     * @param titleOrKey a literal title or an i18n message key
     * @param args       optional arguments for message interpolation
     */
    public void setTitle(String titleOrKey, Object... args) {
        requireStage();
        String resolved = resolveTitle(titleOrKey, args);
        String formatted = titleFormat.formatted(resolved);
        primaryStage.setTitle(formatted);
        log.debug("Title set to: \"{}\"", formatted);
    }

    /**
     * Resolves a title key or literal. Tries the message source first;
     * falls back to the raw value if no message is found.
     */
    String resolveTitle(String titleOrKey, Object... args) {
        try {
            return messages.msg(titleOrKey, args);
        } catch (NoSuchMessageException e) {
            return titleOrKey;
        }
    }

    private void requireStage() {
        if (primaryStage == null) {
            throw new IllegalStateException(
                    "FxTitleService not initialized. Call titleService.init(primaryStage) "
                            + "in Application.start() before setting titles.");
        }
    }
}
