package game.characters;

import game.combat.Combatant;
import game.combat.MeleeFighter;
import game.combat.PhysicalAttacker;
import game.engine.RandomUtil;
import game.map.Position;

public class Warrior extends PlayerCharacter implements MeleeFighter, PhysicalAttacker {

    private int defence;

    public Warrior(String name) {
        super(name);
        defence = RandomUtil.getRandomInt(0, 121);
    }

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


    @Override
    public void receiveDamage(int amount, Combatant source) {
        int damage = (int) (source.getPower() * (1 - Math.min(0.6, defence / 200.0)));
        setHealth(getHealth() - damage);
    }

    @Override
    public void fightClose(Combatant target) {
        if(isInMeleeRange(getPosition(), target.getPosition()))
            target.receiveDamage(calculateDamage(target), this);
        else
            System.out.println("Out of range!");
    }

    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    @Override
    public int calculateDamage(Combatant target) {
        if(isCriticalHit())
            return 2 * getPower();
        else
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
        return "WARRIOR";
    }
}
