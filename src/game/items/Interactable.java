package game.items;

import game.characters.PlayerCharacter;

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
}
