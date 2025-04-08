package game.items;

import game.core.GameEntity;
import game.map.Position;

public abstract class GameItem implements GameEntity {

    private Position position;
    private boolean blocksMovement;
    private String description;
    private boolean visible; // Added myself

    public GameItem(Position position, boolean blocksMovement, String description) {

        this.position = new Position(position);
        this.blocksMovement = blocksMovement;
        this.description = description;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position newPos) {
        this.position = new Position(newPos);
    }

    @Override
    public String getDisplaySymbol() {
        return "SYMBOL TO BE ADDED";
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
