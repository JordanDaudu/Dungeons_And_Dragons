package game.combat;

import game.map.Position;

public interface RangedFighter {

    void fightRanged(Combatant target);
    int getRange();
    boolean isInRange(Position self, Position target);
}
