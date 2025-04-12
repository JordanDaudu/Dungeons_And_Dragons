package game.combat;

import game.map.Position;

/**
 * Represents a combatant capable of performing ranged attacks.
 * Ranged attacks can be executed from a distance, depending on the attacker's range.
 */
public interface RangedFighter {

    /**
     * Executes a ranged attack on the specified target.
     *
     * @param target the combatant being attacked
     */
    void fightRanged(Combatant target);

    /**
     * Returns the maximum range (in tiles) from which this combatant can perform a ranged attack.
     *
     * @return the attack range
     */
    int getRange();

    /**
     * Determines if the target is within range for a ranged attack.
     *
     * @param self the position of the attacker
     * @param target the position of the defender
     * @return true if the target is within range, false otherwise
     */
    boolean isInRange(Position self, Position target);
}
