package game.combat;

/**
 * Represents an entity capable of performing magical attacks.
 * Typically implemented by characters like Mages or Dragons.
 */
public interface MagicAttacker {

    /**
     * Calculates the magic damage dealt to a target.
     * Elemental matchups may affect the final damage.
     *
     * @param target the combatant being attacked
     * @return the amount of magic damage to apply
     */
    long calculateMagicDamage(Combatant target);

    /**
     * Performs a spell cast on the target.
     * The specific behavior depends on the implementing class.
     *
     * @param target the target of the spell
     */
    void castSpell(Combatant target);

    /**
     * Returns the magic element type used by this attacker.
     *
     * @return the MagicElement of this attacker
     */
    MagicElement getElement();

    /**
     * Compares this attacker's element to another.
     *
     * @param other the other magic attacker to compare against
     * @return true if this attacker's element is stronger, false otherwise
     */
    boolean isElementStrongerThan(MagicAttacker other);
}
