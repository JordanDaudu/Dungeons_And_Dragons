package game.combat;

import game.characters.Archer;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
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
     * Resolves combat between two combatants. The attacker will attempt to attack the defender
     * using either melee or ranged capabilities. If the defender dies, appropriate
     * consequences are triggered (e.g. game over for a player, loot drop for an enemy).
     *
     * @param attacker the combatant initiating the attack
     * @param defender the target of the attack
     */
    public void resolveCombat(Combatant attacker, Combatant defender) {

        System.out.println("Attacker: " + attacker);
        System.out.println("Defender: " + defender);

        if(attacker instanceof MeleeFighter)
            ((MeleeFighter) attacker).fightClose(defender);
        else if(attacker instanceof RangedFighter)
            ((RangedFighter) attacker).fightRanged(defender);
        if(defender.isDead()) {
            if(defender instanceof PlayerCharacter) {
                defender.setVisible(false);
                System.out.println("\n||GAME OVER!||\n");
            }
            else if(defender instanceof Enemy) {
                ((Enemy) defender).defeat();
                SoundManager.playRandomBattleTrack(true);
            }
        }
    }

    /**
     * Tests the combat interaction between an attacker and a defender. The attacker will attempt
     * to attack the defender using either melee or ranged capabilities, considering evasion logic.
     * If the attack is successful and the defender dies, the appropriate outcome (game over or defeat)
     * will be triggered.
     *
     * @param attacker the combatant initiating the attack
     * @param defender the target of the attack
     */
    public void test(Combatant attacker, Combatant defender) {

        System.out.println("Attacker: " + attacker);
        System.out.println("Defender: " + defender);

        // Checks the Combatant type and tries to evade, damage calculation is inside attacking functions
        if(attacker instanceof MeleeFighter) {
            if(defender.tryEvade()) {
                System.out.println("Attack evaded!");
                return;
            }
            else {
                ((MeleeFighter) attacker).fightClose(defender);
            }
        }
        else if(attacker instanceof RangedFighter) {
            if(attacker instanceof Archer) {
                if(defender.tryEvade(attacker.getAccuracyModifier())) {
                    System.out.println("Attack evaded!");
                    return;
                }
                else {
                    ((Archer) attacker).fightRanged(defender);
                }
            }
            else if(defender.tryEvade()) {
                System.out.println("Attack evaded!");
                return;
            }
            else {
                ((RangedFighter) attacker).fightRanged(defender);
            }
        }

        // Handling of dying player or enemy
        if(defender.isDead()) {
            if(defender instanceof PlayerCharacter) {
                System.out.println("\n||GAME OVER!||\n");
            }
            else if(defender instanceof Enemy) {
                ((Enemy) defender).defeat();
            }
        }
    }
    /*
    public void receiveDamage(int amount, Combatant source) {
        if(source instanceof Archer) {
            if(this.tryEvade(source.getAccuracyModifier())) {
                System.out.println("Attack evaded!");
                return;
            }
        }
        else if(this.tryEvade()) {
            System.out.println("Attack evaded!");
            return;
        }
        setHealth(getHealth() - amount);
        System.out.println(getClass().getSimpleName() + " received " + amount + " damage!");
    }
     */
}
