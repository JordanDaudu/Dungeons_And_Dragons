package game.characters;

import game.combat.Combatant;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.engine.RandomUtil;
import game.map.Position;

public class Goblin extends Enemy implements PhysicalAttacker, MeleeFighter {

    // data members
    private int agility;

    //methods
    public Goblin() {
        super();
        this.agility = RandomUtil.getRandomInt(0, 81);
    }

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

    @Override
    public boolean tryEvade() {
        double goblin_evasion = Math.min(0.8, agility / 100.0);
        return RandomUtil.getRandomDouble() < goblin_evasion;
    }

    @Override
    public boolean isCriticalHit() {
        return RandomUtil.getRandomInt(10) == 0;
    }

    @Override
    public int calculateDamage(Combatant target) {
        if (isCriticalHit())
            return 2 * getPower();
        else
            return getPower();
    }

    @Override
    public void attack(Combatant target){
        fightClose(target);
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
    public String getDisplaySymbol() {
        return "G";
    }
}




