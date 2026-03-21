package io.github.vakho10.springjavafxboot.router;

import javafx.stage.Modality;

/**
 * Configuration options for opening a route in a new window.
 * <p>
 * Use the builder-style setters for a fluent API:
 * <pre>{@code
 * router.openWindow("/settings", new WindowOptions()
 *     .title("Settings")
 *     .size(600, 400)
 *     .resizable(false));
 *
 * router.openModal("/picker", Map.of("items", list), new WindowOptions()
 *     .title("Select an item")
 *     .size(400, 300));
 * }</pre>
 *
 * @see FxRouter#openWindow(String, java.util.Map, WindowOptions)
 * @see FxRouter#openModal(String, java.util.Map, WindowOptions)
 */
public class WindowOptions {

    private String title;
    private double width = -1;
    private double height = -1;
    private boolean resizable = true;
    private Modality modality = Modality.NONE;

    public WindowOptions title(String title) {
        this.title = title;
        return this;
    }

    public WindowOptions size(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive, got: %sx%s".formatted(width, height));
        }
        this.width = width;
        this.height = height;
        return this;
    }

    public WindowOptions resizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    WindowOptions modality(Modality modality) {
        this.modality = modality;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isResizable() {
        return resizable;
    }

    public Modality getModality() {
        return modality;
    }
}