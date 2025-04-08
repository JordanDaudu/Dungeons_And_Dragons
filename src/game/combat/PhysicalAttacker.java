package game.combat;

public interface PhysicalAttacker {

    void attack(Combatant target);
    boolean isCriticalHit();
}
