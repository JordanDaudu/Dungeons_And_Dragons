package game.items;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

public class Potion extends GameItem implements Interactable {

    private final int increaseAmount;
    private boolean isUsed;

    public Potion(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
        increaseAmount = initializeIncreaseAmount();
        isUsed = false;
    }

    @Override
    public String toString() {
        // Getting super.toString() in a clean way to append
        String parentString = super.toString();
        String cleanedParentString = parentString.substring(parentString.indexOf("{") + 1, parentString.lastIndexOf("}"));

        return getClass().getSimpleName() + "{" +
                cleanedParentString +
                ", increaseAmount=" + increaseAmount +
                ", isUsed=" + isUsed +
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
        Potion potion = (Potion) obj;
        return increaseAmount == potion.increaseAmount &&
                isUsed == potion.isUsed;
    }


    public int getIncreaseAmount() {
        return increaseAmount;
    }

    protected boolean getIsUsed() { return isUsed; }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    @Override
    public void interact(PlayerCharacter c) {
        if(c.getPosition().distanceTo(getPosition()) == 1) {
            if(!isUsed) {
                System.out.println(c.getName() + " was healed " + getIncreaseAmount() + "HP");
                c.heal(increaseAmount);
                isUsed = true;
            }
        }
    }

    @Override
    public String getDisplaySymbol() { return "H"; }

    protected int initializeIncreaseAmount() {
        return RandomUtil.getRandomInt(10, 51);
    }
}
