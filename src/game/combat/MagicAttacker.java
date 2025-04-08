package game.combat;

public interface MagicAttacker {

    void calculateMagicDamage(Combatant target);
    void castSpell(Combatant target);
    MagicElement getElement();
    boolean isElementStrongerThan(MagicAttacker other);
}
