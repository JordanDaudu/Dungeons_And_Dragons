package game.core;

import game.map.Position;

public interface GameEntity {

    Position getPosition(); // Return position
    void setPosition(Position newPos); // Set Position
    String getDisplaySymbol(); // Graphic representation
    void setVisible(boolean visible); // Visibility
}
