package game.characters;

import game.combat.Combatant;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.engine.RandomUtil;
import game.engine.SoundManager;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a Warrior character that specializes in close-range combat.
 * Inherits from PlayerCharacter and implements melee fighting and physical attacking behavior.
 */
public class Warrior extends PlayerCharacter implements MeleeFighter, PhysicalAttacker {

    // Data Members
    private final int defence;

    // Methods
    /**
     * Constructs a Warrior with the given name.
     * Randomly initializes the defense stat between 0 and 120.
     *
     * @param name the name of the warrior
     */
    public Warrior(String name) {
        super(name);
        defence = RandomUtil.getRandomInt(0, 121);
    }

    /**
     * Constructs a Warrior with custom stats.
     * Health and power are added to base values. Defence is randomized and increased by the given value.
     *
     * @param name    the name of the warrior
     * @param health  bonus health to add to base
     * @param power   bonus power to add to base
     * @param defence bonus defense to add to a randomly generated base
     */
    public Warrior(String name, int health, int power, int defence) {
        super(name);
        setMaxHealth(getHealth() + health);
        setHealth(getHealth() + health);
        setPower(getPower() + power);
        this.defence = RandomUtil.getRandomInt(0, 121) + defence;
    }

    /**
     * Copy constructor for Warrior.
     * Initializes a new Warrior with the same state as the given one.
     *
     * @param other the Warrior to copy
     */
    public Warrior(Warrior other){
        super(other);
        defence = other.defence;
    }

    /**
     * Returns a string representation of the Warrior including inherited
     * fields and the defense stat.
     *
     * @return string describing the warrior
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", defence = " + defence +
                '}';
    }

    /**
     * Checks if two Warrior objects are equal.
     * Compares all inherited fields as well as the defense stat.
     *
     * @param obj the object to compare with
     * @return true if they are equal in state
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
        Warrior warrior = (Warrior) obj;
        return defence == warrior.defence;
    }

    public int getDefence() {return defence;}

    /**
     * Gets the type description of the warrior.
     *
     * @return a string describing the combat roles of the warrior
     */
    @Override
    public String getType() {
        return "PhysicalAttacker, MeleeFighter";
    }

    /**
     * Handles receiving damage from an attacker and updates the last damage amount received.
     * Damage is reduced based on the warrior's defense.
     *
     * @param amount the base amount of incoming damage
     * @param source the combatant causing the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        if(source != null)
            amount = (int) (source.getPower() * (1 - Math.min(0.6, defence / 200.0)));
        setHealth(getHealth() - amount);
        setLastDamageReceived(amount);
    }

    /**
     * Performs a melee attack on a target if in melee range.
     *
     * @param target the target to attack
     */
    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
    }

    /**
     * Determines if the target is in melee range (Manhattan distance of 1).
     *
     * @param self   the warrior's current position
     * @param target the target's position
     * @return true if within melee range
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    /**
     * Calculates the amount of damage to deal to a target.
     * May double the damage if a critical hit occurs.
     *
     * @param target the combatant being attacked
     * @return the damage amount
     */
    @Override
    public int calculateDamage(Combatant target) {
        if(isCriticalHit())
            return 2 * getPower();
        else
            return getPower();
    }

    /**
     * Attacks the target using the melee fighting method.
     *
     * @param target the combatant to attack
     */
    @Override
    public void attack(Combatant target) {
        fightClose(target);
    }

    /**
     * Determines whether a critical hit occurs.
     * A critical hit occurs with 10% probability.
     *
     * @return true if it's a critical hit
     */
    @Override
    public boolean isCriticalHit() {
        if(RandomUtil.getRandomInt(10) == 0) {
            SoundManager.playEffect("criticalHit");
            return true;
        }
        return false;
    }

    /**
     * Gets the symbol used to represent a warrior on the map.
     *
     * @return "⟨WARRIOR⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨WARRIOR⟩";
    }

    /**
     * Retrieves the display image associated with the warrior character.
     *
     * @return an Image object representing the warrior
     */
    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/warrior.png")));
        return icon.getImage();
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

    @Override
    public boolean isPhysicalAttacker() {
        return true;
    }

    @Override
    public boolean isMagicAttacker() {
        return false;
    }

    @Override
    public boolean isMeleeFighter() {
        return true;
    }

    @Override
    public boolean isRangedFighter() {
        return false;
    }

    /**
     * Returns the sound effect to play when the warrior attacks.
     *
     * @return the name of the attack sound
     */
    @Override
    public String getAttackSound() {
        return "swordSwing";
    }

    /**
     * Creates and returns a deep copy of this Warrior.
     *
     * @return a clone of this Warrior
     * @throws CloneNotSupportedException if the Warrior cannot be cloned
     */
    @Override
    protected Warrior clone() throws CloneNotSupportedException {
        return new Warrior(this);
    }
}
