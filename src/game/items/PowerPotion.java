package game.items;

import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

public class PowerPotion extends Potion {

    public PowerPotion(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
    }

    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                "}";
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
        return true;
    }

    @Override
    public void interact(PlayerCharacter c) {
        if(c.getPosition().distanceTo(getPosition()) == 1) {
            if(!getIsUsed()) {
                c.addPower(getIncreaseAmount());
                setIsUsed(true);
            }
        }
    }

    @Override
    public String getDisplaySymbol() { return "P"; }

    @Override
    protected int initializeIncreaseAmount() {
        return RandomUtil.getRandomInt(1, 6);
    }
}
