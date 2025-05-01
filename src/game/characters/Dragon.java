package game.characters;

import game.combat.*;
import game.engine.RandomUtil;
import game.map.Position;

import java.awt.Image;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * Represents a Dragon enemy character.
 * Dragons are powerful foes capable of both magic and physical attacks,
 * and can engage enemies at both melee and ranged distances.
 */
public class Dragon extends Enemy implements MagicAttacker, RangedFighter, MeleeFighter, PhysicalAttacker {

    //Data Members
    private MagicElement element;
    private int range;

    // Methods
    /**
     * Constructs a Dragon with a random magic element and default range.
     */
    public Dragon() {
        super();
        this.element = MagicElement.values()[RandomUtil.getRandomInt(4)];
        range = 2;
    }

    /**
     * Returns a string representation of the Dragon, including base info, element, and range.
     *
     * @return a string describing the dragon
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", element=" + element +
                ", range=" + range +
                '}';
    }

    /**
     * Checks equality between this Dragon and another object.
     *
     * @param obj the object to compare
     * @return true if they are logically equal
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
        Dragon that = (Dragon) obj;
        return range == that.range && element == that.element;
    }

    @Override
    public String getType() {
        return "MagicAttacker, RangedFighter, MeleeFighter, PhysicalAttacker";
    }

    /**
     * Calculates the magic damage dealt to a target.
     * Elemental strengths and weaknesses are taken into account.
     *
     * @param target the target of the spell
     * @return the magic damage value
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
     * Calculates physical damage dealt to a target.
     *
     * @param target the combatant to hit
     * @return the damage amount
     */
    @Override
    public int calculateDamage(Combatant target) {
        return getPower();
    }

    /**
     * Casts a spell on the target if within range.
     *
     * @param target the target to damage
     */
    @Override
    public void castSpell(Combatant target) {
        if(isInRange(getPosition(), target.getPosition()))
            target.receiveDamage(((int) calculateMagicDamage(target)), this);
    }

    /**
     * Returns the dragon's magic element.
     *
     * @return the element
     */
    @Override
    public MagicElement getElement() {
        return element;
    }

    /**
     * Determines if the dragon's element is stronger than another attacker's.
     *
     * @param other the other magic attacker
     * @return true if this dragon's element is stronger
     */
    @Override
    public boolean isElementStrongerThan(MagicAttacker other) {
        return element.isStrongerThan(other.getElement());
    }

    /**
     * Returns the dragon's element as its type.
     *
     * @return the magic element type
     */
    @Override
    public MagicElement getElementType() {
        return getElement();
    }

    @Override
    public int getRangeModifier() {
        return range;
    }

    /**
     * Performs a melee attack if the target is adjacent.
     *
     * @param target the combatant to attack
     */
    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
    }

    /**
     * Checks if the target is in melee range (distance = 1).
     *
     * @param self the position of the dragon
     * @param target the position of the target
     * @return true if adjacent
     */
    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    /**
     * Attacks a target using melee or ranged combat depending on distance.
     *
     * @param target the target to attack
     */
    @Override
    public void attack(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            fightClose(target);
        else if(isInRange(getPosition(), target.getPosition()))
            fightRanged(target);
    }

    /**
     * Determines whether the dragon performs a critical hit.
     *
     * @return true if a critical hit occurred (always false as enemies do not crit)
     */
    @Override
    public boolean isCriticalHit() {
        return false;
    }

    /**
     * Performs a ranged magic attack on a target.
     *
     * @param target the target to attack
     */
    @Override
    public void fightRanged(Combatant target) {
        castSpell(target);
    }

    /**
     * Returns the spell casting range of the dragon.
     *
     * @return the range value
     */
    @Override
    public int getRange() {
        return range;
    }

    /**
     * Determines if the target is within ranged spell casting distance.
     *
     * @param self the dragon's position
     * @param target the target's position
     * @return true if target is in range
     */
    @Override
    public boolean isInRange(Position self, Position target) {
        if(self.distanceTo(target) <= getRange())
            return true;
        return false;
    }

    /**
     * Returns the symbol used to represent the dragon on the map.
     *
     * @return the character "⟨D⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨D⟩";
    }

    @Override
    public Image getDisplayImage() {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/dragon.png")));
        return icon.getImage();
    }

    /**
     * Combatant Interface function, helps delegate the fighting logic to use from Interface
     *
     * @param target the combatant to attack
     */
    @Override
    public void fight(Combatant target) {
        attack(target);
    }

    @Override
    public String getDescription(){
        return "All-around powerhouse, strong in magic, ranged, and melee attacks.";
    }

}
