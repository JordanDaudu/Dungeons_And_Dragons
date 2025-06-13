package game.decorator;

import game.characters.PlayerCharacter;
import game.combat.*;
import game.core.Inventory;
import game.items.Interactable;
import game.map.Position;

import java.awt.*;
import java.util.UUID;

/**
 * Abstract base class for player ability decorators using the Decorator design pattern.
 * <p>
 * This class wraps a {@link PlayerCharacter} and delegates most method calls to the decorated player.
 * Subclasses are expected to override relevant methods to modify or enhance specific behaviors
 * (e.g., attack power, healing, defense) without altering the original player implementation.
 */
public abstract class PlayerDecorator extends PlayerCharacter {

    // Data Members
    private final PlayerCharacter decoratedPlayer;

    // Methods
    /**
     * Constructs a new {@code PlayerDecorator} by wrapping the given {@code PlayerCharacter}.
     *
     * @param other the base player character to decorate
     */
    public PlayerDecorator(PlayerCharacter other) {
        super(other);
        this.decoratedPlayer = other;
    }

    /**
     * Copy constructor for {@code PlayerDecorator}.
     * Creates a new decorator wrapping the same base as the given decorator.
     *
     * @param other the decorator to copy
     */
    public PlayerDecorator(PlayerDecorator other) {
        super(other.decoratedPlayer);
        this.decoratedPlayer = other.decoratedPlayer;
    }

    /**
     * Recursively unwraps and returns the original base {@link PlayerCharacter}
     * that is not a decorator.
     *
     * @return the base (undecorated) {@code PlayerCharacter}
     */
    @Override
    public PlayerCharacter getBaseCharacter() {
        if (decoratedPlayer instanceof PlayerDecorator) {
            return decoratedPlayer.getBaseCharacter(); // recursive unwrap
        }
        return decoratedPlayer;
    }

    /**
     * Returns the player currently decorated by this decorator.
     *
     * @return the wrapped {@code PlayerCharacter}
     */
    public PlayerCharacter getDecoratedPlayer() {return decoratedPlayer;}

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return decoratedPlayer.toString();
    }

    /** {@inheritDoc} */
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

    // All following methods delegate directly to the decoratedPlayer.
    // These are included to preserve full functionality unless overridden by a subclass.
    /** {@inheritDoc} */
    @Override
    public Position getPosition() {
        return decoratedPlayer.getPosition();
    }

    /** {@inheritDoc} */
    @Override
    public int getHealth() {
        return decoratedPlayer.getHealth();
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxHealth() {
        return decoratedPlayer.getMaxHealth();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setMaxHealth(int value) {
        return decoratedPlayer.setMaxHealth(value);
    }

    /** {@inheritDoc} */
    @Override
    public int getLastDamageReceived() {
        return decoratedPlayer.getLastDamageReceived();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setLastDamageReceived(int newLastDamageReceived) {
        return decoratedPlayer.setLastDamageReceived(newLastDamageReceived);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setVisible(boolean visible) {
        return decoratedPlayer.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public UUID getId() {
        return decoratedPlayer.getId();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisible() {
        return decoratedPlayer.isVisible();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setHealth(int health) {
        return decoratedPlayer.setHealth(health);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setPower(int power) {
        return decoratedPlayer.setPower(power);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return decoratedPlayer.getName();
    }

    /** {@inheritDoc} */
    @Override
    public Inventory getInventory() {
        return decoratedPlayer.getInventory();
    }

    /** {@inheritDoc} */
    @Override
    public boolean addToInventory(Interactable item) {
        return decoratedPlayer.addToInventory(item);
    }

    /** {@inheritDoc} */
    @Override
    public boolean usePotion() {
        return decoratedPlayer.usePotion();
    }

    /** {@inheritDoc} */
    @Override
    public boolean usePowerPotion() {
        return decoratedPlayer.usePowerPotion();
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateTreasurePoints(int amount) {
        return decoratedPlayer.updateTreasurePoints(amount);
    }

    /** {@inheritDoc} */
    @Override
    public int getTreasurePoints() {
        return decoratedPlayer.getTreasurePoints();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return decoratedPlayer.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplaySymbol() {
        return decoratedPlayer.getDisplaySymbol();
    }

    /** {@inheritDoc} */
    @Override
    public Position moveRight() {
        return decoratedPlayer.moveRight();
    }

    /** {@inheritDoc} */
    @Override
    public Position moveLeft() {
        return decoratedPlayer.moveLeft();
    }

    /** {@inheritDoc} */
    @Override
    public Position moveUp() {
        return decoratedPlayer.moveUp();
    }

    /** {@inheritDoc} */
    @Override
    public Position moveDown() {
        return decoratedPlayer.moveDown();
    }

    /** {@inheritDoc} */
    @Override
    public void fight(Combatant target) {
        decoratedPlayer.fight(target);
    }

    /** {@inheritDoc} */
    @Override
    public void defeat() {
        decoratedPlayer.defeat();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPhysicalAttacker() {
        return decoratedPlayer.isPhysicalAttacker();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMagicAttacker() {
        return decoratedPlayer.isMagicAttacker();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMeleeFighter() {
        return decoratedPlayer.isMeleeFighter();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRangedFighter() {
        return decoratedPlayer.isRangedFighter();
    }

    /** {@inheritDoc} */
    @Override
    public String getLogName() {
        return decoratedPlayer.getLogName();
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return decoratedPlayer.getType();
    }

    /** {@inheritDoc} */
    @Override
    public void setPosition(Position pos) {
        decoratedPlayer.setPosition(pos);
    }

    /** {@inheritDoc} */
    @Override
    public boolean tryEvade() {
        return decoratedPlayer.tryEvade();
    }

    /** {@inheritDoc} */
    @Override
    public boolean tryEvade(double multiplier) {
        return decoratedPlayer.tryEvade(multiplier);
    }

    /** {@inheritDoc} */
    @Override
    public Position getPositionModifier() {
        return decoratedPlayer.getPositionModifier();
    }

    /** {@inheritDoc} */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        decoratedPlayer.receiveDamage(amount, source);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDead() {
        return decoratedPlayer.isDead();
    }

    /** {@inheritDoc} */
    @Override
    public void heal(int amount) {
        decoratedPlayer.heal(amount);
    }

    /** {@inheritDoc} */
    @Override
    public void addPower(int power) {
        decoratedPlayer.addPower(power);
    }

    /** {@inheritDoc} */
    @Override
    public int getPower() {
        return decoratedPlayer.getPower();
    }

    /** {@inheritDoc} */
    @Override
    public Object callClone() throws CloneNotSupportedException {
        return clone();
    }

    /** {@inheritDoc} */
    @Override
    public Image getDisplayImage() {
        return decoratedPlayer.getDisplayImage();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return decoratedPlayer.getDescription();
    }

    /** {@inheritDoc} */
    @Override
    public double getAccuracyModifier() {
        return decoratedPlayer.getAccuracyModifier();
    }

    /** {@inheritDoc} */
    @Override
    public MagicElement getElementType() {
        return decoratedPlayer.getElementType();
    }

    /** {@inheritDoc} */
    @Override
    public String getAttackSound() {
        return decoratedPlayer.getAttackSound();
    }

    /** {@inheritDoc} */
    @Override
    public int getRangeModifier() {
        return decoratedPlayer.getRangeModifier();
    }

    /**
     * Creates a deep clone of this decorated player, ensuring the base character is also cloned.
     *
     * @return a new {@code PlayerDecorator} instance wrapping a clone of the original decorated player
     * @throws CloneNotSupportedException if cloning fails due to reflection or constructor access
     */
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
