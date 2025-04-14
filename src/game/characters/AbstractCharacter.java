package game.characters;

import game.combat.Combatant;
import game.core.GameEntity;
import game.map.Position;
import game.engine.RandomUtil;

/**
 * Represents a base class for all characters in the game (both players and enemies).
 * Implements common functionality shared by all character types.
 */

public abstract class AbstractCharacter implements Combatant, GameEntity {

    // Data Members
    private Position position;
    private int health;
    private int power;
    private double evasionChance = 0.25;
    private boolean visible;


    // Methods
    /**
     * Default constructor initializing position, health, and power randomly.
     */
    public AbstractCharacter(){
        this.position = null;
        this.health = 100;
        this.power = RandomUtil.getRandomInt(4, 15);
        visible = false;
    }

    /**
     * Returns a string representation of the character.
     * @return a string describing the character's current state
     */
    public String toString() {
        return "AbstractCharacter{" +
                "position = " + position +
                ", health = " + health +
                ", power = " + power +
                ", evasionChance = " + evasionChance +
                ", visible = " + visible +
                '}';
    }

    /**
     * Compares this character to another object for equality.
     * @param obj the object to compare with
     * @return true if the characters are equal, false otherwise
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractCharacter that = (AbstractCharacter) obj;
        return health == that.health &&
                power == that.power &&
                Double.compare(that.evasionChance, evasionChance) == 0 &&
                visible == that.visible &&
                position.equals(that.position);
    }

    /**
     * Gets the character's position.
     * @return the current position
     */
    @Override
    public Position getPosition(){
        return position;
    }

    /**
     * Gets the character's current health.
     * @return the current health
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * Sets the character's visibility state.
     * @param visible true to make visible, false to hide
     * @return true always
     */
    @Override
    public boolean setVisible(boolean visible) {
        this.visible = visible;
        return true;
    }

    /**
     * Checks whether the character is visible.
     * @return true if visible, false otherwise
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Updates the character's health.
     * @param health the new health value
     * @return true if updated
     */
    @Override
    public boolean setHealth(int health) {
        this.health = health;
        if(this.health <= 0)
            this.health = 0;
        return true;
    }

    /**
     * Sets the character's position.
     * @param pos the new position
     */
    @Override
    public void setPosition(Position pos) { this.position = new Position(pos); }

    /**
     * Attempts to evade an attack using the default evasion chance.
     * @return true if the attack is evaded
     */
    @Override
    public boolean tryEvade() { return RandomUtil.getRandomDouble() < evasionChance; }

    /**
     * Attempts to evade an attack using a modified evasion multiplier.
     * @param multiplier evasion penalty or bonus (lower = better evasion)
     * @return true if evaded
     */
    @Override
    public boolean tryEvade(double multiplier) {
        return RandomUtil.getRandomDouble() < evasionChance * (1 - multiplier);
    }

    /**
     * Applies damage to the character from an attacker.
     * @param amount the amount of damage received
     * @param source the attacker who dealt the damage
     */
    @Override
    public void receiveDamage(int amount, Combatant source) {
        // Moved the evasion logic to CombatSystem for now
        setHealth(getHealth() - amount);
        System.out.println(getClass().getSimpleName() + " received " + amount + " damage!");
    }

    /**
     * Checks whether the character is dead (health <= 0).
     * @return true if dead, false otherwise
     */
    @Override
    public boolean isDead(){
        return (health <= 0);
    }

    /**
     * Heals the character by the specified amount.
     * Caps at 100 health.
     * @param amount the healing amount
     */
    @Override
    public void heal(int amount){
        if (isDead())
            return;
        health += amount;
        if(health > 100)
            health = 100;
    }

    /**
     * Increases the character's power stat.
     * @param power amount to add
     */
    public void addPower(int power) {
        this.power += power;
    }

    /**
     * Gets the character's current power.
     * @return the power level
     */
    @Override
    public int getPower(){
        return power;
    }

}
