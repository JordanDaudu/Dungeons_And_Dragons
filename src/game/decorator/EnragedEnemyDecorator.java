package game.decorator;

import game.characters.Enemy;
import game.combat.Combatant;
import game.logging.GameLogger;

/**
 * Decorator that enrages an enemy by increasing its power as it loses health.
 * The power boost scales based on the percentage of health lost, capped by a
 * calculated maximum increase based on the enemy's original power and max health.
 */
public class EnragedEnemyDecorator extends EnemyDecorator {

    // Data Members
    private final int originalPower;
    private final int maxPowerIncrease; // 5% of original power scaled by maxHealth

    // Methods
    /**
     * Constructs an EnragedEnemyDecorator wrapping the specified enemy.
     * Calculates the maximum power increase based on original power and max health.
     *
     * @param character the enemy to be decorated and enraged
     */
    public EnragedEnemyDecorator(Enemy character) {
        super(character);
        this.originalPower = character.getPower();
        this.maxPowerIncrease = (int) Math.ceil(originalPower * 0.05 * (character.getMaxHealth() / 100.0));
    }

    /**
     * Copy constructor for cloning.
     *
     * @param other the EnragedEnemyDecorator instance to copy from
     */
    public EnragedEnemyDecorator(EnragedEnemyDecorator other) {
        super(other);
        this.originalPower = other.originalPower;
        this.maxPowerIncrease = other.maxPowerIncrease;
    }

    /**
     * Creates a deep clone of this decorator and the wrapped enemy.
     *
     * @return a cloned EnragedEnemyDecorator instance wrapping a cloned base enemy
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected EnragedEnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) getDecoratedEnemy().callClone(); // Deep clone the base character
        return new EnragedEnemyDecorator(clonedBase); // Correct return type
    }

    /**
     * Called when the enemy receives damage.
     * Updates the enemy's power based on the percentage of health lost.
     * Logs the power increase event.
     *
     * @param amount the amount of damage received
     * @param source the source of the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage(amount, source);
        int lostHealth = getDecoratedEnemy().getMaxHealth() - getDecoratedEnemy().getHealth();
        double lostPercentage = (double) lostHealth / getDecoratedEnemy().getMaxHealth();

        int newPower = originalPower + (int) Math.floor(maxPowerIncrease * lostPercentage);
        getDecoratedEnemy().setPower(newPower);
        GameLogger.getInstance().log(getBaseCharacter().getLogName() + " is enraged power boosted from " + originalPower + " to " + newPower + ".");
    }

    /**
     * EnragedEnemyDecorator does not have an active ability.
     *
     * @return false always, indicating no active ability to use
     */
    @Override
    public boolean useAbility() {
        return false; // Passive
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
