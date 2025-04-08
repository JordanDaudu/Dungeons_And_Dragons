package game.items;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;
import game.engine.RandomUtil;
import game.map.Position;

public class Treasure extends GameItem implements Interactable {

    private int value;
    boolean collected;

    public Treasure(Position position, boolean blocksMovement, String description) {
        super(position, blocksMovement, description);
        value = RandomUtil.getRandomInt(100, 301);
        collected = false;
    }

    @Override
    public void interact(PlayerCharacter c) {
        if(!collected) {
            collected = true;
            int randomizer = RandomUtil.getRandomInt(0, 6);
            if(randomizer >= 0 && randomizer <= 2)
                return;
                // ADD TO INVENTORY NEED TO IMPLEMENT
            else if(randomizer >= 3 && randomizer <= 4)
                return;
                // ADD TO CHARACTER TREASUREPOINTS
            else
                return;
                // ADD POWER TO CHARACTER
        }
    }
}
