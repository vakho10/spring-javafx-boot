package io.github.vakho10.springjavafxboot.router;

/**
 * Thrown when the {@link FxRouter} encounters an error during navigation,
 * such as an unknown route, view resolution failure, or handler invocation error.
 */
public class RoutingException extends RuntimeException {

    public RoutingException(String message) {
        super(message);
    }

    public RoutingException(String message, Throwable cause) {
        super(message, cause);
    }
}