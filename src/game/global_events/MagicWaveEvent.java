package game.global_events;

import game.core.ScreenAction;
import game.core.ScreenListener;
import game.map.GameMap;

/**
 * Represents a global "Magic Wave" event that affects all characters in the game.
 * When triggered, the event notifies the game controller to apply its effects (e.g., damaging all characters).
 */
public class MagicWaveEvent implements GlobalEvent {

    // Methods
    /**
     * Constructs a MagicWaveEvent.
     */
    public MagicWaveEvent() {}

    /**
     * Executes the Magic Wave event by notifying the game controller.
     * Triggers a screen action indicating that a global event has occurred.
     *
     * @param map the game map (unused in this implementation but provided for consistency)
     * @param gameController the controller to notify about the global event
     */
    @Override
    public void execute(GameMap map, ScreenListener gameController) {
        gameController.onAction(ScreenAction.GLOBAL_EVENT, this);
    }

    /**
     * Gets the name/description of the event.
     * Used for logging or GUI display.
     *
     * @return a string describing the Magic Wave event
     */
    @Override
    public String getName() {
        return "Magic Wave - All characters take 2 damage!";
    }
}
