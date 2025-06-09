package game.characters;

import game.combat.Combatant;
import game.core.Inventory;
import game.core.PlayerMovement;
import game.decorator.CharacterDecorator;
import game.items.Interactable;
import game.items.Potion;
import game.items.PowerPotion;
import game.logging.GameLogger;
import game.map.Position;

import java.util.UUID;
import java.io.Serializable;

/**
 * Represents a player-controlled character in the game.
 * Inherits combat and position logic from AbstractCharacter,
 * and adds inventory management, treasure tracking, and movement.
 */
public abstract class PlayerCharacter extends AbstractCharacter implements PlayerMovement, Serializable {

    // Data Members
    private final String name;
    private final Inventory inventory;
    private int treasurePoints;
    private final UUID id;
    private CharacterDecorator ability1 = null;
    private CharacterDecorator ability2 = null;


    // Methods
    /**
     * Constructs a PlayerCharacter with the specified name.
     * Initializes inventory and treasure points.
     *
     * @param name the name of the character
     */
    public PlayerCharacter(String name) {
        super();
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1); // making sure starts with an uppercase
        inventory = new Inventory();
        treasurePoints = 0;
        this.id = UUID.randomUUID(); // Unique per player
    }

    public PlayerCharacter(PlayerCharacter other) {
        super(other);
        this.name = other.name;
        inventory = new Inventory(other.inventory);
        treasurePoints = other.treasurePoints;
        this.id = other.id;
        this.ability1 = other.ability1;
        this.ability2 = other.ability2;
    }

    /**
     * Returns a string representing the state of the player.
     * Includes inherited fields, name, treasure, and inventory.
     *
     * @return a string describing the player character
     */
    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +  // Remove the initial class name and '{' from the super.toString()
                ", name = \"" + name + "\"" +
                ", treasurePoints = " + treasurePoints +
                ", inventory = " + inventory +
                ", ID = " + id +
                '}';
    }

    /**
     * Compares this player character to another object for equality.
     *
     * @param obj the object to compare
     * @return true if both objects represent the same player state
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {  // Call the equals method of AbstractCharacter
            return false;
        }
        PlayerCharacter that = (PlayerCharacter) obj;
        return treasurePoints == that.treasurePoints &&
                name.equals(that.name) &&
                inventory.equals(that.inventory) && id.equals(that.id);
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the player's inventory.
     *
     * @return the Inventory object containing the player's items
     */
    public Inventory getInventory() {return inventory;}

    public UUID getId() {
        return id;
    }

    public void setAbility1(CharacterDecorator ability1) {
        this.ability1 = ability1;
    }

    public void setAbility2(CharacterDecorator ability2) {
        this.ability2 = ability2;
    }

    public CharacterDecorator getAbility1() {return ability1;}

    public CharacterDecorator getAbility2() {return ability2;}

    /**
     * Adds a game item to the player's inventory.
     *
     * @param item the item to add
     * @return true if added successfully
     */
    public boolean addToInventory(Interactable item) {
        return inventory.addItem(item);
    }

    /**
     * Uses the first available Potion in the inventory, if any.
     * Triggers interaction and removes the item from inventory.
     *
     * @return true if a potion was used
     */
    public boolean usePotion() {
        for(Interactable item : inventory.getItems()) {
            if(item.getClass() == Potion.class) {
                item.interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Uses the first available PowerPotion in the inventory, if any.
     * Triggers interaction and removes the item from inventory.
     *
     * @return true if a power potion was used
     */
    public boolean usePowerPotion() {
        for(Interactable item : inventory.getItems()) {
            if(item.getClass() == PowerPotion.class) {
                item.interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Increases the player's treasure points.
     *
     * @param amount amount to add
     * @return true always
     */
    public boolean updateTreasurePoints(int amount) {
        treasurePoints += amount;
        return true;
    }

    /**
     * Gets the total treasure points collected by the player.
     *
     * @return the treasure points
     */
    public int getTreasurePoints() {
        return treasurePoints;
    }

    /**
     *
     * @return if the player inventory is empty
     */
    public boolean isEmpty() {
        return inventory.getItems().isEmpty();
    }

    /**
     * Gets the symbol used to represent the player on the map.
     *
     * @return the display symbol "⟨C⟩"
     */
    @Override
    public String getDisplaySymbol() {
        return "⟨C⟩";
    }

    /**
     * Calculates the new position if the player moves right.
     *
     * @return the new position
     */
    @Override
    public Position moveRight(){
        Position currentPosition = this.getPosition();
        return new Position(currentPosition.getRow(), currentPosition.getCol() + 1);
    }

    /**
     * Calculates the new position if the player moves left.
     *
     * @return the new position
     */
    @Override
    public Position moveLeft(){
        Position currentPosition = this.getPosition();
        return new Position(currentPosition.getRow(), currentPosition.getCol() - 1);
    }

    /**
     * Calculates the new position if the player moves up.
     *
     * @return the new position
     */
    @Override
    public Position moveUp(){
        Position currentPosition = this.getPosition();
        return new Position(currentPosition.getRow() - 1, currentPosition.getCol());
    }

    /**
     * Calculates the new position if the player moves down.
     *
     * @return the new position
     */
    @Override
    public Position moveDown(){
        Position currentPosition = this.getPosition();
        return new Position(currentPosition.getRow() + 1, currentPosition.getCol());
    }


    /**
     * Setting up for future class chosen
     *
     * @param target the combatant to attack
     */
    @Override
    public void fight(Combatant target) {
        System.out.println("Choose a class to be able to fight");
    }

    /**
     * Handles the defeat of the player character.
     * Sets the character as invisible, displays a game over message, and logs the event.
     */
    @Override
    public void defeat() {
        setVisible(false);
        GameLogger.getInstance().log("Player: " + this.getName() + " is dead");
    }

    /**
     * Gets the log name of the player character for logging purposes.
     *
     * @return a formatted string with the player's name
     */
    @Override
    public String getLogName() {
        return "Player: " + getName();
    }
}