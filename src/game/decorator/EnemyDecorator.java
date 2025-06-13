package game.decorator;

import game.characters.Enemy;
import game.combat.*;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.map.Position;

import java.awt.*;
import java.util.UUID;

/**
 * Abstract decorator class for {@link Enemy} objects.
 * Provides a way to extend or modify the behavior of Enemy instances
 * without altering the original Enemy class by wrapping them.
 * Delegates most method calls to the wrapped (decorated) enemy.
 */
public abstract class EnemyDecorator extends Enemy {

    // Data Members
    private final Enemy decoratedEnemy;

    // Methods
    /**
     * Constructs an EnemyDecorator wrapping the specified enemy.
     *
     * @param enemy the enemy to be decorated
     */
    public EnemyDecorator(Enemy enemy) {
        super(enemy);
        this.decoratedEnemy = enemy;
    }

    /**
     * Copy constructor for cloning purposes.
     *
     * @param other the EnemyDecorator instance to copy from
     */
    public EnemyDecorator(EnemyDecorator other) {
        super(other.decoratedEnemy);
        this.decoratedEnemy = other.decoratedEnemy;
    }

    /**
     * Recursively unwraps decorated enemies to return the base enemy instance.
     *
     * @return the base (undecorated) Enemy instance
     */
    @Override
    public Enemy getBaseCharacter() {
        if (decoratedEnemy instanceof EnemyDecorator) {
            return decoratedEnemy.getBaseCharacter(); // recursive unwrap
        }
        return decoratedEnemy;
    }

    /**
     * Gets the enemy type name delegated to the wrapped enemy.
     *
     * @return the type name of the enemy
     */
    @Override
    public String getEnemyTypeName() {
        return decoratedEnemy.getEnemyTypeName();
    }

    /**
     * Returns the wrapped enemy instance.
     *
     * @return the decorated enemy
     */
    public Enemy getDecoratedEnemy() {return decoratedEnemy;}

    /**
     * Returns the string representation delegated to the wrapped enemy.
     *
     * @return string representation of the decorated enemy
     */
    @Override
    public String toString() {
        return decoratedEnemy.toString();
    }

    /**
     * Checks equality by comparing the decorated enemy.
     *
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        EnemyDecorator enemyDecorator = (EnemyDecorator) obj;
        return decoratedEnemy.equals(enemyDecorator);
    }

    // The following methods delegate directly to the decorated enemy
    /** {@inheritDoc} */
    @Override
    public Position getPosition() {
        return decoratedEnemy.getPosition();
    }

    /** {@inheritDoc} */
    @Override
    public int getHealth() {
        return decoratedEnemy.getHealth();
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxHealth() {
        return decoratedEnemy.getMaxHealth();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setMaxHealth(int value) {
        return decoratedEnemy.setMaxHealth(value);
    }

    /** {@inheritDoc} */
    @Override
    public int getLastDamageReceived() {
        return decoratedEnemy.getLastDamageReceived();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setLastDamageReceived(int newLastDamageReceived) {
        return decoratedEnemy.setLastDamageReceived(newLastDamageReceived);
    }
    /** {@inheritDoc} */
    @Override
    public boolean setVisible(boolean visible) {
        return decoratedEnemy.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public UUID getId() {
        return decoratedEnemy.getId();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisible() {
        return decoratedEnemy.isVisible();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setHealth(int health) {
        return decoratedEnemy.setHealth(health);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setPower(int power) {
        return decoratedEnemy.setPower(power);
    }

    /** {@inheritDoc} */
    @Override
    public void setLoot(int loot) {
        decoratedEnemy.setLoot(loot);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setScreenListener(ScreenListener screenListener) {
        return decoratedEnemy.setScreenListener(screenListener);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplaySymbol() {
        return decoratedEnemy.getDisplaySymbol();
    }

    /** {@inheritDoc} */
    @Override
    public void heal(int amount) {
        decoratedEnemy.heal(amount);
    }

    /** {@inheritDoc} */
    @Override
    public void addPower(int power) {
        decoratedEnemy.addPower(power);
    }

    /** {@inheritDoc} */
    @Override
    public int getPower() {
        return decoratedEnemy.getPower();
    }

    /** {@inheritDoc} */
    @Override
    public void defeat() {
        decoratedEnemy.defeat();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPhysicalAttacker() {
        return decoratedEnemy.isPhysicalAttacker();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMagicAttacker() {
        return decoratedEnemy.isMagicAttacker();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMeleeFighter() {
        return decoratedEnemy.isMeleeFighter();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRangedFighter() {
        return decoratedEnemy.isRangedFighter();
    }

    /** {@inheritDoc} */
    @Override
    public String getLogName() {
        return decoratedEnemy.getLogName();
    }

    /** {@inheritDoc} */
    @Override
    public void fight(Combatant target) {
        decoratedEnemy.fight(target);
    }

    /** {@inheritDoc} */
    @Override
    public void threadAction() {
        if (getDecoratedEnemy().getScreenListener() != null) {
            decoratedEnemy.getScreenListener().onAction(ScreenAction.ENEMY_ACTION, this); // Ensure the decorator itself is passed
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return decoratedEnemy.getType();
    }

    /** {@inheritDoc} */
    @Override
    public void setPosition(Position pos) {
        decoratedEnemy.setPosition(pos);
    }

    /** {@inheritDoc} */
    @Override
    public boolean tryEvade() {
        return decoratedEnemy.tryEvade();
    }

    /** {@inheritDoc} */
    @Override
    public boolean tryEvade(double multiplier) {
        return decoratedEnemy.tryEvade(multiplier);
    }

    /** {@inheritDoc} */
    @Override
    public Position getPositionModifier() {
        return decoratedEnemy.getPositionModifier();
    }

    /** {@inheritDoc} */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        decoratedEnemy.receiveDamage(amount, source);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDead() {
        return decoratedEnemy.isDead();
    }

    /** {@inheritDoc} */
    @Override
    public Image getDisplayImage() {
        return decoratedEnemy.getDisplayImage();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return decoratedEnemy.getDescription();
    }

    /** {@inheritDoc} */
    @Override
    public double getAccuracyModifier() {
        return decoratedEnemy.getAccuracyModifier();
    }

    /** {@inheritDoc} */
    @Override
    public MagicElement getElementType() {
        return decoratedEnemy.getElementType();
    }

    /** {@inheritDoc} */
    @Override
    public String getAttackSound() {
        return decoratedEnemy.getAttackSound();
    }

    /** {@inheritDoc} */
    @Override
    public int getRangeModifier() {
        return decoratedEnemy.getRangeModifier();
    }

    /**
     * Calls clone on the decorated enemy and returns the cloned decorator instance.
     *
     * @return a deep clone of this decorator wrapping a cloned enemy
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    public Object callClone() throws CloneNotSupportedException {
        return clone();
    }

    /**
     * Creates a deep clone of this decorator and the wrapped enemy.
     * Attempts to instantiate the same decorator class with the cloned enemy.
     *
     * @return a cloned EnemyDecorator instance
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected EnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) decoratedEnemy.callClone(); // Deep clone the base character

        try {
            return this.getClass()
                    .getDeclaredConstructor(Enemy.class)
                    .newInstance(clonedBase); // Correctly wrap cloned base
        } catch (Exception e) {
            throw new CloneNotSupportedException("Failed to clone decorator: " + e.getMessage());
        }
    }
}
