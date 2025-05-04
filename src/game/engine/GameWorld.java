package game.engine;

import game.characters.*;
import game.core.GameEntity;
import game.items.GameItem;
import game.map.GameMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for running the Dungeons & Dragons-like game.
 * Manages the game loop, player actions, world setup, and entity interactions.
 */
public class GameWorld {

    // Data Members
    private final List<PlayerCharacter> players;
    private final List<Enemy> enemies;
    private final List<GameItem> items;
    private final GameMap map;
    private PlayerCharacter currentPlayer;
    private static GameWorld instance = null;

    // Methods
    /**
     * Constructs the game world with a map of specified size.
     *
     * @param row number of rows in the map
     * @param col number of columns in the map
     */
    private GameWorld(int row, int col) {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.items = new ArrayList<>();
        this.map = GameMap.getInstance();
        this.map.init(row, col);
    }

    // This method is called once to initialize the world with custom size
    /**
     * Initializes the singleton instance of the game world with a custom map size.
     *
     * @param row number of rows in the map
     * @param col number of columns in the map
     */
    public static void initialize(int row, int col) {
        if (instance == null) {
            instance = new GameWorld(row, col); // Create instance only once
        }
        else {
            throw new IllegalStateException("GameWorld is already initialized.");
        }
    }

    /**
     * Gets the singleton instance of the game world.
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

    public GameMap getMap() {
        return map;
    }

    /**
     * Gets the game map instance.
     *
     * @return the game map
     */
    public PlayerCharacter getCurrentPlayer() {return currentPlayer;}

    /**
     * Gets the player whose turn is currently active.
     *
     * @return the current player
     */
    public boolean setCurrentPlayer(PlayerCharacter p) {
        currentPlayer = p;
        return true;
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
}

