package game.decorator;

import game.characters.Enemy;
import game.combat.Combatant;
import game.engine.SoundManager;
import game.logging.GameLogger;
import game.map.GameMap;

/**
 * Decorator that gives an enemy the ability to teleport away when its health falls below 30%.
 * The teleportation is triggered upon receiving damage and is accompanied by a sound effect and a log entry.
 */
public class TeleportingEnemyDecorator extends EnemyDecorator {

    // Methods
    /**
     * Constructs a TeleportingEnemyDecorator wrapping the specified enemy.
     *
     * @param character the enemy to decorate with teleportation ability
     */
    public TeleportingEnemyDecorator(Enemy character) {
        super(character);
    }

    /**
     * Copy constructor for cloning.
     *
     * @param other the TeleportingEnemyDecorator instance to copy from
     */
    public TeleportingEnemyDecorator(TeleportingEnemyDecorator other) {
        super(other);
    }

    /**
     * Creates a deep clone of this decorator and the wrapped enemy.
     *
     * @return a cloned TeleportingEnemyDecorator instance wrapping a cloned base enemy
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected TeleportingEnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) getDecoratedEnemy().callClone(); // Deep clone the base character
        return new TeleportingEnemyDecorator(clonedBase);
    }

    /**
     * TeleportingEnemyDecorator has no active ability to use.
     *
     * @return false always, indicating no active ability
     */
    @Override
    public boolean useAbility() {
        return false; // Passive Skill
    }

    /**
     * Called when the enemy receives damage.
     * If the enemy is alive and health falls below 30% of max health,
     * triggers teleportation, plays sound, and logs the event.
     *
     * @param amount the amount of damage received
     * @param source the source of the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage(amount, source);
        if(!isDead() && getHealth() < getMaxHealth() * 0.3) {
            SoundManager.playEffect("teleport");
            teleporting();
            GameLogger.getInstance().log(getBaseCharacter().getLogName() + " teleported away");
        }
    }

    /**
     * Teleports the enemy by removing it from the current map position
     * and placing it randomly on the map.
     */
    public void teleporting() {
        GameMap.getInstance().removeEntity(this);
        GameMap.getInstance().placeEnemyRandomly(this);
    }

    /**
     * Returns the duration of the ability effect in milliseconds.
     *
     * @return -1 indicating no timed ability
     */
    @Override
    public int abilityTimeInMilliseconds() {
        return -1; // Not a timed effect
    }
}
