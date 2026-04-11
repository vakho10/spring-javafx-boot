package io.github.vakho10.springjavafxboot.guard;

import io.github.vakho10.springjavafxboot.router.FxRouteGuard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Demo global route guard that controls access to the "/second" route.
 * <p>
 * Toggle access on/off via {@link #setAccessEnabled(boolean)} to demonstrate
 * how {@link FxRouteGuard} blocks navigation.
 */
@Slf4j
@Component
public class DemoAccessGuard implements FxRouteGuard {

    private boolean accessEnabled = true;

    @Override
    public boolean canActivate(String targetPath, Map<String, Object> params) {
        if ("/second".equals(targetPath) && !accessEnabled) {
            log.info("Access to \"{}\" denied by DemoAccessGuard", targetPath);
            return false;
        }
        return true;
    }

    public boolean isAccessEnabled() {
        return accessEnabled;
    }

    public void setAccessEnabled(boolean accessEnabled) {
        this.accessEnabled = accessEnabled;
    }
}
