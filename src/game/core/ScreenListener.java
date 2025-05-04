package game.core;

/**
 * Listener interface for handling screen-level actions triggered during gameplay.
 * Implementing classes (typically controllers) respond to UI or game events like movement,
 * attacking, or ending a turn.
 */
public interface ScreenListener {

    /**
     * Called when a specific screen action is triggered.
     *
     * @param action the action to handle (e.g., MOVE, ATTACK, END_TURN)
     * @param data   optional additional data relevant to the action (e.g., direction, entity involved)
     * @return true if the action was handled successfully, false otherwise
     */
    boolean onAction(ScreenAction action, Object... data);
}
