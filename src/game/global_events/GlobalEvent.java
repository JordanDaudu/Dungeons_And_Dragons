package game.global_events;

import game.core.ScreenListener;
import game.map.GameMap;

/**
 * Represents a global event that can affect the entire game map or game state.
 * Implementations of this interface define specific behaviors to be triggered
 * during the game (e.g., natural disasters, magical surges, global buffs/debuffs).
 */
public interface GlobalEvent {

    /**
     * Executes the global event logic on the provided game map.
     *
     * @param map the game map on which the event occurs
     * @param gameController the screen listener to update GUI or log interactions
     */
    void execute(GameMap map, ScreenListener gameController);

    /**
     * Gets the name of the global event.
     * Used for display in GUI messages and logging purposes.
     *
     * @return the name of the event
     */
    String getName(); // For logs or GUI messages
}
