package game.combat;

import game.characters.Archer;
import game.characters.Dragon;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.SoundManager;
import game.logging.GameLogger;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The CombatSystem class handles combat resolution between two Combatants.
 * It determines the type of attack to perform and handles post-combat effects such as death.
 */
public class CombatSystem {

    // Data Members
    private static CombatSystem instance = null;
    private ScreenListener listener;
    private static final Lock combatLock = new ReentrantLock(true); // Fair lock, FIFO order

    // Methods
    /**
     * Constructs a new CombatSystem instance.
     */
    private CombatSystem(ScreenListener screenListener) {
        this.listener = screenListener;
    }

    /**
     * Returns the singleton instance of the CombatSystem.
     * If the instance does not yet exist, it is created with a null ScreenListener.
     * You should call {@link #setListener(ScreenListener)} afterward to assign a valid listener.
     *
     * @return the CombatSystem singleton instance
     */
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
        combatLock.lock();
        try {
            System.out.println("Attacker: " + attacker);
            System.out.println("Defender: " + defender);
            executeCombatTurn(attacker, defender);

            if (!defender.isDead()) {
                System.out.println("Attacker: " + defender);
                System.out.println("Defender: " + attacker);
                executeCombatTurn(defender, attacker);
            }
        }
        catch (Exception e) {
            // Handle any specific exceptions that might be thrown during combat
            e.printStackTrace();
        }
        finally {
            combatLock.unlock();  // Ensure lock is always released
        }
    }

    /**
     * Sets the ScreenListener used for visual combat feedback such as animations and screen effects.
     *
     * @param screenListener the listener to handle screen actions
     * @return true if the listener was successfully set
     */
    public boolean setListener(ScreenListener screenListener) {
        this.listener = screenListener;
        return true;
    }

    /**
     * Executes a single turn of combat from one combatant to another.
     * Handles evasion checks, damage application, sound/visual effects,
     * and post-combat logic like death or music changes.
     *
     * @param attacker the combatant performing the attack
     * @param defender the combatant receiving the attack
     */
    private void executeCombatTurn(Combatant attacker, Combatant defender) {
        // Checks the Combatant type and tries to evade, damage calculation is inside attacking functions
        int amountOfReceivedDamage = 0;
        defender.setLastDamageReceived(0);

        if (attacker instanceof Archer) {
            if (defender.tryEvade(attacker.getAccuracyModifier())) {
                System.out.println("Attack evaded!");
                logEvade(attacker, defender); // LOGGING HERE
                playGrayEvadedBlinkAnimation(attacker, defender);
                return;
            }
            else {
                attacker.fight(defender);
                amountOfReceivedDamage = defender.getLastDamageReceived();
                logAttack(attacker, defender, amountOfReceivedDamage); // LOGGING HERE
                if(amountOfReceivedDamage > 0)
                    playNumberPopUpAnimation(attacker, defender, amountOfReceivedDamage);
                playAttackSound(attacker);
                playRedDamagedBlinkAnimation(attacker, defender);
            }
        }
        else if (defender.tryEvade()) {
            System.out.println("Attack evaded!");
            logEvade(attacker, defender); // LOGGING HERE
            playGrayEvadedBlinkAnimation(attacker, defender);
            return;
        }
        else {
            attacker.fight(defender);
            amountOfReceivedDamage = defender.getLastDamageReceived();
            logAttack(attacker, defender, amountOfReceivedDamage); // LOGGING HERE
            if(amountOfReceivedDamage > 0)
                playNumberPopUpAnimation(attacker, defender, amountOfReceivedDamage);
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
        }
        else {
            if (defender instanceof PlayerCharacter)
                checkLowHPMusic(defender);
            else if(defender instanceof Dragon)
                attemptChangeToDragonBattleMusic(attacker);
        }
    }

    /**
     * Plays the attack sound effect associated with the attacking combatant.
     *
     * @param attacker the combatant initiating the attack
     */
    private void playAttackSound(Combatant attacker) {
        String attackSound = attacker.getAttackSound();
        if (attackSound != null) {
            SoundManager.playEffect(attackSound);
        }
    }

    /**
     * Triggers a red blink animation on the defender to indicate they received damage.
     *
     * @param attacker the combatant who attacked
     * @param defender the combatant who received the attack
     */
    private void playRedDamagedBlinkAnimation(Combatant attacker, Combatant defender) {
        if (listener != null)
            if (attacker.getPositionModifier().distanceTo(defender.getPositionModifier()) <= attacker.getRangeModifier())
                listener.onAction(ScreenAction.RECEIVED_DAMAGE, defender.getPosition(), new Color(255, 0, 0, 192));
    }

    /**
     * Triggers a gray blink animation on the defender to indicate they evaded an attack.
     *
     * @param attacker the combatant who attempted the attack
     * @param defender the combatant who evaded the attack
     */
    private void playGrayEvadedBlinkAnimation(Combatant attacker, Combatant defender) {
        if (listener != null)
            if (attacker.getPositionModifier().distanceTo(defender.getPositionModifier()) <= attacker.getRangeModifier())
                listener.onAction(ScreenAction.RECEIVED_DAMAGE, defender.getPosition(), new Color(128, 128, 128, 192));
    }

    /**
     * Displays a number pop-up animation to show damage dealt.
     * Triggers only if the attacker is in range and the damage amount is greater than zero.
     *
     * @param attacker the combatant who dealt the damage
     * @param defender the combatant who received the damage
     * @param amount the amount of damage dealt
     */
    private void playNumberPopUpAnimation(Combatant attacker, Combatant defender, int amount) {
        if(listener != null) {
            if (attacker.getPositionModifier().distanceTo(defender.getPositionModifier()) <= attacker.getRangeModifier() && amount > 0)
                listener.onAction(ScreenAction.RECEIVE_DAMAGE_TEXT_ANIMATION, attacker, defender.getPosition(), amount);
        }
    }

    /**
     * Checks if the player's health is critically low and triggers low HP music if so.
     *
     * @param player the player combatant to check
     */
    private void checkLowHPMusic(Combatant player) {
        if (player.getHealth() <= 25)
            SoundManager.crossfadeTo("lowHP", true);
    }

    /**
     * Changes the battle music to a random track if the conditions are met (e.g., enemy defeated).
     *
     * @param attacker the player who defeated the enemy
     * @param defender the enemy that was defeated
     */
    private void changeBattleMusic(Combatant attacker, Combatant defender) {
        if(attacker instanceof PlayerCharacter && attacker.getHealth() > 25 && !(defender instanceof Dragon))
            SoundManager.playRandomBattleTrack(true);
    }

    /**
     * Switches to dragon battle music if the player is fighting a dragon and has enough health.
     *
     * @param attacker the attacking player
     */
    private void attemptChangeToDragonBattleMusic(Combatant attacker) {
        if(attacker instanceof PlayerCharacter && attacker.getHealth() > 25)
            SoundManager.crossfadeTo("dragon1", true);
    }

    private void logAttack(Combatant attacker, Combatant defender, int damage) {
        String message = "";

        if (attacker instanceof PlayerCharacter && defender instanceof Enemy) {
            message = "Player: " + ((PlayerCharacter) attacker).getName() + " attacked Enemy " + defender.getClass().getSimpleName() + " and dealt " + damage + " damage.";
        }
        else if (attacker instanceof Enemy && defender instanceof PlayerCharacter) {
            message = "Enemy: " + attacker.getClass().getSimpleName() + " attacked Player " + ((PlayerCharacter) defender).getName() + " and dealt " + damage + " damage.";
        }
        GameLogger.getInstance().log(message);
    }

    private void logEvade(Combatant attacker, Combatant defender) {
        String message = "";

        if (attacker instanceof PlayerCharacter && defender instanceof Enemy) {
            message = "Enemy: " + defender.getClass().getSimpleName() + " evaded attack from Player: " + ((PlayerCharacter) attacker).getName() + ".";
        } else if (attacker instanceof Enemy && defender instanceof PlayerCharacter) {
            message = "Player: " + ((PlayerCharacter) defender).getName() + " evaded attack from Enemy " + attacker.getClass().getSimpleName() + ".";
        }

        GameLogger.getInstance().log(message);
    }

}