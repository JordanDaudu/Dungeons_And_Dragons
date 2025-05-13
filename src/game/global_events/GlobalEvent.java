package game.global_events;

import game.core.ScreenListener;
import game.map.GameMap;

public interface GlobalEvent {
    void execute(GameMap map, ScreenListener gameController);
    String getName(); // For logs or GUI messages
}
