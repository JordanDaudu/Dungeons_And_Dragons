package game.gui;

import java.awt.Color;

/**
 * Interface for components that support blinking animations.
 * Implementing classes define how the component should blink
 * with a given color.
 */
public interface BlinkingAnimation {

    /**
     * Performs a blink animation using the specified color.
     *
     * @param color the color to blink with
     */
    void blink(Color color);
}
