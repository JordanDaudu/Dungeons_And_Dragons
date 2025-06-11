package game.decorator;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.*;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.map.Position;

import java.awt.*;
import java.util.UUID;

public abstract class EnemyDecorator extends Enemy {
    private final Enemy decoratedEnemy;

    public EnemyDecorator(Enemy enemy) {
        super(enemy);
        this.decoratedEnemy = enemy;
    }

    public EnemyDecorator(EnemyDecorator other) {
        super(other.decoratedEnemy);
        this.decoratedEnemy = other.decoratedEnemy;
    }

    @Override
    public Enemy getBaseCharacter() {
        if (decoratedEnemy instanceof EnemyDecorator) {
            return decoratedEnemy.getBaseCharacter(); // recursive unwrap
        }
        return decoratedEnemy;
    }

    public Enemy getDecoratedEnemy() {return decoratedEnemy;}

    @Override
    public String toString() {
        return decoratedEnemy.toString();
    }

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

    @Override
    public Position getPosition() {
        return decoratedEnemy.getPosition();
    }

    @Override
    public int getHealth() {
        return decoratedEnemy.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return decoratedEnemy.getMaxHealth();
    }

    @Override
    public boolean setMaxHealth(int value) {
        return decoratedEnemy.setMaxHealth(value);
    }

    @Override
    public int getLastDamageReceived() {
        return decoratedEnemy.getLastDamageReceived();
    }

    @Override
    public boolean setLastDamageReceived(int newLastDamageReceived) {
        return decoratedEnemy.setLastDamageReceived(newLastDamageReceived);
    }

    @Override
    public boolean setVisible(boolean visible) {
        return decoratedEnemy.setVisible(visible);
    }

    @Override
    public UUID getId() {
        return decoratedEnemy.getId();
    }

    @Override
    public boolean isVisible() {
        return decoratedEnemy.isVisible();
    }

    @Override
    public boolean setHealth(int health) {
        return decoratedEnemy.setHealth(health);
    }

    @Override
    public boolean setPower(int power) {
        return decoratedEnemy.setPower(power);
    }

    @Override
    public void setLoot(int loot) {
        decoratedEnemy.setLoot(loot);
    }

    @Override
    public boolean setScreenListener(ScreenListener screenListener) {
        return decoratedEnemy.setScreenListener(screenListener);
    }

    @Override
    public String getDisplaySymbol() {
        return decoratedEnemy.getDisplaySymbol();
    }

    @Override
    public void heal(int amount) {
        decoratedEnemy.heal(amount);
    }

    @Override
    public void addPower(int power) {
        decoratedEnemy.addPower(power);
    }

    @Override
    public int getPower() {
        return decoratedEnemy.getPower();
    }

    @Override
    public void defeat() {
        decoratedEnemy.defeat();
    }

    @Override
    public boolean isPhysicalAttacker() {
        return decoratedEnemy.isPhysicalAttacker();
    }

    @Override
    public boolean isMagicAttacker() {
        return decoratedEnemy.isMagicAttacker();
    }

    @Override
    public boolean isMeleeFighter() {
        return decoratedEnemy.isMeleeFighter();
    }

    @Override
    public boolean isRangedFighter() {
        return decoratedEnemy.isRangedFighter();
    }

    @Override
    public String getLogName() {
        return decoratedEnemy.getLogName();
    }

    @Override
    public void fight(Combatant target) {
        decoratedEnemy.fight(target);
    }

    @Override
    public void threadAction() {
        if (getDecoratedEnemy().getScreenListener() != null) {
            System.out.println("Calling threadAction() from: " + this.getClass().getSimpleName());
            decoratedEnemy.getScreenListener().onAction(ScreenAction.ENEMY_ACTION, this); // Ensure the decorator itself is passed
        }
    }

    @Override
    public String getType() {
        return decoratedEnemy.getType();
    }

    @Override
    public void setPosition(Position pos) {
        super.setPosition(pos);
        decoratedEnemy.setPosition(pos);
    }

    @Override
    public boolean tryEvade() {
        return decoratedEnemy.tryEvade();
    }

    @Override
    public boolean tryEvade(double multiplier) {
        return decoratedEnemy.tryEvade(multiplier);
    }

    @Override
    public Position getPositionModifier() {
        return decoratedEnemy.getPositionModifier();
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        decoratedEnemy.receiveDamage(amount, source);
    }

    @Override
    public boolean isDead() {
        return decoratedEnemy.isDead();
    }

    @Override
    public Image getDisplayImage() {
        return decoratedEnemy.getDisplayImage();
    }

    @Override
    public String getDescription() {
        return decoratedEnemy.getDescription();
    }

    @Override
    public double getAccuracyModifier() {
        return decoratedEnemy.getAccuracyModifier();
    }

    @Override
    public MagicElement getElementType() {
        return decoratedEnemy.getElementType();
    }

    @Override
    public String getAttackSound() {
        return decoratedEnemy.getAttackSound();
    }

    @Override
    public int getRangeModifier() {
        return decoratedEnemy.getRangeModifier();
    }

    @Override
    public Object callClone() throws CloneNotSupportedException {
        return clone();
    }

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
