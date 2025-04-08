package game.combat;

import game.map.Position;

public interface MeleeFighter {

    void fightClose(Combatant target);
    boolean isInMeleeRange(Position self, Position target);
}
