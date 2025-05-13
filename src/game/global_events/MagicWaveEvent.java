package game.global_events;

import game.characters.AbstractCharacter;
import game.core.GameEntity;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.map.GameMap;
import game.map.Position;

import java.util.List;

public class MagicWaveEvent implements GlobalEvent {
    @Override
    public void execute(GameMap map, ScreenListener gameController) {
        for (Position pos : map.getAllPositions()) {
            List<GameEntity> entities = map.getEntitiesAt(pos);
            synchronized (entities) {
                for (GameEntity c : entities) {
                    if (c instanceof AbstractCharacter character) {
                        character.receiveDamage(2, null);
                    }
                }
            }
        }
        gameController.onAction(ScreenAction.GLOBAL_EVENT, (Object) null);
    }

    @Override
    public String getName() {
        return "Magic Wave - All entities take 2 damage!";
    }
}
