package io.github.vakho10.springjavafxboot;

/**
 * Application entry point that delegates to {@link JavaFxApplication}.
 * <p>
 * When the JVM's main class extends {@link javafx.application.Application},
 * the JavaFX launcher performs a module-path check before {@code main()} executes.
 * This fails in classpath-based setups (e.g. Maven, Spring Boot).
 * <p>
 * Using a plain launcher class bypasses that check — JavaFX is instead
 * initialized via {@link javafx.application.Application#launch} at runtime,
 * where it resolves its classes from the classpath normally.
 */
public class Launcher {
    public static void main(String[] args) {
        JavaFxApplication.main(args);
    }
}
