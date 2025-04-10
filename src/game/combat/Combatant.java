package game.combat;

import game.core.GameEntity;

public interface Combatant extends GameEntity {

    int getHealth();
    boolean setHealth(int health);
    void receiveDamage(int amount, Combatant source);
    void heal(int amount);
    boolean isDead();
    int getPower();
    boolean tryEvade();
}
