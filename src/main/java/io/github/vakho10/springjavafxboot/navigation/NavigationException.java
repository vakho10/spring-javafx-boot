package io.github.vakho10.springjavafxboot.navigation;

/**
 * Thrown when view navigation fails (e.g., FXML load error or uninitialized navigator).
 */
public class NavigationException extends RuntimeException {

    public NavigationException(String message) {
        super(message);
    }

    public NavigationException(String message, Throwable cause) {
        super(message, cause);
    }
}
