package game.characters;

import game.combat.Combatant;
import game.engine.RandomUtil;
import game.items.Treasure;
import game.map.GameMap;

/**
 * Represents an enemy character in the game.
 * An Enemy has limited health and can drop loot (as a Treasure) when defeated.
 */
public abstract class Enemy extends AbstractCharacter {

    // Data Members
    private final int loot;

    // Methods
    /**
     * Constructs a default Enemy with 50 health and no loot.
     */
    public Enemy() {
        super();
        setHealth(getMaxHealth());
        loot = RandomUtil.getRandomInt(100, 301);
    }

    /**
     * Returns a string representation of the Enemy, including health and loot.
     *
     * @return a string describing the enemy
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString + // Remove the initial class name and '{' from the super.toString()
                ", loot=" + loot +
                '}';
    }

    /**
     * Compares this Enemy with another object for logical equality.
     *
     * @param obj the object to compare
     * @return true if the objects are logically equal
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
        System.out.println("ADDING TREASURE!");
        map.addEntity(new Treasure(getPosition(), true, "Loot", getLoot()));
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
}
