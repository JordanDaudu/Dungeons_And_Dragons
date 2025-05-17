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

/**
 * Represents a global Sandstorm event that damages all visible characters
 * and shifts all entities in a random direction by one tile.
 */
public class SandstormEvent implements GlobalEvent {

    // Data Members
    String directionName = "";

    // Methods
    /**
     * Constructs a SandstormEvent.
     */
    public SandstormEvent() {}

    /**
     * Executes the Sandstorm event:
     * - Deals 1 damage to each visible character on the map and triggers a damage animation.
     * - Randomly shifts all entities in one of four directions (up, down, left, right).
     * - Notifies the game controller of the global event.
     *
     * @param map the game map where the sandstorm takes place
     * @param gameController the game controller to notify of the event
     */
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

    /**
     * Returns a description of the sandstorm event, including the shift direction.
     *
     * @return a string describing the sandstorm event
     */
    @Override
    public String getName() {
        return "Sandstorm - Entities shift 1 tile " + directionName + " and characters receive 1 damage!";
    }
}
