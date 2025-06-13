package game.items;

import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a wall in the game world.
 * Walls are static obstacles that block player and enemy movement.
 */
public class Wall extends GameItem {

    // Methods
    /**
     * Constructs a Wall at the specified position with a given description.
     * Walls always block movement.
     *
     * @param position    the position of the wall on the map
     * @param description a short description of the wall
     */
    public Wall(Position position, String description) { super(position, true, description); }

    /**
     * Copy constructor to create a new Wall by copying another Wall.
     *
     * @param other the Wall to copy
     */
    public Wall(Wall other) { super(other); }

    /**
     * Returns a string representation of this Wall, including inherited fields.
     *
     * @return a formatted string with the wall's properties
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                '}';
    }

    /**
     * Checks equality with another object based on superclass comparison.
     *
     * @param obj the object to compare to
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns the symbol used to represent a wall on the game map.
     *
     * @return the character symbol "W"
     */
    @Override
    public String getDisplaySymbol() { return "⟨W⟩";}

    /**
     * Returns the image used to visually represent the wall in the UI.
     *
     * @return the wall's image icon
     */
    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/wall.png")));
        return icon.getImage();
    }

    /**
     * Creates and returns a deep copy of this Wall.
     *
     * @return a cloned Wall object
     */
    @Override
    public Wall clone() {
        return new Wall(this);
    }
}
