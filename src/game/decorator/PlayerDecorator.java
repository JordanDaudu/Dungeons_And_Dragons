package game.decorator;

import game.characters.PlayerCharacter;
import game.combat.*;
import game.core.Inventory;
import game.items.Interactable;
import game.map.Position;

import java.awt.*;
import java.util.UUID;

public abstract class PlayerDecorator extends PlayerCharacter {
    private final PlayerCharacter decoratedPlayer;

    public PlayerDecorator(PlayerCharacter other) {
        super(other);
        this.decoratedPlayer = other;
    }

    public PlayerDecorator(PlayerDecorator other) {
        super(other.decoratedPlayer);
        this.decoratedPlayer = other.decoratedPlayer;
    }

    @Override
    public PlayerCharacter getBaseCharacter() {
        if (decoratedPlayer instanceof PlayerDecorator) {
            return decoratedPlayer.getBaseCharacter(); // recursive unwrap
        }
        return decoratedPlayer;
    }

    public PlayerCharacter getDecoratedPlayer() {return decoratedPlayer;}

    @Override
    public String toString() {
        return decoratedPlayer.toString();
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
        PlayerDecorator playerDecorator = (PlayerDecorator) obj;
        return decoratedPlayer.equals(playerDecorator);
    }

    @Override
    public Position getPosition() {
        return decoratedPlayer.getPosition();
    }

    @Override
    public int getHealth() {
        return decoratedPlayer.getHealth();
    }

    @Override
    public int getMaxHealth() {
        return decoratedPlayer.getMaxHealth();
    }

    @Override
    public boolean setMaxHealth(int value) {
        return decoratedPlayer.setMaxHealth(value);
    }

    @Override
    public int getLastDamageReceived() {
        return decoratedPlayer.getLastDamageReceived();
    }

    @Override
    public boolean setLastDamageReceived(int newLastDamageReceived) {
        return decoratedPlayer.setLastDamageReceived(newLastDamageReceived);
    }

    @Override
    public boolean setVisible(boolean visible) {
        return decoratedPlayer.setVisible(visible);
    }

    @Override
    public UUID getId() {
        return decoratedPlayer.getId();
    }

    @Override
    public boolean isVisible() {
        return decoratedPlayer.isVisible();
    }

    @Override
    public boolean setHealth(int health) {
        return decoratedPlayer.setHealth(health);
    }

    @Override
    public boolean setPower(int power) {
        return decoratedPlayer.setPower(power);
    }

    @Override
    public String getName() {
        return decoratedPlayer.getName();
    }

    @Override
    public Inventory getInventory() {
        return decoratedPlayer.getInventory();
    }

    @Override
    public boolean addToInventory(Interactable item) {
        return decoratedPlayer.addToInventory(item);
    }

    @Override
    public boolean usePotion() {
        return decoratedPlayer.usePotion();
    }

    @Override
    public boolean usePowerPotion() {
        return decoratedPlayer.usePowerPotion();
    }

    @Override
    public boolean updateTreasurePoints(int amount) {
        return decoratedPlayer.updateTreasurePoints(amount);
    }

    @Override
    public int getTreasurePoints() {
        return decoratedPlayer.getTreasurePoints();
    }

    @Override
    public boolean isEmpty() {
        return decoratedPlayer.isEmpty();
    }

    @Override
    public String getDisplaySymbol() {
        return decoratedPlayer.getDisplaySymbol();
    }

    @Override
    public Position moveRight() {
        return decoratedPlayer.moveRight();
    }

    @Override
    public Position moveLeft() {
        return decoratedPlayer.moveLeft();
    }

    @Override
    public Position moveUp() {
        return decoratedPlayer.moveUp();
    }

    @Override
    public Position moveDown() {
        return decoratedPlayer.moveDown();
    }

    @Override
    public void fight(Combatant target) {
        decoratedPlayer.fight(target);
    }

    @Override
    public void defeat() {
        decoratedPlayer.defeat();
    }

    @Override
    public boolean isPhysicalAttacker() {
        return decoratedPlayer.isPhysicalAttacker();
    }

    @Override
    public boolean isMagicAttacker() {
        return decoratedPlayer.isMagicAttacker();
    }

    @Override
    public boolean isMeleeFighter() {
        return decoratedPlayer.isMeleeFighter();
    }

    @Override
    public boolean isRangedFighter() {
        return decoratedPlayer.isRangedFighter();
    }

    @Override
    public String getLogName() {
        return decoratedPlayer.getLogName();
    }

    @Override
    public String getType() {
        return decoratedPlayer.getType();
    }

    @Override
    public void setPosition(Position pos) {
        decoratedPlayer.setPosition(pos);
    }

    @Override
    public boolean tryEvade() {
        return decoratedPlayer.tryEvade();
    }

    @Override
    public boolean tryEvade(double multiplier) {
        return decoratedPlayer.tryEvade(multiplier);
    }

    @Override
    public Position getPositionModifier() {
        return decoratedPlayer.getPositionModifier();
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        decoratedPlayer.receiveDamage(amount, source);
    }

    @Override
    public boolean isDead() {
        return decoratedPlayer.isDead();
    }

    @Override
    public void heal(int amount) {
        decoratedPlayer.heal(amount);
    }

    @Override
    public void addPower(int power) {
        decoratedPlayer.addPower(power);
    }

    @Override
    public int getPower() {
        return decoratedPlayer.getPower();
    }

    @Override
    public Object callClone() throws CloneNotSupportedException {
        return clone();
    }

    @Override
    public Image getDisplayImage() {
        return decoratedPlayer.getDisplayImage();
    }

    @Override
    public String getDescription() {
        return decoratedPlayer.getDescription();
    }

    @Override
    public double getAccuracyModifier() {
        return decoratedPlayer.getAccuracyModifier();
    }

    @Override
    public MagicElement getElementType() {
        return decoratedPlayer.getElementType();
    }

    @Override
    public String getAttackSound() {
        return decoratedPlayer.getAttackSound();
    }

    @Override
    public int getRangeModifier() {
        return decoratedPlayer.getRangeModifier();
    }

    @Override
    protected PlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) decoratedPlayer.callClone(); // Deep clone the base character

        try {
            return this.getClass()
                    .getDeclaredConstructor(PlayerCharacter.class)
                    .newInstance(clonedBase); // Correctly wrap cloned base
        } catch (Exception e) {
            throw new CloneNotSupportedException("Failed to clone decorator: " + e.getMessage());
        }
    }
}
