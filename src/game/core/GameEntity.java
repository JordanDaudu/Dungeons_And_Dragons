package game.core;

import game.map.Position;

public interface GameEntity {

    public Position getPosition();
    public void setPosition(Position newPos);
    public String getDisplaySymbol();
    public void setVisible(boolean visible);
}
