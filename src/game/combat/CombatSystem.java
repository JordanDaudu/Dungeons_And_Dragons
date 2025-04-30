package game.combat;

import game.characters.Archer;
import game.characters.Enemy;
import game.characters.Mage;
import game.characters.Warrior;
import game.engine.ScreenAction;
import game.engine.ScreenListener;
import game.engine.SoundManager;

import java.awt.*;

/**
 * The CombatSystem class handles combat resolution between two Combatants.
 * It determines the type of attack to perform and handles post-combat effects such as death.
 */
public class CombatSystem {

    private static CombatSystem instance = null;
    private ScreenListener listener;
    // Methods
    /**
     * Constructs a new CombatSystem instance.
     */
    private CombatSystem(ScreenListener screenListener) {}

    public static CombatSystem getInstance() {
        if(instance == null)
            instance = new CombatSystem(null);
        return instance;
    }

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
                if(listener != null)
                    listener.onAction(ScreenAction.RECEIVEDDAMAGE, defender.getPosition().getRow(), defender.getPosition().getCol(), new Color(128, 128, 128, 120));
                return;
            }
            else {
                attacker.fight(defender);
                SoundManager.playEffect("bowShot");
                if(listener != null)
                    listener.onAction(ScreenAction.RECEIVEDDAMAGE, defender.getPosition().getRow(), defender.getPosition().getCol(), new Color(255, 0, 0, 120));
            }
        }
        else if(defender.tryEvade()) {
            System.out.println("Attack evaded!");
            if(listener != null)
                listener.onAction(ScreenAction.RECEIVEDDAMAGE, defender.getPosition().getRow(), defender.getPosition().getCol(), new Color(128, 128, 128, 120));
            return;
        }
        else {
            attacker.fight(defender);
            if(attacker instanceof Warrior)
                SoundManager.playEffect("swordSwing");
            else if(attacker instanceof Mage)
                SoundManager.playEffect("magicSpell");
            if(listener != null)
                listener.onAction(ScreenAction.RECEIVEDDAMAGE, defender.getPosition().getRow(), defender.getPosition().getCol(), new Color(255, 0, 0, 120));
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

    public boolean setListener(ScreenListener screenListener) {
        this.listener = screenListener;
        return true;
    }
}
