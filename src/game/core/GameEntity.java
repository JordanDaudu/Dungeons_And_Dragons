package game.core;

import game.map.Position;

public interface GameEntity {

    Position getPosition(); // Return position
    void setPosition(Position newPos); // Set Position
    String getDisplaySymbol(); // Graphic representation
    boolean setVisible(boolean visible); // Visibility
    boolean isVisible();
}
