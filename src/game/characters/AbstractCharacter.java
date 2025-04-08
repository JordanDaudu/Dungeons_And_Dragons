package game.characters;

import game.combat.Combatant;
import game.core.GameEntity;
import game.map.Position;

import game.engine.RandomUtil;

public abstract class AbstractCharacter implements Combatant,GameEntity {

    // Data Members
    private Position position;
    private int health;
    private int power;
    private double evasionChance = 0.25;

    // Methods
    public AbstractCharacter(Position position, double evasionChance){
        this.position = position;
        this.health = 100;
        this.power = RandomUtil.getRandomInt(4, 15);
        this.evasionChance = evasionChance;
    }

    public Position getPosition(){
        return position;
    }

    public void setPosition(Position pos){
        this.position = pos;
    }

    public boolean tryEvade(){
        return RandomUtil.getRandomInt(4) == 0;
    }
    /*
    public void receiveDamage(int amount, Combatant source){
        if (this.tryEvade())
            return;
        health -= amount;
    }
*/
    public boolean isDead(){
        return (health == 0);
    }

    public void heal(int amount){
        if (isDead())
            return;
        health += amount;
        if(health > 100)
            health = 100;
    }

    public int getPower(){
        return power;
    }

}
