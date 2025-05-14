package game.global_events;

import game.core.GameEntity;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.RandomUtil;
import game.map.GameMap;
import game.map.Position;

import java.util.*;

public class SandstormEvent implements GlobalEvent {

    String directionName = "";

    public SandstormEvent() {}

    @Override
    public void execute(GameMap map, ScreenListener gameController) {
        int random = RandomUtil.getRandomInt(4);
        switch (random) {
            case 0 -> {
                directionName = "right";
                map.applySandstorm(new Position(0, 1));
            }
            case 1 -> {
                directionName = "left";
                map.applySandstorm(new Position(0, -1));
            }
            case 2 -> {
                directionName = "up";
                map.applySandstorm(new Position(-1, 0));
            }
            case 3 -> {
                directionName = "down";
                map.applySandstorm(new Position(1, 0));
            }
        }
        gameController.onAction(ScreenAction.GLOBAL_EVENT, this);
    }

    @Override
    public String getName() {
        return "Sandstorm - Entities shift 1 tile " + directionName + "!";
    }
}
