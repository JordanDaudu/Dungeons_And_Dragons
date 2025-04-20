package game.combat;

import game.core.GameEntity;

/**
 * The Combatant interface represents any entity that can participate in combat.
 * This includes players, enemies, or any entity with health and damage logic.
 */
public interface Combatant extends GameEntity {

    /**
     * Returns the current health of the combatant.
     *
     * @return the current health value
     */
    int getHealth();

    /**
     * Sets the combatant's health to the given value.
     *
     * @param health the new health value
     * @return true if the health was successfully set, false otherwise
     */
    boolean setHealth(int health);

    /**
     * Applies damage to this combatant from a source attacker.
     *
     * @param amount the amount of damage to apply
     * @param source the source of the damage
     */
    void receiveDamage(int amount, Combatant source);

    /**
     * Heals the combatant by the specified amount.
     *
     * @param amount the amount to heal
     */
    void heal(int amount);

    /**
     * Checks whether the combatant is dead (health <= 0).
     *
     * @return true if dead, false otherwise
     */
    boolean isDead();

    /**
     * Returns the combatant's base power (used for calculating damage).
     *
     * @return the power value
     */
    int getPower();

    /**
     * Attempts to evade an attack using default evasion logic.
     *
     * @return true if the attack was evaded, false otherwise
     */
    boolean tryEvade();

    // FROM HERE THOSE ARE MY OWN-ADDED METHODS

    /**
     * Initiates a fight action against the specified target.
     * This method should define how the combatant attacks   another combatant in battle.
     *
     * @param target the combatant to attack
     */
    void fight(Combatant target);

    /**
     * Handles logic for when this combatant is defeated.
     * This can include game over logic, status changes, or triggering events.
     */
    void defeat();

    /**
     * Attempts to evade an attack using a custom multiplier to modify the evasion chance.
     * Override this method to support advanced evasion logic.
     *
     * @param multiplier the evasion modifier (lower = better chance to evade)
     * @return true if the attack was evaded, false otherwise
     */
    default boolean tryEvade(double multiplier) {
        return tryEvade();
    }

    /**
     * Returns the accuracy modifier used when calculating hit chances.
     * Override this if the combatant has accuracy-altering effects.
     *
     * @return the accuracy modifier (default: 0.0 = no change)
     */
    default double getAccuracyModifier() {
        return 0.0; // default: no modification
    }

    /**
     * Returns the elemental type of this combatant, if applicable.
     * Override if the combatant uses magic (e.g. Mage, Dragon).
     *
     * @return the MagicElement this combatant uses (default: null)
     */
    default MagicElement getElementType() {
        return null; // Default: not magical
    }
}
