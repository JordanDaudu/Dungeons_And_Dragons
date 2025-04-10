package game.characters;

import game.combat.*;
import game.engine.RandomUtil;
import game.map.Position;

public class Dragon extends Enemy implements MagicAttacker, RangedFighter, MeleeFighter, PhysicalAttacker {

    private MagicElement element;
    private int range;

    public Dragon() {
        super();
        this.element = MagicElement.values()[RandomUtil.getRandomInt(4)];
        range = 2;
    }

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
    public long calculateMagicDamage(Combatant target) {
        if(target instanceof MagicAttacker) {
            if(element.isStrongerThan(((MagicAttacker) target).getElement()))
                return Math.round(1.2 * (getPower() * 1.5));
            else
                return Math.round(0.8 * (getPower() * 1.5));
        }
        return Math.round(getPower() * 1.5);
    }

    @Override
    public void castSpell(Combatant target) {
        if(isInRange(getPosition(), target.getPosition()))
            target.receiveDamage(((int) calculateMagicDamage(target)), this);
    }

    @Override
    public MagicElement getElement() {
        return element;
    }

    @Override
    public boolean isElementStrongerThan(MagicAttacker other) {
        return element.isStrongerThan(other.getElement());
    }

    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
    }

    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    @Override
    public int calculateDamage(Combatant target) {
        return getPower();
    }

    @Override
    public void attack(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            fightClose(target);
        else if(isInRange(getPosition(), target.getPosition()))
            fightRanged(target);
    }

    @Override
    public boolean isCriticalHit() {
        return RandomUtil.getRandomInt(10) == 0;
    }

    @Override
    public void fightRanged(Combatant target) {
        castSpell(target);
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean isInRange(Position self, Position target) {
        if(self.distanceTo(target) <= getRange())
            return true;
        return false;
    }

    @Override
    public String getDisplaySymbol() {
        return "D";
    }
}
