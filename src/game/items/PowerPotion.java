package game.items;

import game.engine.RandomUtil;
import game.map.Position;

public class PowerPotion extends Potion {

    public PowerPotion(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
    }

    @Override
    protected int initializeIncreaseAmount() {
        return RandomUtil.getRandomInt(1, 6);
    }
}
