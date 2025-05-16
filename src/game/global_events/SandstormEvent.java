package game.global_events;

import game.characters.AbstractCharacter;
import game.core.GameEntity;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.RandomUtil;
import game.map.GameMap;
import game.map.Position;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class SandstormEvent implements GlobalEvent {

    String directionName = "";

    public SandstormEvent() {}

    @Override
    public void execute(GameMap map, ScreenListener gameController) {
        // Apply damage to all visible characters BEFORE movement
        Set<Position> allPositions = map.getAllPositions();
        for (Position pos : allPositions) {
            ReentrantLock lock = map.getLockForPosition(pos);
            lock.lock();
            try {
                List<GameEntity> entitiesAtPos = map.getEntitiesAt(pos); // Returns a copy
                for (GameEntity entity : entitiesAtPos) {
                    if (entity instanceof AbstractCharacter character && character.isVisible()) {
                        character.receiveDamage(1, null);
                        gameController.onAction(ScreenAction.RECEIVE_DAMAGE_TEXT_ANIMATION, null, pos, 1);
                        break;
                    }
                }
            }
            finally {
                lock.unlock();
            }
        }
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
        return "Sandstorm - Entities shift 1 tile " + directionName + " and characters receive 1 damage!";
    }
}
