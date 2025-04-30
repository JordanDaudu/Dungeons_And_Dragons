package game.characters;

import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MagicElement;
import game.combat.RangedFighter;
import game.engine.RandomUtil;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a Mage character with magical ranged attack abilities.
 * A Mage has a magical element and can cast spells from a distance.
 */
public class Mage extends PlayerCharacter implements MagicAttacker, RangedFighter {

    // Data Members
    private MagicElement element;
    private int range;

    // Methods
    /**
     * Constructs a Mage with a given name and a randomly assigned magic element.
     *
     * @param name the name of the Mage
     */
    public Mage(String name) {
        super(name);
        this.element = MagicElement.values()[RandomUtil.getRandomInt(4)];
        range = 2; // default
    }

    /**
     * Returns a string representation of the Mage, including inherited and class-specific fields.
     *
     * @return a string describing the Mage
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", element = " + element +
                ", range = " + range +
                '}';
    }

    /**
     * Compares this Mage to another object for logical equality.
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
        Mage mage = (Mage) obj;
        return range == mage.range && element == mage.element;
    }

    @Override
    public String getType() {
        return "MagicAttacker, RangedFighter";
    }

    /**
     * Calculates the magical damage dealt to a target.
     * Adjusts damage based on element type interactions and power.
     *
     * @param target the target combatant
     * @return the damage dealt as a long
     */
    @Override
    public long calculateMagicDamage(Combatant target) {
        if(target instanceof MagicAttacker) {
            if(element.isStrongerThan(target.getElementType()))
                return Math.round(1.2 * (getPower() * 1.5));
            else if(element.isWeakerThan(target.getElementType()))
                return Math.round(0.8 * (getPower() * 1.5));
        }
        return Math.round(getPower() * 1.5);
    }

    /**
     * Casts a spell on the target by performing a ranged fight.
     *
     * @param target the target combatant
     */
    @Override
    public void castSpell(Combatant target) {
        fightRanged(target);
    }

    /**
     * Returns the Mage's elemental type.
     *
     * @return the magic element
     */
    @Override
    public MagicElement getElement() {
        return element;
    }

    /**
     * Checks if this Mage's element is stronger than another MagicAttacker's element.
     *
     * @param other the other MagicAttacker
     * @return true if this element is stronger
     */
    @Override
    public boolean isElementStrongerThan(MagicAttacker other) {
        return element.isStrongerThan(other.getElement());
    }

    /**
     * Gets the element type of this Mage for combat purposes.
     *
     * @return the Mage's magic element
     */
    @Override
    public MagicElement getElementType() {
        return getElement();
    }

    /**
     * Performs a ranged attack against a target if it is in range.
     *
     * @param target the target to attack
     */
    @Override
    public void fightRanged(Combatant target) {
        if(isInRange(getPosition(), target.getPosition()))
            target.receiveDamage(((int) calculateMagicDamage(target)), this);
    }

    /**
     * Returns the attack range of the Mage.
     *
     * @return the range in tiles
     */
    @Override
    public int getRange() {
        return range;
    }

    /**
     * Determines whether a target is within range for a ranged attack.
     *
     * @param self   the position of the Mage
     * @param target the position of the target
     * @return true if the target is in range
     */
    @Override
    public boolean isInRange(Position self, Position target) {
        return self.distanceTo(target) <= getRange();
    }

    /**
     * Returns the symbol used to display the Mage on the map.
     *
     * @return the string "⟨MAGE⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨MAGE⟩";
    }

    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/mage.png")));
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
}
