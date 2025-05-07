package game.items;

import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a collectible treasure item in the game world.
 * A treasure can either grant treasure points or provide a random item (Potion or PowerPotion) upon interaction.
 */
public class Treasure extends GameItem implements Interactable {

    // Data Members
    private final int value;
    boolean collected;

    // Methods
    /**
     * Constructs a Treasure with a random value between 100 and 300.
     *
     * @param position        the position of the treasure
     * @param blocksMovement  whether the treasure blocks movement on the map
     * @param description     a short description of the treasure
     */
    public Treasure(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
        this.value = RandomUtil.getRandomInt(100, 301);
        collected = false;
    }

    /**
     * Constructs a Treasure with a fixed loot value.
     *
     * @param position        the position of the treasure
     * @param blocksMovement  whether the treasure blocks movement on the map
     * @param description     a short description of the treasure
     * @param loot            the specific value of the treasure
     */
    public Treasure(Position position, boolean blocksMovement, String description, int loot) {
        super(position, blocksMovement, description);
        this.value = loot;
        collected = false;
    }

    /**
     * Returns a string representation of this Treasure, including its position, description,
     * value, and collected state.
     *
     * @return a formatted string representation
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", value=" + value +
                ", collected=" + collected +
                '}';
    }

    /**
     * Checks equality with another object based on treasure properties and superclass comparison.
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
        if (!super.equals(obj)) {
            return false;
        }
        Treasure treasure = (Treasure) obj;
        return value == treasure.value &&
                collected == treasure.collected;
    }

    /**
     * Allows a {@link PlayerCharacter} to interact with the treasure.
     * If adjacent and not already collected, it gives treasure points or items randomly.
     *
     * @param c the player character interacting with the treasure
     */
    @Override
    public void interact(PlayerCharacter c) {
        if(c.getPosition().distanceTo(getPosition()) == 1) {
            if(!collected) {
                collected = true;
                int randomizer = RandomUtil.getRandomInt(0, 6);
                if(randomizer >= 0 && randomizer <= 2) {
                    // ADD TREASURE POINTS
                    if(c.updateTreasurePoints(value))
                        System.out.println(c.getName() + " gained " + getValue() + " treasure points!");
                }
                // Items in the inventory don't have a position on the map so we initialize as (-1, -1)
                else if(randomizer >= 3 && randomizer <= 4) {
                    // ADD TO INVENTORY POTION
                    if(c.addToInventory(new Potion(new Position(-1, -1), false, "Potion")))
                        System.out.println("Potion added to " + c.getName() + "'s inventory!");
                }
                else {
                    // ADD TO INVENTORY POWER POTION
                    if(c.addToInventory(new PowerPotion(new Position(-1, -1), false, "Power Potion")))
                        System.out.println("Power Potion added to " + c.getName() + "'s inventory!");
                }
            }
        }
    }

    /**
     * Returns a string describing the interaction effect of the treasure,
     * typically indicating the amount of treasure points it provides.
     *
     * @return a string indicating the treasure value
     */
    @Override
    public String getInteractionDetails() {
        return getValue() + " Points";
    }

    /**
     * Returns the symbol used to represent treasure on the game map.
     *
     * @return the character symbol "T"
     */
    @Override
    public String getDisplaySymbol() { return "⟨T⟩"; }

    /**
     * Returns the display image used to visually represent this treasure in the GUI.
     *
     * @return the image of the treasure
     */
    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/treasure.png")));
        return icon.getImage();
    }

    /**
     * Returns the value of this treasure.
     *
     * @return the treasure value
     */
    private int getValue() {
        return value;
    }
}
