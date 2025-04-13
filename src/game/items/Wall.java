package game.items;

import game.characters.PlayerCharacter;
import game.map.Position;

/**
 * Represents a wall in the game world.
 * Walls are static obstacles that block player and enemy movement.
 */
public class Wall extends GameItem {

    /**
     * Constructs a Wall at the specified position with a given description.
     * Walls always block movement.
     *
     * @param position    the position of the wall on the map
     * @param description a short description of the wall
     */
    public Wall(Position position, String description) { super(position, true, description); }

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
     *
     * @param c the {@link PlayerCharacter} path is blocked by the wall.
     */
    @Override
    public void interact(PlayerCharacter c) {
        System.out.println("There is wall blocking the path.");
    }

    /**
     * Returns the symbol used to represent a wall on the game map.
     *
     * @return the character symbol "W"
     */
    @Override
    public String getDisplaySymbol() { return "W";}
}
