package game.characters;

import game.combat.Combatant;
import game.core.Inventory;
import game.items.GameItem;
import game.items.Potion;
import game.items.PowerPotion;
import game.map.Position;

public class PlayerCharacter extends AbstractCharacter {

    private String name;
    private Inventory inventory;
    private int treasurePoints;

    public PlayerCharacter(Position position, String name) {

        super(position);
        this.name = name;
        inventory = new Inventory();
        treasurePoints = 0;
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
                heal(((Potion) item).getIncreaseAmount());
                ((Potion) item).setIsUsed(true);
                inventory.removeItem(item);
                return true;
            }
        }
        return false;
    }

    public boolean usePowerPotion() {
        for(GameItem item : inventory.getItems()) {
            if(item instanceof PowerPotion) {
                addPower(((PowerPotion) item).getIncreaseAmount());
                ((PowerPotion) item).setIsUsed(true);
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
    public void receiveDamage(int amount, Combatant source) {
        // NEEDS TO BE IMPLEMENTED
        //
        //
        //
    }

    @Override
    public String getDisplaySymbol() {
        return "Character";
    }
}
