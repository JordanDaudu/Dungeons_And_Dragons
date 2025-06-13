package game.decorator;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;

/**
 * A decorator that boosts the attack power of a player character.
 * Adds a fixed amount of power to the decorated player when the ability is used.
 */
public class BoostedAttackPlayerDecorator extends PlayerDecorator {

    // Data Members
    private final int boostAmount = 5;

    // Methods
    /**
     * Constructs a new BoostedAttackPlayerDecorator with the given base player character.
     *
     * @param character the player character to decorate
     */
    public BoostedAttackPlayerDecorator(PlayerCharacter character) {
        super(character);
    }

    /**
     * Copy constructor for cloning.
     *
     * @param other the BoostedAttackPlayerDecorator to copy
     */
    public BoostedAttackPlayerDecorator(BoostedAttackPlayerDecorator other) {
        super(other);
    }

    /**
     * Applies the boosted attack ability by increasing the player's power.
     *
     * @return true if the ability was successfully used, false otherwise
     */
    @Override
    public boolean useAbility() {
        return boostAttack();
    }

    /**
     * Creates a deep clone of this decorator and its decorated player.
     *
     * @return a cloned BoostedAttackPlayerDecorator instance
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected BoostedAttackPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new BoostedAttackPlayerDecorator(clonedBase); // Correct return type
    }

    /**
     * Gets the duration of this ability in milliseconds.
     * This ability is instantaneous, so returns -1.
     *
     * @return -1 indicating the ability has no duration
     */
    @Override
    public int abilityTimeInMilliseconds() {
        return -1;
    }

    /**
     * Increases the decorated player's power by a fixed boost amount.
     *
     * @return true if the power was successfully boosted, false otherwise
     */
    private boolean boostAttack() {
        int currentPower = getDecoratedPlayer().getPower();
        int boostedPower = currentPower + boostAmount;
        if(getDecoratedPlayer() instanceof AbstractCharacter character) {
            character.setPower(boostedPower);
            return true;
        }
        return false;
    }
}
