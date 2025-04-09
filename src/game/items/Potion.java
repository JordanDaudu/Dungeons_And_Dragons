package game.items;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

public class Potion extends GameItem implements Interactable {

    private int increaseAmount;
    private boolean isUsed;

    public Potion(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
        increaseAmount = initializeIncreaseAmount();
        isUsed = false;
    }

    public int getIncreaseAmount() {
        return increaseAmount;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    @Override
    public void interact(PlayerCharacter c) {
        if(!isUsed) {
            c.heal(increaseAmount);
            isUsed = true;
        }
    }

    protected int initializeIncreaseAmount() {
        return RandomUtil.getRandomInt(10, 51);
    }
}
