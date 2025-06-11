package game.engine;

import game.characters.*;
import game.core.GameEntity;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.decorator.PlayerDecorator;
import game.items.GameItem;
import game.logging.GameLogger;
import game.map.GameMap;
import game.map.Position;
import game.memento.GameWorldMemento;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class for running the Dungeons & Dragons-like game.
 * Manages the game loop, player actions, world setup, and entity interactions.
 */
public class GameWorld {

    // Data Members
    private List<PlayerCharacter> players;
    private List<Enemy> enemies;
    private List<GameItem> items;
    private GameMap map;
    private GameSettings gameSettings;
    private PlayerCharacter currentPlayer;
    private static volatile GameWorld instance = null;
    private ScreenListener controllerListener = null;

    // Methods
    /**
     * Constructs the game world with a map of specified size.
     *
     * @param gameSettings settings of the game
     */
    private GameWorld(GameSettings gameSettings) {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.gameSettings = new GameSettings(gameSettings);
        this.map = GameMap.getInstance();
        this.map.init(gameSettings.getRows(), gameSettings.getCols());
    }

    // This method is called once to initialize the world with custom size
    /**
     * Initializes the singleton instance of the game world with a custom map size.
     *
     * @param gameSettings settings of the game
     */
    public static void initialize(GameSettings gameSettings) {
        if (instance == null) {
            synchronized (GameWorld.class) {
                if(instance == null) {
                    instance = new GameWorld(gameSettings); // Create instance only once
                }
            }
        }
        else {
            throw new IllegalStateException("GameWorld is already initialized.");
        }
    }

    /**
     * Gets the singleton instance of the game world.
     * Needs to initialize it before using getInstance()
     *
     * @return the GameWorld instance
     */
    public static GameWorld getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameWorld is not initialized.");
        }
        return instance;
    }

    /**
     * Checks whether the game world has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    public void setControllerListener(ScreenListener controllerListener) {
        this.controllerListener = controllerListener;
    }

    /**
     * Gets the list of all players in the game.
     *
     * @return list of player characters
     */
    public List<PlayerCharacter> getPlayers() {
        return players;
    }

    /**
     * Gets the list of all enemies in the game.
     *
     * @return list of enemy characters
     */
    public List<Enemy> getEnemies() {
        for (Enemy e : enemies) {
            System.out.println("Stored enemy in GameWorld: " + e.getClass().getSimpleName());
        }
        return enemies;
    }

    /**
     * Gets the list of all items in the game.
     *
     * @return list of game items
     */
    public List<GameItem> getItems() {
        return items;
    }

    /**
     * Gets the game map instance.
     *
     * @return the game map
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * Gets the game settings.
     *
     * @return the game settings
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Gets the player whose turn is currently active.
     *
     * @return the current player
     */
    public PlayerCharacter getCurrentPlayer() {return currentPlayer;}

    /**
     * Sets the current active player for the game turn.
     *
     * @param p the player character to set as current
     */
    public void setCurrentPlayer(PlayerCharacter p) {
        currentPlayer = p;
    }

    /**
     * Removes a player character from the map but not from the player list.
     *
     * @param player the player to remove from the map
     */
    public void removePlayerFromMap(PlayerCharacter player) {
        map.removeEntity(player);
    }

    /**
     * Removes a player from both the player list and the map.
     *
     * @param player the player character to remove
     */
    public void removePlayer(PlayerCharacter player) {
        players.remove(player);
        map.removeEntity(player);
    }

    /**
     * Removes an enemy from both the enemy list and the map.
     *
     * @param enemy the enemy to remove
     */
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
        map.removeEntity(enemy);
    }

    /**
     * Removes an item from both the item list and the map.
     *
     * @param item the game item to remove
     */
    public void removeItem(GameItem item) {
        items.remove(item);
        map.removeEntity(item);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * Scans the map and collects all player characters into the player list.
     */
    public void collectPlayersFromMap() {
        for (GameEntity entity : map.getAllEntities()) {
            if (entity instanceof PlayerCharacter player) {
                players.add(player);
            }
        }
    }

    /**
     * Scans the map and collects all enemies into the enemy list.
     */
    public void collectEnemiesFromMap() {
        for (GameEntity entity : map.getAllEntities()) {
            if (entity instanceof Enemy enemy) {
                enemies.add(enemy);
                System.out.println("Added enemy: " + enemy.getClass().getSimpleName());
            }
        }
    }

    /**
     * Scans the map and collects all items into the item list.
     */
    public void collectItemsFromMap() {
        for (GameEntity entity : map.getAllEntities()) {
            if (entity instanceof GameItem item) {
                items.add(item);
            }
        }
    }

    public void updateCharacterInWorldAndMap(PlayerCharacter newCharacter) {
        UUID idToReplace = newCharacter.getId();

        // 1. Find old character in players or enemies lists
        PlayerCharacter oldPlayer = null;
        Enemy oldEnemy = null;
        Position characterPosition = null;
        ReentrantLock mapLock = getMap().getMapLock();
        mapLock.lock();
        try {
            // 2. Find old character's position in the map
            for (PlayerCharacter p : players) {
                if (p.getId().equals(idToReplace)) {
                    oldPlayer = p;
                    characterPosition = oldPlayer.getPosition();
                    break;
                }
            }

            if (oldPlayer == null) {
                for (Enemy e : enemies) {
                    if (e.getId().equals(idToReplace)) {
                        oldEnemy = e;
                        characterPosition = oldEnemy.getPosition();
                        break;
                    }
                }
            }

            if(characterPosition == null) {
                System.err.println("Character with ID " + idToReplace + " not found in GameWorld.");
                GameLogger.getInstance().log("Character with ID " + idToReplace + " not found in GameWorld.");
                return;
            }

            // 3. Remove old character from list and map
            if (oldPlayer != null) {
                players.remove(oldPlayer);
                map.removeEntity(oldPlayer);
            }
            if (oldEnemy != null) {
                enemies.remove(oldEnemy);
                map.removeEntity(oldEnemy);
            }

            // 4. Add new character to list and map at the same position
            if (newCharacter instanceof PlayerCharacter player) {
                players.add(player);
                currentPlayer = player;
                map.addEntity(newCharacter);
            }
            else {
                System.err.println("New character is neither PlayerCharacter nor Enemy.");
                GameLogger.getInstance().log("New character is neither PlayerCharacter nor Enemy.");
            }
        }
        finally {
            mapLock.unlock();
        }
    }

    /**
     * Attaches the game controller (screen listener) to all enemies
     * and schedules their actions using the provided executor and atomic flag.
     *
     * @param gameController the screen listener for handling enemy actions
     * @param enemyScheduler the scheduled executor to schedule enemy tasks
     * @param atomicBoolean atomic flag to control scheduling lifecycle
     */
    public void attachGameControllerToEnemies(ScreenListener gameController, ScheduledExecutorService enemyScheduler, AtomicBoolean atomicBoolean) {
        for(Enemy enemy : enemies) {
            enemy.setScreenListener(gameController);
            enemyScheduler.schedule(new EnemyTask(enemy, enemyScheduler, atomicBoolean), 1, TimeUnit.SECONDS);
            EnemyTask.addScheduledEnemy(enemy);
        }
    }

    /**
     * Returns a list of walkable (non-occupied, in-bounds) adjacent positions around the given position.
     *
     * @param center the position to check from
     * @return list of valid adjacent positions
     */
    public List<Position> getAdjacentFreePositions(Position center) {
        List<Position> positions = new ArrayList<>();

        int row = center.getRow();
        int col = center.getCol();

        Position[] candidates = {
                new Position(row - 1, col), // Up
                new Position(row + 1, col), // Down
                new Position(row, col - 1), // Left
                new Position(row, col + 1)  // Right
        };

        for (Position pos : candidates) {
            if (map.isValidPosition(pos) && map.getEntityGameItemAt(pos) == null) {
                positions.add(pos);
            }
        }
        return positions;
    }

    public void setPlayers(List<PlayerCharacter> players) { this.players = players; }

    public void setEnemies(List<Enemy> enemies) { this.enemies = enemies; }

    public void setItems(List<GameItem> items) { this.items = items; }

    public void setMap(GameMap map) { this.map = map; }

    public void clearGameWorld() {
        players.clear();
        enemies.clear();
        items.clear();
        map.getGrid().clear();
    }

    public GameWorldMemento createMemento() throws CloneNotSupportedException {
        return new GameWorldMemento(players,enemies,items,getMap().getGrid(), gameSettings);}

    public void loadFromMemento(GameWorldMemento memento) throws CloneNotSupportedException {
        if(memento != null) {
            UUID currentPlayerId = getCurrentPlayer().getId();
            clearGameWorld();
            players.addAll(memento.getSavedPlayers());
            enemies.addAll(memento.getSavedEnemies());
            items.addAll(memento.getSavedItems());
            map.getGrid().putAll(memento.getSavedMap());
            gameSettings = memento.getGameSettings();

            // Restoring map intended size
            map.init(gameSettings.getRows(), gameSettings.getCols());

            currentPlayer = players.stream().filter(p -> p.getId().equals(currentPlayerId))
                    .findFirst()
                    .orElse(players.getFirst()); // Fallback

            // Launch new game window
            if(controllerListener != null) {
                controllerListener.onAction(ScreenAction.LOAD_DATA, (Object) null);
            }
            else {
                System.err.println("No controller listener in GameWorld");
            }
        }
    }
}

