package game.combat;

public interface Combatant {

    int getHealth();
    boolean setHealth();
    void receiveDamage(int amount, Combatant source);
    void heal(int amount);
    boolean isDead();
    int getPower();
    boolean tryEvade();
}
