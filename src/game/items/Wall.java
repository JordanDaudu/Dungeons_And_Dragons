package game.items;

import game.map.Position;

public class Wall extends GameItem {

    public Wall(Position position, String description) {
        super(position, true, description);
    }
}
