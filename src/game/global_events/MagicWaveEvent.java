package game.global_events;

import game.core.ScreenAction;
import game.core.ScreenListener;
import game.map.GameMap;

public class MagicWaveEvent implements GlobalEvent {

    public MagicWaveEvent() {}

    @Override
    public void execute(GameMap map, ScreenListener gameController) {
        gameController.onAction(ScreenAction.GLOBAL_EVENT, this);
    }

    @Override
    public String getName() {
        return "Magic Wave - All entities take 2 damage!";
    }
}
