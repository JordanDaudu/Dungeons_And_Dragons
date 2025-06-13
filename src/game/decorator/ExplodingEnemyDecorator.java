package game.decorator;

import game.characters.Enemy;
import game.combat.Combatant;
import game.engine.SoundManager;
import game.logging.GameLogger;
import game.map.GameMap;
import game.map.Position;

/**
 * Decorator that causes an enemy to explode upon death, damaging surrounding characters.
 */
public class ExplodingEnemyDecorator extends EnemyDecorator {

    // Methods
    /**
     * Constructs an ExplodingEnemyDecorator wrapping the specified enemy.
     *
     * @param character the enemy to decorate with exploding behavior
     */
    public ExplodingEnemyDecorator(Enemy character) {
        super(character);
    }

    /**
     * Copy constructor for cloning.
     *
     * @param other the ExplodingEnemyDecorator instance to copy from
     */
    public ExplodingEnemyDecorator(ExplodingEnemyDecorator other) {
        super(other);
    }

    /**
     * Creates a deep clone of this decorator and the wrapped enemy.
     *
     * @return a cloned ExplodingEnemyDecorator instance wrapping a cloned base enemy
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected ExplodingEnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) getDecoratedEnemy().callClone(); // Deep clone the base character
        return new ExplodingEnemyDecorator(clonedBase); // Correct return type
    }

    /**
     * Called when the enemy receives damage.
     * If the enemy dies as a result, triggers an explosion effect that damages surrounding characters,
     * plays sound effect, and logs the event.
     *
     * @param amount the amount of damage received
     * @param source the source of the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage(amount, source);
        if(getDecoratedEnemy().isDead()) {
            SoundManager.playEffect("explosion");
            explode();
            GameLogger.getInstance().log(getBaseCharacter().getLogName() + " exploded after dying damaging surrounding.");
        }

    }

    /**
     * ExplodingEnemyDecorator has no active ability to use.
     *
     * @return false always, indicating no active ability
     */
    @Override
    public boolean useAbility() {
        return false; // PassiveAbility when dying
    }

    /**
     * Returns the duration of the ability effect in milliseconds.
     *
     * @return 0 indicating instantaneous effect upon death
     */
    @Override
    public int abilityTimeInMilliseconds() {
        return 0;
    }

    /**
     * Explodes, dealing damage around the enemy's current position based on max health.
     */
    private void explode() {
        GameMap map = GameMap.getInstance();
        Position characterPosition = getDecoratedEnemy().getPosition();
        int damage = (int) (getDecoratedEnemy().getMaxHealth() * 0.02);
        map.damageCharactersAround(characterPosition, damage);
    }
}
