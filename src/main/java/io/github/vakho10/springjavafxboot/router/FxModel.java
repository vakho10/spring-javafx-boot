package io.github.vakho10.springjavafxboot.router;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple model object that carries data from {@link FxMapping} route handlers
 * to FXML controllers via {@link ModelAttribute} field injection.
 * <p>
 * The router creates a fresh instance for each navigation, passes it to the
 * handler method, then injects matching attributes into the target FXML controller.
 *
 * <pre>{@code
 * // In a @FxRoutes class:
 * @FxMapping("/main")
 * public String main(FxModel model) {
 *     model.put("counter", 42);
 *     model.put("username", "Vakho");
 *     return "main";
 * }
 *
 * // In the FXML controller:
 * @ModelAttribute private int counter;     // ← injected as 42
 * @ModelAttribute private String username; // ← injected as "Vakho"
 * }</pre>
 *
 * @see FxMapping
 * @see ModelAttribute
 */
public class FxModel {

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public FxModel put(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    public boolean contains(String key) {
        return attributes.containsKey(key);
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(attributes);
    }
}