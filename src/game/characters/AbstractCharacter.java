package game.characters;

import game.combat.Combatant;
import game.core.GameEntity;
import game.map.Position;

import game.engine.RandomUtil;

public abstract class AbstractCharacter implements Combatant, GameEntity {

    // Data Members
    private Position position;
    private int health;
    private int power;
    private double evasionChance = 0.25;
    private boolean visible;

    // Methods
    public AbstractCharacter(Position position){
        this.position = position;
        this.health = 100;
        this.power = RandomUtil.getRandomInt(4, 15);
        visible = true;
    }

    public Position getPosition(){
        return position;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public boolean setHealth(int health) {
        this.health = health;
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setPosition(Position pos) { this.position = new Position(pos); }

    public boolean tryEvade() { return RandomUtil.getRandomDouble() < evasionChance; }

    public void receiveDamage(int amount, Combatant source) {
        if (this.tryEvade()) {
            System.out.println("Attack evaded!");
            return;
        }
        health -= amount;
        if(this.health < 0)
            this.health = 0;
    }

    public boolean isDead(){
        return (health <= 0);
    }

    public void heal(int amount){
        if (isDead())
            return;
        health += amount;
        if(health > 100)
            health = 100;
    }

    public void addPower(int power) {
        this.power += power;
    }

    public int getPower(){
        return power;
    }

}
