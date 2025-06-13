package game.characters;

import game.combat.Combatant;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.RandomUtil;
import game.items.Treasure;
import game.map.GameMap;

import java.io.Serializable;

/**
 * Represents an enemy character in the game.
 * An Enemy has limited health and can drop loot (as a Treasure) when defeated.
 */
public abstract class Enemy extends AbstractCharacter implements Serializable {

    // Data Members
    private int loot;
    private transient ScreenListener screenListener = null;

    // Methods
    /**
     * Constructs a default Enemy with full health and randomly assigned loot (100–300).
     */
    public Enemy() {
        super();
        setHealth(getMaxHealth());
        loot = RandomUtil.getRandomInt(100, 301);
    }

    /**
     * Copy constructor for creating a deep copy of another Enemy.
     *
     * @param other the enemy to copy
     */
    public Enemy(Enemy other){
        super(other);
        loot = other.loot;
        this.screenListener = other.screenListener;
    }
    /**
     * Returns a string representation of the Enemy, including type, health, and loot.
     *
     * @return a string describing the enemy
     */
    @Override
    public String toString() {
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", loot=" + loot +
                '}';
    }

    /**
     * Checks whether this Enemy is logically equal to another object.
     *
     * @param obj the object to compare
     * @return true if both objects are of the same class and have equal fields
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
        Enemy that = (Enemy) obj;
        return loot == that.loot;
    }

    /**
     * Returns the maximum health of the enemy.
     * Fixed at 50 for all enemies.
     *
     * @return the max health value (50)
     */
    @Override
    public int getMaxHealth() {
        return 50;
    }

    /**
     * Sets the amount of loot this enemy will drop.
     *
     * @param loot the loot value to set
     */
    public void setLoot(int loot) {this.loot = loot;}

    /**
     * Registers a screen listener to this enemy.
     *
     * @param screenListener the listener to register
     * @return true if the listener was set successfully
     */
    public boolean setScreenListener(ScreenListener screenListener) {
        this.screenListener = screenListener;
        return true;
    }

    /**
     * Retrieves the screen listener registered to this enemy.
     *
     * @return the screen listener, or null if none is set
     */
    public ScreenListener getScreenListener() {return screenListener;}

    /**
     * Returns the symbol used to display this enemy on the game map.
     *
     * @return the character "⟨E⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨E⟩";
    }

    /**
     * Heals the enemy by a certain amount, not exceeding the maximum of 50.
     * Does nothing if the enemy is already dead.
     *
     * @param amount the amount to heal
     */
    @Override
    public void heal(int amount){
        if (isDead())
            return;
        setHealth(getHealth() + amount);
        if(getHealth() > getMaxHealth())
            setHealth(getMaxHealth());
    }

    /**
     * Handles logic when the enemy is defeated, such as dropping loot on the game map.
     */
    @Override
    public void defeat() {
        GameMap map = GameMap.getInstance();
        setVisible(false);
        map.addEntity(new Treasure(getPosition(), true, "Loot", getLoot()));
    }

    @Override
    public String getLogName() {
        return "Enemy: " + this.getClass().getSimpleName();
    }

    /**
     * Gets the amount of loot the enemy holds.
     *
     * @return the loot value
     */
    private int getLoot() {
        return loot;
    }

    /**
     * Handles the fight action for enemies that have not implemented specific combat behavior.
     * @param target the combatant to attack
     */
    @Override
    public void fight(Combatant target) {
        System.out.println("Choose a enemy type to be able to fight");
    }

    /**
     * Triggers the screen listener with an {@link ScreenAction#ENEMY_ACTION} event,
     * typically used for animating or updating the UI on enemy turns.
     */
    public void threadAction() {
        if(screenListener != null) {
            screenListener.onAction(ScreenAction.ENEMY_ACTION, this);
        }
    }

    /**
     * Returns the base character instance. Useful for decorator compatibility.
     *
     * @return this enemy instance
     */
    public Enemy getBaseCharacter() {
        return this;
    }

    /**
     * Returns a readable name for this enemy type.
     * Subclasses may override to return more specific names.
     *
     * @return the string "Enemy"
     */
    public String getEnemyTypeName(){
        return "Enemy";
    }
}
