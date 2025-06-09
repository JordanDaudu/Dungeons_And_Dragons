package game.characters;

import game.combat.Combatant;
import game.combat.PhysicalAttacker;
import game.combat.RangedFighter;
import game.engine.RandomUtil;
import game.engine.SoundManager;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents an Archer character that specializes in ranged attacks.
 * Inherits from PlayerCharacter and implements ranged and physical combat behavior.
 */
public class Archer extends PlayerCharacter implements PhysicalAttacker, RangedFighter {

    // Data Members
    private double accuracy;
    private int range;

    // methods
    /**
     * Constructs an Archer with a given name.
     * The accuracy is randomly generated but constrained to be ≤ 0.80.
     *
     * @param name the name of the archer
     */
    public Archer(String name){
        super(name);
        do {
            this.accuracy = RandomUtil.getRandomDouble();
        }
        while(this.accuracy > 0.80);
        range = 2;
    }

    /**
     * Constructs an Archer with custom stats.
     * Health and power are added on top of default values. Accuracy is bounded to not exceed 0.80 before the bonus.
     *
     * @param name     the name of the archer
     * @param health   bonus health to add to base
     * @param power    bonus power to add to base
     * @param accuracy bonus accuracy to add to a base-generated value
     */
    public Archer(String name, int health, int power, double accuracy) {
        super(name);
        setMaxHealth(getHealth() + health);
        setHealth(getHealth() + health);
        setPower(getPower() + power);
        do {
            this.accuracy = RandomUtil.getRandomDouble();
        }
        while(this.accuracy > 0.80);
        this.accuracy = this.accuracy + accuracy;
        range = 2;
    }

    /**
     * Copy constructor for Archer.
     * Creates a new Archer with the same stats and attributes as the provided one.
     *
     * @param other the Archer to copy
     */
    public Archer(Archer other){
        super(other);
        this.accuracy = other.accuracy;
        this.range = other.range;
    }

    /**
     * Returns a string representation of the Archer, including inherited and unique fields.
     *
     * @return a string describing the Archer
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +  // Remove the initial class name and '{' from the super.toString()
                ", accuracy = " + accuracy +
                ", range = " + range +
                '}';
    }

    /**
     * Compares this Archer with another object for equality.
     * Includes all inherited and class-specific fields.
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
        Archer archer = (Archer) obj;
        return Double.compare(archer.accuracy, accuracy) == 0 && range == archer.range;
    }

    /**
     * Returns the combat type(s) of the character.
     *
     * @return a string indicating combat roles
     */
    @Override
    public String getType() {
        return "PhysicalAttacker, RangedFighter";
    }

    /**
     * Attacks a target using the Archer's ranged attack method.
     *
     * @param target the target to attack
     */
    public void attack(Combatant target){
        fightRanged(target);
    }

    /**
     * Calculates the damage dealt to a target.
     * May be doubled if a critical hit occurs.
     *
     * @param target the target combatant
     * @return the damage value
     */
    @Override
    public int calculateDamage(Combatant target) {
        if(isCriticalHit())
            return 2 * getPower();
        else
             return getPower();
    }

    /**
     * Determines whether a critical hit occurs.
     * Critical hit has a 10% chance.
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
     * Performs a ranged attack if the target is within range.
     * Prints a message if the target is out of range.
     *
     * @param target the combatant being attacked
     */
    @Override
    public void fightRanged(Combatant target) {
        if(isInRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
    }

    /**
     * Returns the Archer's attack range.
     *
     * @return the range in tiles
     */
    @Override
    public int getRange() {
        return range;
    }

    /**
     * Determines whether the target is within attack range.
     *
     * @param self   the Archer's current position
     * @param target the target's position
     * @return true if target is within range
     */
    @Override
    public boolean isInRange(Position self, Position target){
            int distance = self.distanceTo(target);
            return distance <= getRange();
    }

    /**
     * Gets the accuracy modifier used when calculating evasion chances against this Archer.
     *
     * @return the accuracy value
     */
    @Override
    public double getAccuracyModifier() {
        return accuracy;
    }

    /**
     * Returns the name of the sound effect used when the Archer attacks.
     *
     * @return the attack sound file name
     */
    @Override
    public String getAttackSound() {
        return "bowShot";
    }

    /**
     * Returns a modifier related to the Archer's attack range.
     * May be used to alter game balance dynamically.
     *
     * @return the range modifier
     */
    @Override
    public int getRangeModifier() {
        return range;
    }

    /**
     * Returns the symbol used to represent an Archer on the game map.
     *
     * @return the string "⟨ARCHER⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨ARCHER⟩";
    }

    /**
     * Loads and returns the image used to display the Archer in the GUI.
     *
     * @return an Image object of the Archer
     */
    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/archer.png")));
        return icon.getImage();
    }

    /**
     * Combatant Interface function, helps delegate the fighting logic to use from Interface
     *
     * @param target the combatant to attack
     */
    @Override
    public void fight(Combatant target) {
        fightRanged(target);
    }

    /**
     * Creates and returns a deep copy of this Archer.
     *
     * @return a clone of this instance
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected Archer clone() throws CloneNotSupportedException {
        return new Archer(this);
    }
}
