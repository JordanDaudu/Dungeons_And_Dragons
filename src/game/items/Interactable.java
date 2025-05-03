package game.items;

import game.characters.PlayerCharacter;
import game.map.Position;

/**
 * Represents an object that can be interacted with by a {@link PlayerCharacter}.
 */
public interface Interactable {

    /**
     * Defines the interaction behavior when a {@link PlayerCharacter} interacts with this object.
     *
     * @param c the player character interacting with this object
     */
    void interact(PlayerCharacter c);

    /**
     * Sets a new position for the entity
     *
     * @param newPos the new position to set
     */
    void setPosition(Position newPos);

    /**
     * Provides a textual description of the interaction, if any.
     * Useful for displaying contextual information to the player.
     *
     * @return a string describing the interaction, or {@code null} if none is provided
     */
    default String getInteractionDetails() {return null;}
}
