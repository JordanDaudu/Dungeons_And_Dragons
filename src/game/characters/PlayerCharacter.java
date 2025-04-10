package game.characters;

import game.core.Inventory;
import game.items.GameItem;
import game.items.Interactable;
import game.items.Potion;
import game.items.PowerPotion;

public class PlayerCharacter extends AbstractCharacter {

    private String name;
    private Inventory inventory;
    private int treasurePoints;

    public PlayerCharacter(String name) {

        super();
        this.name = name;
        inventory = new Inventory();
        treasurePoints = 0;
    }

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

    public String getName() {
        return name;
    }

    public boolean addToInventory(GameItem item) {
        return inventory.addItem(item);
    }

    public boolean usePotion() {
        for(GameItem item : inventory.getItems()) {
            if(item instanceof Potion) {
                ((Interactable) item).interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    public boolean usePowerPotion() {
        for(GameItem item : inventory.getItems()) {
            if(item instanceof PowerPotion) {
                ((Interactable) item).interact(this);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    public boolean updateTreasurePoint(int amount) {
        treasurePoints += amount;
        return true;
    }

    public int getTreasurePoints() {
        return treasurePoints;
    }

    @Override
    public String getDisplaySymbol() {
        return "C";
    }
}
