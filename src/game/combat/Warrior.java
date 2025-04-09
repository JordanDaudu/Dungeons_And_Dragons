package game.combat;

import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

public class Warrior extends PlayerCharacter implements MeleeFighter, PhysicalAttacker {

    private int defence;

    public Warrior(Position position, String name, int defence) {
        super(position, name);
        if(defence > 120)
            this.defence = 120;
        else if(defence < 0)
            this.defence = 0;
        else
            this.defence = defence;
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        int damage = (int) (source.getPower() * (1 - Math.min(0.6, defence / 200.0)));
        setHealth(getHealth() - damage);
    }

    @Override
    public void fightClose(Combatant target) {
        // NEED TO IMPLEMENT
        //
        //
        //
    }

    @Override
    public boolean isInMeleeRange(Position self, Position target) {
        int distance = self.distanceTo(target);
        return distance == 1;
    }

    @Override
    public void attack(Combatant target) {
        // NEED TO IMPLEMENT
        // IF CRITICAL HIT MULTIPLY BY 2 DAMAGE
        //
        //
    }

    @Override
    public boolean isCriticalHit() {
        return RandomUtil.getRandomInt(10) == 0;
    }
}
