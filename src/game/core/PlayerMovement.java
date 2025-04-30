package game.core;

import game.map.Position;

/**
 * Interface defining movement capabilities for a player character on the game map.
 */
public interface PlayerMovement {

    /**
     * Moves the player one step to the right on the map.
     *
     * @return the new position after moving right.
     */
    Position moveRight();

    /**
     * Moves the player one step to the left on the map.
     *
     * @return the new position after moving left.
     */
    Position moveLeft();

    /**
     * Moves the player one step upward on the map.
     *
     * @return the new position after moving up.
     */
    Position moveUp();

    /**
     * Moves the player one step downward on the map.
     *
     * @return the new position after moving down.
     */
    Position moveDown();
}
