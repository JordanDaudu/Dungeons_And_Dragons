package game.core;

import game.characters.PlayerCharacter;
import game.map.Position;

public interface PlayerMovement {
    Position MoveRight(PlayerCharacter player);
    Position MoveLeft(PlayerCharacter player);
    Position MoveUp(PlayerCharacter player);
    Position MoveDown(PlayerCharacter player);
}
