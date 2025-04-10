package game.combat;

public interface MagicAttacker {

    long calculateMagicDamage(Combatant target);
    void castSpell(Combatant target);
    MagicElement getElement();
    boolean isElementStrongerThan(MagicAttacker other);
}
