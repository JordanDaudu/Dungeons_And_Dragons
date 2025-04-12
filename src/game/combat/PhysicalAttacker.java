package game.combat;

/**
 * Represents a combatant capable of performing physical attacks.
 * Physical attacks may deal normal or critical damage depending on chance.
 */
public interface PhysicalAttacker {

    /**
     * Calculates the damage dealt to a target based on the attacker's stats,
     * possibly considering critical hits or target defense.
     *
     * @param target the combatant being attacked
     * @return the amount of damage to deal
     */
    int calculateDamage(Combatant target);

    /**
     * Executes a physical attack on the specified target.
     *
     * @param target the combatant being attacked
     */
    void attack(Combatant target);

    /**
     * Determines whether the attack is a critical hit.
     * Critical hits often deal double or enhanced damage.
     *
     * @return true if the attack is a critical hit, false otherwise
     */
    boolean isCriticalHit();
}
