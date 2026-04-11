package io.github.vakho10.springjavafxboot.listener;

import io.github.vakho10.springjavafxboot.event.NavigationEvent;
import io.github.vakho10.springjavafxboot.event.NavigationPhase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Demo listener that logs all navigation events.
 * <p>
 * Shows how any Spring bean can react to navigation using standard
 * {@code @EventListener} — no tight coupling to the router.
 */
@Slf4j
@Component
public class NavigationLogger {

    @EventListener
    public void onNavigation(NavigationEvent event) {
        if (event.getPhase() == NavigationPhase.AFTER) {
            log.info("[NavigationLogger] {} → {}", event.getFromPath(), event.getToPath());
        }
    }
}
