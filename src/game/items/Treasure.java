package game.items;

import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

public class Treasure extends GameItem implements Interactable {

    private int value;
    boolean collected;

    public Treasure(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
        this.value = RandomUtil.getRandomInt(100, 301);
        collected = false;
    }

    public Treasure(Position position, boolean blocksMovement, String description, int loot) {
        super(position, blocksMovement, description);
        this.value = loot;
        collected = false;
    }

    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", value=" + value +
                ", collected=" + collected +
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
        if (!super.equals(obj)) {
            return false;
        }
        Treasure treasure = (Treasure) obj;
        return value == treasure.value &&
                collected == treasure.collected;
    }

    @Override
    public void interact(PlayerCharacter c) {
        if(c.getPosition().distanceTo(getPosition()) == 1) {
            if(!collected) {
                collected = true;
                int randomizer = RandomUtil.getRandomInt(0, 6);
                if(randomizer >= 0 && randomizer <= 2) {
                    // ADD TREASURE POINTS
                    c.updateTreasurePoint(value);
                }
                else if(randomizer >= 3 && randomizer <= 4) {
                    // ADD TO INVENTORY POTION
                    System.out.println("Potion added!");
                    c.addToInventory(new Potion(null, false, "Potion"));
                }
                else {
                    // ADD TO INVENTORY POWER POTION
                    System.out.println("Power Potion added!");
                    c.addToInventory(new PowerPotion(null, false, "Power Potion"));
                }
            }
        }
    }

    @Override
    public String getDisplaySymbol() { return "T"; }
}
