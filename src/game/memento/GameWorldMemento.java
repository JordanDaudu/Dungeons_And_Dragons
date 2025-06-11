package game.memento;

import game.characters.*;
import game.core.GameEntity;
import game.engine.GameSettings;
import game.items.*;
import game.map.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameWorldMemento implements Serializable {
    // Memento

    private final List<PlayerCharacter> savedPlayers = new ArrayList<>();
    private final List<Enemy> savedEnemies = new ArrayList<>();
    private final List<GameItem> savedItems = new ArrayList<>();
    private final GameSettings gameSettings;
    private final ConcurrentMap<Position , List<GameEntity>> gridCopy;
    private final LocalDateTime timestamp;


    public GameWorldMemento(List<PlayerCharacter> players,List<Enemy>enemies,List<GameItem> items,ConcurrentMap<Position , List<GameEntity>> grid, GameSettings gameSettings) throws CloneNotSupportedException {
    for(PlayerCharacter player : players) {
        savedPlayers.add((PlayerCharacter) player.callClone());
    }
    for (Enemy enemy: enemies) {
        savedEnemies.add((Enemy) enemy.callClone());
    }
    for (GameItem item: items) {
        savedItems.add((GameItem) item.callClone());
    }
    this.gridCopy = deepCopyGrid(grid);
    this.gameSettings = new GameSettings(gameSettings);
    this.timestamp = LocalDateTime.now();
    }

    public List<PlayerCharacter> getSavedPlayers() throws CloneNotSupportedException {
        List<PlayerCharacter> clones = new ArrayList<>();
        for (PlayerCharacter p : savedPlayers) {
            clones.add((PlayerCharacter) p.callClone());
        }
        return clones;
    }

    // Return fresh clones of savedEnemies
    public List<Enemy> getSavedEnemies() throws CloneNotSupportedException {
        List<Enemy> clones = new ArrayList<>();
        for (Enemy e : savedEnemies) {
            clones.add((Enemy) e.callClone());
        }
        return clones;
    }

    // Return fresh clones of savedItems
    public List<GameItem> getSavedItems() throws CloneNotSupportedException {
        List<GameItem> clones = new ArrayList<>();
        for (GameItem item : savedItems) {
            clones.add((GameItem) item.callClone());
        }
        return clones;
    }

    // Return a fresh deep copy of the gridCopy map
    public ConcurrentMap<Position, List<GameEntity>> getSavedMap() throws CloneNotSupportedException {
        return deepCopyGrid(gridCopy);
    }

    public GameSettings getGameSettings() {return gameSettings;}

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    public ConcurrentMap<Position, List<GameEntity>> deepCopyGrid(Map<Position, List<GameEntity>> originalGrid) throws CloneNotSupportedException {
        ConcurrentMap<Position, List<GameEntity>> newGrid = new ConcurrentHashMap<>();
        for (Map.Entry<Position, List<GameEntity>> entry : originalGrid.entrySet()) {
            List<GameEntity> clonedEntities = new ArrayList<>();
            for (GameEntity gameEntity : entry.getValue()) {
                clonedEntities.add((GameEntity)gameEntity.callClone());
            }
            newGrid.put(new Position(entry.getKey()), clonedEntities);
        }
        return newGrid;
    }
}

