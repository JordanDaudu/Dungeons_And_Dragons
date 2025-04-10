package game.items;

import game.characters.PlayerCharacter;
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
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "position=" + position +
                ", blocksMovement=" + blocksMovement +
                ", description='" + description + '\'' +
                ", visible=" + visible +
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
        GameItem that = (GameItem) obj;
        return blocksMovement == that.blocksMovement &&
                visible == that.visible &&
                position.equals(that.position) &&
                description.equals(that.description);
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
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
