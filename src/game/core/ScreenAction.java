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
    PLAYER_ATTACK,

    /**
     * Indicates that an entity has received damage.
     */
    RECEIVED_DAMAGE,

    /**
     * Indicates that an entity needs to have a text animation
     */
    RECEIVE_DAMAGE_TEXT_ANIMATION,

    /**
     * Represents the action of picking up an item on the map.
     */
    PICKUP,

    /**
     * Marks the end of a player's or enemy's turn.
     */
    END_TURN,

    /**
     * Represents an action taken by an enemy unit
     */
    ENEMY_ACTION,

    /**
     * Represents enemy attacking during it's action
     */
    ENEMY_ATTACK,

    /**
     * Representing part of the logic needed to load a save
     */
    LOAD_DATA,

    /**
     * Represents a global game event, such as weather effects, map changes, or scripted events.
     */
    GLOBAL_EVENT,

    /**
     * Exits the game and closes the application.
     */
    EXIT_GAME,

    /**
     * Refreshes GUI elements
     */
    REFRESH_GUI
}
