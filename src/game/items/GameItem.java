package game.items;

import game.core.GameEntity;
import game.map.GameMap;
import game.map.Position;

import java.io.Serializable;

/**
 * Abstract base class representing an item that can exist on the game map.
 * Implements {@link GameEntity} and supports positioning, visibility, and interaction logic.
 */
public abstract class GameItem implements GameEntity, Cloneable {

    // Data Members
    private Position position;
    private boolean blocksMovement;
    private String description;
    private boolean visible;

    // Methods
    /**
     * Constructs a GameItem with the specified position, movement blocking state, and description.
     *
     * @param position the position of the item on the map.
     * @param blocksMovement whether the item blocks' movement.
     * @param description a short description of the item.
     */
    public GameItem(Position position, boolean blocksMovement, String description) {

        this.position = new Position(position);
        this.blocksMovement = blocksMovement;
        this.description = description;
    }

    public GameItem(GameItem other){
        this.position = new Position(other.getPosition());
        this.blocksMovement = other.blocksMovement;
        this.description = other.description;
        this.visible = other.visible;
    }

    /**
     * Returns the description of this item.
     *
     * @return the item's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this item blocks movement.
     *
     * @return {@code true} if it blocks movement; {@code false} otherwise.
     */
    public boolean getBlockMovement() {
        return blocksMovement;
    }

    /**
     * Returns a string representation of the item including class name, position, movement blocking state,
     * description, and visibility.
     *
     * @return a formatted string describing the item.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "position=" + position +
                ", blocksMovement=" + blocksMovement +
                ", description='" + description + '\'' +
                ", visible=" + visible +
                '}';
    }

    /**
     * Checks if this item is equal to another object.
     *
     * @param obj the object to compare with.
     * @return {@code true} if the other object is a GameItem with the same properties.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GameItem that = (GameItem) obj;
        return blocksMovement == that.blocksMovement &&
                visible == that.visible &&
                position.equals(that.position) &&
                description.equals(that.description);
    }

    /**
     * Returns the item's position on the map.
     *
     * @return the item's {@link Position}.
     */
    @Override
    public Position getPosition() { return position;}

    /**
     * Sets the item's position on the map.
     *
     * @param newPos the new position to assign.
     */
    @Override
    public void setPosition(Position newPos) { this.position = new Position(newPos);}

    /**
     * Sets the visibility state of the item.
     *
     * @param visible {@code true} to make the item visible; {@code false} to hide it.
     * @return always returns {@code true}.
     */
    @Override
    public boolean setVisible(boolean visible) {
        this.visible = visible;
        return true;
    }

    /**
     * Returns whether the item is currently visible on the map.
     *
     * @return {@code true} if the item is visible; {@code false} otherwise.
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public Object callClone() throws CloneNotSupportedException {
        return clone();
    }

}
