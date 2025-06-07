package game.items;

import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.logging.GameLogger;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a health potion that can be picked up and used by a {@link PlayerCharacter}.
 * When interacted with, it heals the character by a random amount between 10 and 50 HP.
 */
public class Potion extends GameItem implements Interactable {

    // Data Members
    private final int increaseAmount;
    private boolean isUsed;

    // Methods
    /**
     * Constructs a new Potion with the specified position, movement blocking behavior, and description.
     * The healing amount is initialized randomly.
     *
     * @param position        the position of the potion on the map
     * @param blocksMovement  whether the potion blocks movement
     * @param description     a short description of the potion
     */
    public Potion(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
        increaseAmount = initializeIncreaseAmount();
        isUsed = false;
    }

    public Potion(Potion other) {
        super(other);
        increaseAmount = other.increaseAmount;
        isUsed = other.isUsed;
    }

    /**
     * Returns a string representation of this potion, including its healing amount and usage status.
     *
     * @return a formatted string describing the potion
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", increaseAmount=" + increaseAmount +
                ", isUsed=" + isUsed +
                '}';
    }

    /**
     * Compares this potion to another object for equality.
     * Two potions are equal if they have the same position, description, and internal state.
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
        Potion potion = (Potion) obj;
        return increaseAmount == potion.increaseAmount &&
                isUsed == potion.isUsed;
    }

    /**
     * Gets the amount of HP this potion restores.
     *
     * @return the healing amount
     */
    public int getIncreaseAmount() {
        return increaseAmount;
    }

    /**
     * Returns whether this potion has been used.
     *
     * @return true if used, false otherwise
     */
    protected boolean getIsUsed() { return isUsed; }

    /**
     * Sets the usage status of this potion.
     *
     * @param isUsed whether the potion has been used
     */
    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    /**
     * Allows a {@link PlayerCharacter} to interact with this potion.
     * If the player is adjacent to the potion or if the potion is in the player's inventory,
     * and it hasn't been used yet, the player is healed by the potion's amount.
     * Logs the interaction using {@link GameLogger}.
     *
     * @param c the player character attempting to interact
     */
    @Override
    public void interact(PlayerCharacter c) {
        // The second condition is specially for items inside player inventory
        if(c.getPosition().distanceTo(getPosition()) == 1 || getPosition().equals(new Position(-1, -1))) {
            if(!isUsed) {
                GameLogger.getInstance().log(c.getName() + " used a potion and was healed " + getIncreaseAmount() + "HP");
                c.heal(increaseAmount);
                isUsed = true;
            }
        }
    }

    /**
     * Returns a string describing the interaction effect of this potion,
     * typically the amount of HP it restores.
     *
     * @return a string indicating the healing amount
     */
    @Override
    public String getInteractionDetails() {
        return getIncreaseAmount() + "HP";
    }

    /**
     * Returns the display symbol used to represent this potion on the game map.
     *
     * @return a string symbol for this item
     */
    @Override
    public String getDisplaySymbol() { return "⟨H⟩"; }

    /**
     * Returns the display image used to visually represent this potion in the GUI.
     *
     * @return the image of the potion
     */
    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/life_potion.png")));
        return icon.getImage();
    }

    /**
     * Initializes the healing amount randomly between 10 and 50.
     *
     * @return the randomly generated healing amount
     */
    protected int initializeIncreaseAmount() {
        return RandomUtil.getRandomInt(10, 51);
    }

    @Override
    protected Potion clone() {
        return new Potion(this);
    }
}
