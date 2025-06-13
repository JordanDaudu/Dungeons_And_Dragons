package game.decorator;

import game.characters.PlayerCharacter;
import game.combat.Combatant;

/**
 * A decorator that reduces incoming damage to the player, simulating a shield effect.
 */
public class ShieldedPlayerDecorator extends PlayerDecorator{

    // Methods
    /**
     * Constructs a ShieldedPlayerDecorator that wraps the given player character.
     *
     * @param character the player character to decorate
     */
    public ShieldedPlayerDecorator(PlayerCharacter character) {
        super(character);
    }

    /**
     * Copy constructor used for cloning.
     *
     * @param other the ShieldedPlayerDecorator to copy
     */
    public ShieldedPlayerDecorator(ShieldedPlayerDecorator other) {
        super(other);
    }

    /**
     * The shield ability does not have an active use and returns false.
     *
     * @return false, as this ability is passive
     */
    @Override
    public boolean useAbility() {
        return false;
    }

    /**
     * Creates a deep clone of this decorator and its decorated player.
     *
     * @return a cloned ShieldedPlayerDecorator instance
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected ShieldedPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new ShieldedPlayerDecorator(clonedBase);
    }

    /**
     * Returns the duration of the ability in milliseconds.
     *
     * @return -1, indicating indefinite or passive ability duration
     */
    @Override
    public int abilityTimeInMilliseconds() {
        return -1;
    }

    /**
     * Reduces incoming damage by 5% before passing it to the decorated player.
     *
     * @param amount the original amount of damage
     * @param source the combatant causing the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage((int) (amount * 0.95), source);
    }
}
