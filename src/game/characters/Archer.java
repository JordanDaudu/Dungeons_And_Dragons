package game.characters;

import game.combat.Combatant;
import game.combat.PhysicalAttacker;
import game.combat.RangedFighter;
import game.engine.RandomUtil;
import game.map.Position;

public class Archer extends PlayerCharacter implements PhysicalAttacker, RangedFighter {

    // data members
    private double accuracy;
    private int range;

    // methods
    public Archer(String name){
        super(name);
        do {
            this.accuracy = RandomUtil.getRandomDouble();
        }
        while(this.accuracy > 0.80);
        range = 2;
    }

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

    public void attack(Combatant target){
        fightRanged(target);
    }

    public double getAccuracy() {
        return accuracy;
    }

    @Override
    public int calculateDamage(Combatant target) {
        if(isCriticalHit())
            return 2 * getPower();
        else
             return getPower();
    }

    @Override
    public boolean isCriticalHit(){
        return RandomUtil.getRandomInt(10) == 0;
    }

    @Override
    public void fightRanged(Combatant target) {
        if(isInRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
        else
            System.out.println("Out of Range!");
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public boolean isInRange(Position self, Position target){
            int distance = self.distanceTo(target);
            return distance == getRange();
    }

    @Override
    public String getDisplaySymbol() {
        return "ARCHER";
    }
}
