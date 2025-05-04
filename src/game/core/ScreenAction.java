package game.core;

/**
 * Defines the possible screen-level actions that can occur during gameplay.
 * These actions are used for communication between the view and controller components,
 * allowing user input or game events to trigger specific behaviors.
 */
public enum ScreenAction {
    /**
     * Starts a new game session.
     */
    START_GAME,

    /**
     * Represents a movement action performed by the player.
     */
    MOVE,

    /**
     * Represents a combat action where an entity performs an attack.
     */
    ATTACK,

    /**
     * Indicates that an entity has received damage.
     */
    RECEIVEDDAMAGE,

    /**
     * Represents the action of picking up an item on the map.
     */
    PICKUP,

    /**
     * Marks the end of a player's or enemy's turn.
     */
    END_TURN,

    /**
     * Exits the game and closes the application.
     */
    EXIT_GAME
}
