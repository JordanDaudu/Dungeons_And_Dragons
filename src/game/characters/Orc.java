package game.characters;

import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.engine.RandomUtil;
import game.map.Position;

/**
 * Represents an Orc enemy in the game.
 * Orcs are melee fighters with resistance to magic damage and a chance to evade attacks.
 */
public class Orc extends Enemy implements MeleeFighter, PhysicalAttacker {

    // Data Members
    private double resistance;

    // Methods
    /**
     * Constructs an Orc with randomized magic resistance.
     * The resistance is guaranteed to be between 0 and 0.5.
     */
    public Orc() {
        super();
        do {
            this.resistance = RandomUtil.getRandomDouble();
        }
        while(this.resistance > 0.50);
    }

    /**
     * Returns a string representation of the Orc, including inherited values and resistance.
     *
     * @return a string describing the orc
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", resistance=" + resistance +
                '}';
    }

    /**
     * Compares this Orc to another object for logical equality.
     *
     * @param obj the object to compare
     * @return true if the objects are logically equal
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
        Orc that = (Orc) obj;
        return Double.compare(that.resistance, resistance) == 0;
    }

    /**
     * Receives damage from a source, factoring in magic resistance or potential evasion.
     *
     * @param amount the raw damage amount
     * @param source the source of the attack
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        if(source instanceof MagicAttacker) {
            setHealth(((int) Math.round(getHealth() - (amount * (1 - resistance)))));
            System.out.println(getClass().getSimpleName() +" received " + amount + " damage!");
            return;
        }
        setHealth(getHealth() - amount);
        System.out.println(getClass().getSimpleName() +" received " + amount + " damage!");
    }

    /**
     * Executes a melee attack if the target is within range.
     *
     * @param target the combatant to fight
     */
    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
    }

    /**
     * Checks if the target is in melee range (distance = 1).
     *
     * @param self the position of the orc
     * @param target the position of the target
     * @return true if target is adjacent
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    /**
     * Calculates the damage dealt to a target.
     * Damage is doubled on a critical hit.
     *
     * @param target the target of the attack
     * @return the amount of damage to be dealt
     */
    @Override
    public int calculateDamage(Combatant target) {
        if (isCriticalHit())
            return 2 * getPower();
        else
            return getPower();
    }

    /**
     * Executes an attack action against a target.
     *
     * @param target the target to attack
     */
    @Override
    public void attack(Combatant target) {
        fightClose(target);
    }

    /**
     * Determines whether the orc lands a critical hit.
     * 10% chance to deal double damage.
     *
     * @return true if a critical hit occurs
     */
    @Override
    public boolean isCriticalHit() {
        return RandomUtil.getRandomInt(10) == 0;
    }

    /**
     * Returns the symbol used to represent the orc on the game map.
     *
     * @return the character "O"
     */
    @Override
    public String getDisplaySymbol() {
        return "O";
    }

    /**
     * Combatant Interface function, helps delegate the fighting logic to use from Interface
     *
     * @param target the combatant to attack
     */
    @Override
    public void fight(Combatant target) {
        fightClose(target);
    }
}
