package game.combat;

import game.characters.Archer;
import game.characters.Enemy;
import game.engine.SoundManager;

/**
 * The CombatSystem class handles combat resolution between two Combatants.
 * It determines the type of attack to perform and handles post-combat effects such as death.
 */
public class CombatSystem {

    // Methods
    /**
     * Constructs a new CombatSystem instance.
     */
    public CombatSystem() {}

    /**
     * Resolve combat interaction between an attacker and a defender. The attacker will attempt
     * to attack the defender using either melee or ranged capabilities, considering evasion logic.
     * If the attack is successful and the defender dies, the appropriate outcome (game over or defeat)
     * will be triggered.
     *
     * @param attacker the combatant initiating the attack
     * @param defender the target of the attack
     */
    public void resolveCombat(Combatant attacker, Combatant defender) {

        System.out.println("Attacker: " + attacker);
        System.out.println("Defender: " + defender);

        // Checks the Combatant type and tries to evade, damage calculation is inside attacking functions
        if(attacker instanceof Archer) {
            if(defender.tryEvade(attacker.getAccuracyModifier())) {
                System.out.println("Attack evaded!");
                return;
            }
            else {
                attacker.fight(defender);
            }
        }
        else if(defender.tryEvade()) {
            System.out.println("Attack evaded!");
            return;
        }
        else {
            attacker.fight(defender);
        }

        // Handling of dying player or enemy
        if(defender.isDead()) {
            if(defender instanceof Enemy) {
                defender.defeat();
                SoundManager.playRandomBattleTrack(true);
            }
            else {
                defender.defeat();
            }
        }
    }
}
