package game.combat;

import game.characters.Enemy;
import game.characters.PlayerCharacter;

public class CombatSystem {

    public CombatSystem() {}

    public void resolveCombat(Combatant attacker, Combatant defender) {

        if(attacker instanceof MeleeFighter)
            ((MeleeFighter) attacker).fightClose(defender);
        else if(attacker instanceof RangedFighter)
            ((RangedFighter) attacker).fightRanged(defender);
        if(defender.isDead()) {
            if(defender instanceof PlayerCharacter) {
                System.out.println("\n||GAME OVER!||\n");
                System.out.println("You got in total: " + ((PlayerCharacter) defender).getTreasurePoints() + " treasure points");
            }
            else if(defender instanceof Enemy) {
                ((Enemy) defender).defeat();
            }
        }

    }
}
