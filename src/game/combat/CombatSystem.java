package game.combat;

import game.characters.Archer;
import game.characters.Dragon;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
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
    private CombatSystem(ScreenListener screenListener) {
        this.listener = screenListener;
    }

    public static CombatSystem getInstance() {
        if (instance == null)
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
        executeCombatTurn(attacker, defender);

        if (!defender.isDead()) {
            System.out.println("Attacker: " + defender);
            System.out.println("Defender: " + attacker);
            executeCombatTurn(defender, attacker);
        }
    }

    public boolean setListener(ScreenListener screenListener) {
        this.listener = screenListener;
        return true;
    }

    private void executeCombatTurn(Combatant attacker, Combatant defender) {
        // Checks the Combatant type and tries to evade, damage calculation is inside attacking functions
        if (attacker instanceof Archer) {
            if (defender.tryEvade(attacker.getAccuracyModifier())) {
                System.out.println("Attack evaded!");
                playGrayEvadedBlinkAnimation(attacker, defender);
                return;
            } else {
                attacker.fight(defender);
                playAttackSound(attacker);
                playRedDamagedBlinkAnimation(attacker, defender);
            }
        } else if (defender.tryEvade()) {
            System.out.println("Attack evaded!");
            playGrayEvadedBlinkAnimation(attacker, defender);
            return;
        } else {
            attacker.fight(defender);
            playAttackSound(attacker);
            playRedDamagedBlinkAnimation(attacker, defender);
        }

        // Handling of dying player or enemy
        if (defender.isDead()) {
            if (defender instanceof Enemy) {
                defender.defeat();
                changeBattleMusic(attacker, defender);
            } else {
                defender.defeat();
            }
        } else {
            if (defender instanceof PlayerCharacter)
                checkLowHPMusic(defender);
            attemptChangeToDragonBattleMusic(attacker, defender);
        }
    }

    private void playAttackSound(Combatant attacker) {
        String attackSound = attacker.getAttackSound();
        if (attackSound != null) {
            SoundManager.playEffect(attackSound);
        }
    }

    private void playRedDamagedBlinkAnimation(Combatant attacker, Combatant defender) {
        if (listener != null)
            if (attacker.getPositionModifier().distanceTo(defender.getPositionModifier()) <= attacker.getRangeModifier())
                listener.onAction(ScreenAction.RECEIVEDDAMAGE, defender.getPosition().getRow(), defender.getPosition().getCol(), new Color(255, 0, 0, 120));
    }

    private void playGrayEvadedBlinkAnimation(Combatant attacker, Combatant defender) {
        if (listener != null)
            if (attacker.getPositionModifier().distanceTo(defender.getPositionModifier()) <= attacker.getRangeModifier())
                listener.onAction(ScreenAction.RECEIVEDDAMAGE, defender.getPosition().getRow(), defender.getPosition().getCol(), new Color(128, 128, 128, 120));
    }

    private void checkLowHPMusic(Combatant player) {
        if (player.getHealth() <= 25)
            SoundManager.crossfadeTo("lowHP", true);
    }

    private void changeBattleMusic(Combatant attacker, Combatant defender) {
        if(attacker instanceof PlayerCharacter && attacker.getHealth() > 25 && !(defender instanceof Dragon))
            SoundManager.playRandomBattleTrack(true);
    }

    private void attemptChangeToDragonBattleMusic(Combatant attacker, Combatant defender) {
        if(attacker instanceof PlayerCharacter && attacker.getHealth() > 25)
            SoundManager.crossfadeTo("dragon1", true);
    }
}