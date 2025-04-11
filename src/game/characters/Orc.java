package game.characters;

import game.combat.Combatant;
import game.combat.MagicAttacker;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.engine.RandomUtil;
import game.map.Position;

public class Orc extends Enemy implements MeleeFighter, PhysicalAttacker {

    private double resistance;

    public Orc() {
        super();
        do {
            this.resistance = RandomUtil.getRandomDouble();
        }
        while(this.resistance > 0.50);
    }

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

    @Override
    public void receiveDamage(int amount, Combatant source) {
        if(source instanceof Archer) {
            if(this.tryEvade(((Archer) source).getAccuracy())) {
                System.out.println("Attack evaded!");
                return;
            }
        }
        else if(this.tryEvade()) {
            System.out.println("Attack evaded!");
            return;
        }
        else if(source instanceof MagicAttacker) {
            setHealth(((int) Math.round(getHealth() - (amount * (1 - resistance)))));
            System.out.println(getClass().getSimpleName() +" received " + amount + " damage!");
            return;
        }
        setHealth(getHealth() - amount);
        System.out.println(getClass().getSimpleName() +" received " + amount + " damage!");
    }

    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition())) {
            if(isCriticalHit())
                target.receiveDamage(2 * getPower(), this);
            else
                target.receiveDamage(getPower(), this);
        }
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
        fightClose(target);
    }

    @Override
    public boolean isCriticalHit() {
        return RandomUtil.getRandomInt(10) == 0;
    }

    @Override
    public String getDisplaySymbol() {
        return "O";
    }
}
