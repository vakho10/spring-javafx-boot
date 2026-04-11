package io.github.vakho10.springjavafxboot.event;

/**
 * The phase of a navigation lifecycle event.
 *
 * @see NavigationEvent
 */
public enum NavigationPhase {

    /** Published before guards are checked and navigation begins. */
    BEFORE,

    /** Published after navigation completes and the view is rendered. */
    AFTER
}
