package io.github.vakho10.springjavafxboot.router;

import java.util.Optional;

/**
 * Holds the result of a modal window after it closes.
 * <p>
 * The FXML controller of the modal can set a result before closing:
 * <pre>{@code
 * @FXML
 * private void onSave() {
 *     windowResult.set(selectedItem);
 *     stage.close();
 * }
 * }</pre>
 * <p>
 * The caller retrieves it:
 * <pre>{@code
 * WindowResult<String> result = router.openModal("/picker");
 * result.get().ifPresent(value -> label.setText(value));
 * }</pre>
 *
 * @param <T> the result type
 */
public class WindowResult<T> {

    private T value;
    private boolean present = false;

    /**
     * Set the result value. Called by the modal's controller before closing.
     */
    public void set(T value) {
        this.value = value;
        this.present = true;
    }

    /**
     * Get the result value, if one was set.
     */
    public Optional<T> get() {
        return present ? Optional.ofNullable(value) : Optional.empty();
    }

    /**
     * Whether a result was explicitly set (even if null).
     */
    public boolean isPresent() {
        return present;
    }
}