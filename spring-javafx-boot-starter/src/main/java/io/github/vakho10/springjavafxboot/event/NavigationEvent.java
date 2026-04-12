package io.github.vakho10.springjavafxboot.event;

import java.util.Map;
import org.springframework.context.ApplicationEvent;

/**
 * Published by {@link io.github.vakho10.springjavafxboot.router.FxRouter} during navigation.
 * <p>
 * Two events are fired for each navigation:
 * <ol>
 *   <li>{@link NavigationPhase#BEFORE} — before guards are checked</li>
 *   <li>{@link NavigationPhase#AFTER} — after the view has been rendered</li>
 * </ol>
 *
 * Listen using Spring's standard {@code @EventListener}:
 * <pre>{@code
 * @Component
 * public class NavigationLogger {
 *
 *     @EventListener
 *     public void onNavigation(NavigationEvent event) {
 *         if (event.getPhase() == NavigationPhase.AFTER) {
 *             log.info("Navigated from {} to {}", event.getFromPath(), event.getToPath());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see NavigationPhase
 */
public class NavigationEvent extends ApplicationEvent {

    private final String fromPath;
    private final String toPath;
    private final Map<String, Object> params;
    private final NavigationPhase phase;

    public NavigationEvent(Object source, String fromPath, String toPath,
                           Map<String, Object> params, NavigationPhase phase) {
        super(source);
        this.fromPath = fromPath;
        this.toPath = toPath;
        this.params = params;
        this.phase = phase;
    }

    /** The route path being navigated away from, or {@code null} for the first navigation. */
    public String getFromPath() {
        return fromPath;
    }

    /** The route path being navigated to. */
    public String getToPath() {
        return toPath;
    }

    /** The navigation parameters (includes path variables). */
    public Map<String, Object> getParams() {
        return params;
    }

    /** Whether this event is fired before or after navigation. */
    public NavigationPhase getPhase() {
        return phase;
    }

    @Override
    public String toString() {
        return "NavigationEvent{phase=%s, from='%s', to='%s'}".formatted(phase, fromPath, toPath);
    }
}
