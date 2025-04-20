package game.characters;

import game.combat.Combatant;
import game.core.Inventory;
import game.core.PlayerMovement;
import game.items.Interactable;
import game.items.Potion;
import game.items.PowerPotion;
import game.map.Position;

/**
 * Represents a player-controlled character in the game.
 * Inherits combat and position logic from AbstractCharacter,
 * and adds inventory management, treasure tracking, and movement.
 */
public class PlayerCharacter extends AbstractCharacter implements PlayerMovement {

    // Data Members
    private String name;
    private Inventory inventory;
    private int treasurePoints;

    // Methods
    /**
     * Constructs a PlayerCharacter with the specified name.
     * Initializes inventory and treasure points.
     *
     * @param name the name of the character
     */
    public PlayerCharacter(String name) {

        super();
        this.name = name;
        inventory = new Inventory();
        treasurePoints = 0;
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
                inventory.equals(that.inventory);
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
    public boolean updateTreasurePoint(int amount) {
        treasurePoints += amount;
        System.out.println("Your total now is: " + getTreasurePoints());
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
     * @return the display symbol ("C")
     */
    @Override
    public String getDisplaySymbol() {
        return "C";
    }

    /**
     * Calculates the new position if the player moves right.
     *
     * @param player the current player
     * @return the new position
     */
    @Override
    public Position MoveRight(PlayerCharacter player){
        Position currentPosition = player.getPosition();
        return new Position(currentPosition.getRow(),currentPosition.getCol() + 1);
    }

    /**
     * Calculates the new position if the player moves left.
     *
     * @param player the current player
     * @return the new position
     */
    @Override
    public Position MoveLeft(PlayerCharacter player){
        Position currentPosition = player.getPosition();
        return new Position(currentPosition.getRow(), currentPosition.getCol() - 1);
    }

    /**
     * Calculates the new position if the player moves up.
     *
     * @param player the current player
     * @return the new position
     */
    @Override
    public Position MoveUp(PlayerCharacter player){
        Position currentPosition = player.getPosition();
        return new Position(currentPosition.getRow() - 1, currentPosition.getCol());
    }

    /**
     * Calculates the new position if the player moves down.
     *
     * @param player the current player
     * @return the new position
     */
    @Override
    public Position MoveDown(PlayerCharacter player){
        Position currentPosition = player.getPosition();
         return new Position(currentPosition.getRow() + 1, currentPosition.getCol());
    }

    /**
     * Prints the contents of the player's inventory.
     * If the inventory is empty, a message is displayed to indicate that.
     */
    public void printInventoryOfPlayer(){
        if (this.isEmpty())
            System.out.println("Your inventory is empty.");
        else {
            this.inventory.printInventory();
        }
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
     * Prints a game over message along with the player's total treasure points.
     */
    @Override
    public void defeat() {
        System.out.println("\n||GAME OVER " + getName() + "!||\n");
        System.out.println(getName() + " you gained " + getTreasurePoints() + " treasure point(s) in total!\n");
    }
}