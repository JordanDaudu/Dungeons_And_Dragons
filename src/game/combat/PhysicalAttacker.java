package game.combat;

public interface PhysicalAttacker {

    int calculateDamage(Combatant target);
    void attack(Combatant target);
    boolean isCriticalHit();
}
