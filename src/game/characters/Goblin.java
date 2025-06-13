package game.characters;

import game.combat.Combatant;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.engine.RandomUtil;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a Goblin enemy in the game.
 * Goblins are melee fighters with a chance to evade attacks based on agility.
 */
public class Goblin extends Enemy implements PhysicalAttacker, MeleeFighter {

    // data members
    private final int agility;

    //methods
    /**
     * Constructs a Goblin with randomized agility.
     * Agility is a value between 0 and 80 (inclusive).
     */
    public Goblin() {
        super();
        this.agility = RandomUtil.getRandomInt(0, 81);
    }

    /**
     * Constructs a Goblin with specified attributes.
     *
     * @param health the goblin's maximum and current health
     * @param power the goblin's attack power
     * @param loot the loot value dropped by the goblin upon defeat
     * @param agility the goblin's agility, used for evasion chances
     */
    public Goblin(int health, int power, int loot, int agility) {
        super();
        setMaxHealth(health);
        setHealth(health);
        setPower(power);
        setLoot(loot);
        this.agility = agility;
    }

    /**
     * Copy constructor for Goblin.
     *
     * @param other the Goblin instance to copy
     */
    public Goblin(Goblin other){
        super(other);
        this.agility = other.agility;
    }

    /**
     * Returns a string representation of the Goblin, including inherited data and agility.
     *
     * @return a string describing the goblin
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", agility=" + agility +
                '}';
    }

    /**
     * Compares this Goblin to another object for logical equality.
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
        Goblin that = (Goblin) obj;
        return agility == that.agility;
    }

    /**
     * Returns the goblin's agility value.
     *
     * @return the agility stat
     */
    public int getAgility() {return agility;}

    /**
     * Returns the type(s) of combat roles the Goblin fulfills.
     *
     * @return a string listing the Goblin's combat roles
     */
    @Override
    public String getType() {
        return "PhysicalAttacker, MeleeFighter";
    }

    /**
     * Attempts to evade an attack using the goblin's agility.
     * Goblins can evade with a probability based on their agility (max 80%).
     *
     * @return true if the goblin successfully evades the attack
     */
    @Override
    public boolean tryEvade() {
        double goblin_evasion = Math.min(0.8, agility / 100.0);
        return RandomUtil.getRandomDouble() < goblin_evasion;
    }

    /**
     * Attempts to evade an attack using a multiplier to the base agility-based evasion.
     *
     * @param multiplier evasion penalty between 0 and 1 (lower = evasion goes down)
     * @return true if the goblin successfully evades the attack
     */
    @Override
    public boolean tryEvade(double multiplier) {
        if(multiplier < 0 || multiplier > 1)
            System.err.println("Multiplier isn't between 0 and 1, error in calculation may occur");
        double goblin_evasion = Math.min(0.8, agility / 100.0);
        return RandomUtil.getRandomDouble() < (goblin_evasion * (1 - multiplier));
    }

    /**
     * Determines if a critical hit occurs.
     *
     * @return true if a critical hit occurs (always false as enemies do not crit)
     */
    @Override
    public boolean isCriticalHit() {
        return false;
    }

    /**
     * Calculates the damage dealt to a target.
     *
     * @param target the target of the attack
     * @return the amount of damage to be dealt
     */
    @Override
    public int calculateDamage(Combatant target) {
        // Enemies do not have a critical multiplier therefore
        return getPower();
    }

    /**
     * Performs an attack on the target using melee combat.
     *
     * @param target the target to attack
     */
    @Override
    public void attack(Combatant target){
        fightClose(target);
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
     * Checks if the target is in melee range (adjacent).
     *
     * @param self the goblin's position
     * @param target the target's position
     * @return true if distance is exactly 1
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    /**
     * Returns the symbol used to represent the goblin on the game map.
     *
     * @return the character "⟨G⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨G⟩";
    }

    /**
     * Returns an image representing the Goblin for the game UI.
     * The image is loaded from the resource path /images/goblin.png.
     *
     * @return the goblin's image
     */
    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/goblin.png")));
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

    /**
     * Indicates whether the goblin uses physical attacks.
     *
     * @return true
     */
    @Override
    public boolean isPhysicalAttacker() {
        return true;
    }

    /**
     * Indicates whether the goblin uses magic attacks.
     *
     * @return false
     */
    @Override
    public boolean isMagicAttacker() {
        return false;
    }

    /**
     * Indicates whether the goblin is a melee fighter.
     *
     * @return true
     */
    @Override
    public boolean isMeleeFighter() {
        return true;
    }

    /**
     * Indicates whether the goblin is a ranged fighter.
     *
     * @return false
     */
    @Override
    public boolean isRangedFighter() {
        return false;
    }

    /**
     * Provides a textual description of the Goblin for the GUI.
     *
     * @return the goblin's description
     */
    @Override
    public String getDescription(){
        return "Fast and evasive, excels in melee with high dodge and swift strikes.";
    }

    /**
     * Creates a clone of this Goblin.
     *
     * @return a cloned Goblin object
     * @throws CloneNotSupportedException if cloning is not supported
     */
    @Override
    protected Goblin clone() throws CloneNotSupportedException {
        return new Goblin(this);
    }

    /**
     * Returns the display name of this enemy type.
     *
     * @return the string "Goblin"
     */
    @Override
    public String getEnemyTypeName(){
        return "Goblin";
    }

}