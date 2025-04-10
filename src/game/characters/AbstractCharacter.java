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
    public AbstractCharacter(){
        this.position = null;
        this.health = 100;
        this.power = RandomUtil.getRandomInt(4, 15);
        visible = false;
    }

    public String toString() {
        return "AbstractCharacter{" +
                "position = " + position +
                ", health = " + health +
                ", power = " + power +
                ", evasionChance = " + evasionChance +
                ", visible = " + visible +
                '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractCharacter that = (AbstractCharacter) obj;
        return health == that.health &&
                power == that.power &&
                Double.compare(that.evasionChance, evasionChance) == 0 &&
                visible == that.visible &&
                position.equals(that.position);
    }

    @Override
    public Position getPosition(){
        return position;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean setHealth(int health) {
        this.health = health;
        if(this.health <= 0)
            this.health = 0;
        return true;
    }

    @Override
    public void setPosition(Position pos) { this.position = new Position(pos); }

    @Override
    public boolean tryEvade() { return RandomUtil.getRandomDouble() < evasionChance; }

    protected boolean tryEvade(double multiplier) {
        return RandomUtil.getRandomDouble() < evasionChance * (1 - multiplier);
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
        setHealth(getHealth() - amount);
    }

    @Override
    public boolean isDead(){
        return (health <= 0);
    }

    @Override
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

    @Override
    public int getPower(){
        return power;
    }

}
