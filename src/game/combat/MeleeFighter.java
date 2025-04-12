package game.combat;

import game.map.Position;

/**
 * Represents an entity capable of engaging in melee combat.
 * Melee fighters typically need to be adjacent to their target to attack.
 */
public interface MeleeFighter {

    /**
     * Executes a close-range melee attack on the given target.
     *
     * @param target the combatant being attacked
     */
    void fightClose(Combatant target);

    /**
     * Determines whether the target is within melee range of the attacker.
     * Typically, melee range is considered to be a distance of 1.
     *
     * @param self the position of the attacker
     * @param target the position of the target
     * @return true if the target is in melee range, false otherwise
     */
    boolean isInMeleeRange(Position self, Position target);
}
