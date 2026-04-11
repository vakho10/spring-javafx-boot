package io.github.vakho10.springjavafxboot.router;

import java.util.Map;

/**
 * The result of resolving a navigation path against the route registry.
 * <p>
 * Contains the matched {@link HandlerMethod} and any path variables extracted
 * from the URL. For exact routes (no path variables), {@code pathVariables}
 * is an empty map.
 *
 * @param handler       the matched handler method
 * @param pathVariables extracted path variables (e.g. {@code {id} → "42"})
 */
public record ResolvedRoute(
        HandlerMethod handler,
        Map<String, String> pathVariables
) {
}
