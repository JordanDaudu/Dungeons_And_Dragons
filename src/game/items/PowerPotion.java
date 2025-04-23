package game.items;

import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

/**
 * Represents a power potion that can be picked up and used by a {@link PlayerCharacter}.
 * When interacted with, it increases the player's power points (PP) by a random amount.
 */
public class PowerPotion extends Potion {

    // Methods
    /**
     * Constructs a new PowerPotion with the specified position, movement blocking behavior, and description.
     *
     * @param position        the position of the power potion on the map
     * @param blocksMovement  whether the potion blocks movement
     * @param description     a short description of the power potion
     */
    public PowerPotion(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
    }

    /**
     * Returns a string representation of this power potion, including inherited potion fields.
     *
     * @return a formatted string describing the power potion
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                "}";
    }

    /**
     * Compares this power potion to another object for equality.
     * Two power potions are equal if they share the same position, description, and internal state.
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }

    /**
     * Allows a {@link PlayerCharacter} to interact with this power potion.
     * If the player is adjacent and the potion hasn't been used, the player gains power.
     *
     * @param c the player character attempting to interact
     */
    @Override
    public void interact(PlayerCharacter c) {
        // The second condition is specially for items inside player inventory
        if(c.getPosition().distanceTo(getPosition()) == 1 || getPosition().equals(new Position(-1, -1))) {
            if(!getIsUsed()) {
                System.out.println(c.getName() + " was powered up " + getIncreaseAmount() + "PP");
                c.addPower(getIncreaseAmount());
                setIsUsed(true);
            }
        }
    }

    /**
     * Returns the display symbol used to represent this power potion on the game map.
     *
     * @return a string symbol for this item
     */
    @Override
    public String getDisplaySymbol() { return "⟨P⟩"; }

    /**
     * Initializes the power boost amount randomly between 1 and 5.
     *
     * @return the randomly generated power increase amount
     */
    @Override
    protected int initializeIncreaseAmount() {
        return RandomUtil.getRandomInt(1, 6);
    }
}
