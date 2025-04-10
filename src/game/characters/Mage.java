package game.characters;

import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MagicElement;
import game.combat.RangedFighter;
import game.engine.RandomUtil;
import game.map.Position;

public class Mage extends PlayerCharacter implements MagicAttacker, RangedFighter {

    private MagicElement element;
    private int range;

    public Mage(String name) {
        super(name);
        this.element = MagicElement.values()[RandomUtil.getRandomInt(4)];
        range = 2; // default
    }

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
        fightRanged(target);
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
    public void fightRanged(Combatant target) {
        if(isInRange(getPosition(), target.getPosition()))
            target.receiveDamage(((int) calculateMagicDamage(target)), this);
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean isInRange(Position self, Position target) {
        return self.distanceTo(target) <= getRange();
    }

    @Override
    public String getDisplaySymbol() {
        return "MAGE";
    }
}
