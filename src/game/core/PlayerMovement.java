package game.core;

import game.characters.PlayerCharacter;
import game.map.Position;

/**
 * Interface defining movement capabilities for a player character on the game map.
 */
public interface PlayerMovement {

    /**
     * Moves the player one step to the right on the map.
     *
     * @param player the player character to move.
     * @return the new position after moving right.
     */
    Position MoveRight(PlayerCharacter player);

    /**
     * Moves the player one step to the left on the map.
     *
     * @param player the player character to move.
     * @return the new position after moving left.
     */
    Position MoveLeft(PlayerCharacter player);

    /**
     * Moves the player one step upward on the map.
     *
     * @param player the player character to move.
     * @return the new position after moving up.
     */
    Position MoveUp(PlayerCharacter player);

    /**
     * Moves the player one step downward on the map.
     *
     * @param player the player character to move.
     * @return the new position after moving down.
     */
    Position MoveDown(PlayerCharacter player);
}
