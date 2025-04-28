package game.core;

import game.map.Position;

import java.awt.Image;

/**
 * Represents a generic entity that exists within the game world.
 * Entities have a position on the map, a visible state, and a symbol for display.
 * Implementing classes may include characters, items, or structures.
 */
public interface GameEntity {

    /**
     * Gets the current position of the entity on the map.
     *
     * @return the position of the entity
     */
    Position getPosition();

    /**
     * Sets a new position for the entity.
     *
     * @param newPos the new position to set
     */
    void setPosition(Position newPos);

    /**
     * Returns a single-character or symbolic representation used for display.
     *
     * @return the display symbol of the entity
     */
    String getDisplaySymbol();

    /**
     * Sets the visibility state of the entity (e.g., for fog of war).
     *
     * @param visible true if the entity should be visible, false otherwise
     * @return true if the state was changed, false if unchanged
     */
    boolean setVisible(boolean visible);

    /**
     * Checks whether the entity is currently visible.
     *
     * @return true if visible, false otherwise
     */
    boolean isVisible();

    /**
     * Returns an image representation of the entity for display.
     *
     * @return the image of the entity
     */
    Image getDisplayImage();
}
