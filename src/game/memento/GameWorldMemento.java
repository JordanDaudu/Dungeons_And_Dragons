package game.memento;

import game.characters.*;
import game.core.GameEntity;
import game.engine.GameSettings;
import game.engine.GameWorld;
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

/**
 * Memento class that stores a snapshot of the game world state,
 * including players, enemies, items, the game grid, and settings.
 */
public class GameWorldMemento implements Serializable {

    // Data Members
    private final List<PlayerCharacter> savedPlayers = new ArrayList<>();
    private final List<Enemy> savedEnemies = new ArrayList<>();
    private final List<GameItem> savedItems = new ArrayList<>();
    private final PlayerCharacter currentPlayer;
    private final GameSettings gameSettings;
    private final ConcurrentMap<Position , List<GameEntity>> gridCopy;
    private final LocalDateTime timestamp;

    // Methods
    /**
     * Creates a memento by deep copying the given lists and grid.
     *
     * @param players the list of player characters to save
     * @param enemies the list of enemies to save
     * @param items the list of game items to save
     * @param grid the current game grid map to save
     * @param gameSettings the current game settings to save
     * @throws CloneNotSupportedException if cloning of any entity fails
     */
    public GameWorldMemento(List<PlayerCharacter> players,List<Enemy>enemies,List<GameItem> items,ConcurrentMap<Position , List<GameEntity>> grid, GameSettings gameSettings, PlayerCharacter currentPlayer) throws CloneNotSupportedException {
    for(PlayerCharacter player : players) {
        savedPlayers.add((PlayerCharacter) player.callClone());
    }
    for (Enemy enemy: enemies) {
        savedEnemies.add((Enemy) enemy.callClone());
    }
    for (GameItem item: items) {
        savedItems.add((GameItem) item.callClone());
    }
    this.currentPlayer = currentPlayer;
    this.gridCopy = deepCopyGrid(grid);
    this.gameSettings = new GameSettings(gameSettings);
    this.timestamp = LocalDateTime.now();
    }

    /**
     * Returns a list of cloned saved player characters.
     *
     * @return list of cloned players
     * @throws CloneNotSupportedException if cloning fails
     */
    public List<PlayerCharacter> getSavedPlayers() throws CloneNotSupportedException {
        List<PlayerCharacter> clones = new ArrayList<>();
        for (PlayerCharacter p : savedPlayers) {
            clones.add((PlayerCharacter) p.callClone());
        }
        return clones;
    }

    /**
     * Returns a list of cloned saved enemies.
     *
     * @return list of cloned enemies
     * @throws CloneNotSupportedException if cloning fails
     */
    public List<Enemy> getSavedEnemies() throws CloneNotSupportedException {
        List<Enemy> clones = new ArrayList<>();
        for (Enemy e : savedEnemies) {
            clones.add((Enemy) e.callClone());
        }
        return clones;
    }

    /**
     * Returns a list of cloned saved game items.
     *
     * @return list of cloned items
     * @throws CloneNotSupportedException if cloning fails
     */
    public List<GameItem> getSavedItems() throws CloneNotSupportedException {
        List<GameItem> clones = new ArrayList<>();
        for (GameItem item : savedItems) {
            clones.add((GameItem) item.callClone());
        }
        return clones;
    }

    /**
     * Returns a deep copy of the saved game grid map.
     *
     * @return deep copy of the grid map
     * @throws CloneNotSupportedException if cloning fails
     */
    public ConcurrentMap<Position, List<GameEntity>> getSavedMap() throws CloneNotSupportedException {
        return deepCopyGrid(gridCopy);
    }

    public PlayerCharacter getCurrentPlayer() {return currentPlayer;}

    /**
     * Returns the saved game settings.
     *
     * @return the game settings snapshot
     */
    public GameSettings getGameSettings() {return gameSettings;}

    /**
     * Returns the timestamp of when this memento was created,
     * formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return formatted timestamp string
     */
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

