package game.characters;

import game.items.Treasure;
import game.map.GameMap;
import game.map.Position;

public class Enemy extends AbstractCharacter{

    private int loot;

    public Enemy() {
        super();
        setHealth(50);
        loot = 0;
    }

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

    // equals method to compare enemy objects
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

    @Override
    public String getDisplaySymbol() {
        return "E";
    }

    @Override
    public void heal(int amount){
        if (isDead())
            return;
        setHealth(getHealth() + amount);
        if(getHealth() > 50)
            setHealth(50);
    }

    public void defeat() {
        GameMap map = GameMap.getInstance();
        System.out.println("ADDING TREASURE!");
        map.addEntity(new Treasure(getPosition(), true, "Loot", getLoot()));
    }

    private int getLoot() {
        return loot;
    }
}
