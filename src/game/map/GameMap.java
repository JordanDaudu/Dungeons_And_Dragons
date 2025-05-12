package game.map;

import game.characters.*;
import game.core.GameEntity;
import game.engine.RandomUtil;
import game.items.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton class that represents the 2D grid-based game map.
 * It manages the placement, removal, and visibility of all GameEntity objects,
 * including characters, enemies, and items.
 * Thread-safe: each tile contains a synchronized list of entities to support concurrent access.
 *
 * It also provides helper methods to:
 * - Populate the map with random entities.
 * - Check for collisions and entity presence.
 * - Update visibility range based on player position.
 *
 * Only one instance of GameMap exists during runtime.
 */
public class GameMap {

    // Data Members
    private final ConcurrentMap<Position, List<GameEntity>> grid;
    private int rows;
    private int cols;
    private static GameMap instance = null;

    // Methods
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the grid as an empty HashMap.
     */
    private GameMap() {
        grid = new ConcurrentHashMap<>();
    }

    /**
     * Initializes the grid dimensions and clears any existing data.
     * Must be called before using the map.
     *
     * @param rows Number of rows (minimum 10)
     * @param cols Number of columns (minimum 10)
     * @throws IllegalArgumentException if either dimension is less than 10
     */
    public void init(int rows, int cols) {
        if (rows < 10 || cols < 10)
            throw new IllegalArgumentException("Grid size must be at least 10x10");

        this.rows = rows;
        this.cols = cols;
        grid.clear();
    }

    /**
     * Returns the singleton instance of the GameMap.
     * Creates it if it does not already exist.
     *
     * @return the single GameMap instance
     */
    public static GameMap getInstance() {
        if(instance == null)
            instance = new GameMap();
        return instance;
    }

    /**
     * Adds a GameEntity to the map at its position.
     * Ensures the entity list for the tile is thread-safe.
     *
     * @param entity the game entity to add
     */
    public void addEntity(GameEntity entity) {
        /*
          Each Position in the map grid holds a thread-safe list of GameEntities.
          We use Collections.synchronizedList(...) to wrap each ArrayList at creation time,
          ensuring thread-safe access to entity lists per tile.

          All compound actions (like iteration, multiple-step logic) on these lists
          are additionally guarded with synchronized blocks for full thread safety.
         */
        entity.setVisible(false);
        Position pos = entity.getPosition();
        grid.putIfAbsent(pos, Collections.synchronizedList(new ArrayList<>()));
        List<GameEntity> list = grid.get(pos);
        synchronized (list) {
            list.add(entity);
        }
    }

    /**
     * Removes a game entity from its current position on the map.
     *
     * @param entity the game entity to remove
     */
    public void removeEntity(GameEntity entity) {
        Position pos = entity.getPosition();
        List<GameEntity> list = grid.get(pos);
        if (list != null) {
            synchronized (list) {
                list.remove(entity);
            }
        }
    }

    /**
     * Checks if a given position is within the grid bounds.
     *
     * @param pos the position to validate
     * @return true if the position is within the grid, false otherwise
     */
    public boolean isValidPosition(Position pos) {
        int row = pos.getRow();
        int col = pos.getCol();
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Retrieves a copy of all entities at a given position.
     *
     * @param pos the position to query
     * @return a list of entities at that tile (empty if none)
     */
    public List<GameEntity> getEntitiesAt(Position pos) {
        List<GameEntity> list = grid.get(pos);
        if (list == null) return new ArrayList<>();
        synchronized (list) {
            return new ArrayList<>(list);
        }
    }

    /**
     * Retrieves the topmost GameItem (if any) from a given position.
     *
     * @param pos the position to check
     * @return a GameItem if found, otherwise null
     */
    public GameItem getEntityGameItemAt(Position pos) {
        List<GameEntity> list = grid.get(pos);
        if (list != null) {
            synchronized (list) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    GameEntity entity = list.get(i);
                    if (entity instanceof GameItem) {
                        return (GameItem) entity;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if any entity is present at the given position.
     *
     * @param pos the position to check
     * @return true if occupied, false if empty
     */
    public boolean isOccupied(Position pos) {
        List<GameEntity> list = grid.get(pos);
        return list != null && !list.isEmpty();
    }

    /**
     * Checks whether a blocking GameItem is at the specified position.
     * A blocking item is one that prevents player movement.
     *
     * @param pos the position to check
     * @return true if a blocking GameItem is present, false otherwise
     */
    public boolean isGameItemBlocking(Position pos) {
        List<GameEntity> entities = getEntitiesAt(pos);
        if (entities == null)
            return false;
        for (GameEntity entity : entities)
            if (entity instanceof GameItem item && item.getBlockMovement())
                return true;
        return false;
    }

    /**
     * Checks whether a PlayerCharacter is present at the specified position.
     *
     * @param pos the position to check
     * @return true if a player is present, false otherwise
     */
    public boolean isPlayerBlocking(Position pos) {
        List<GameEntity> entities = getEntitiesAt(pos);
        if (entities == null)
            return false;
        for (GameEntity entity : entities)
            if (entity instanceof PlayerCharacter)
                return true;
        return false;
    }

    /**
     * Checks if an enemy is present at the given position.
     *
     * @param pos the position to check
     * @return true if an enemy is present, false otherwise
     */
    public boolean isEnemyBlocking(Position pos) {
        List<GameEntity> entities = getEntitiesAt(pos);
        if (entities == null)
            return false;
        for (GameEntity entity : entities)
            if (entity instanceof Enemy)
                return true;
        return false;
    }

    /**
     * Returns the number of rows in the map.
     *
     * @return the number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns in the map.
     *
     * @return the number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Creates and places a PlayerCharacter on the map based on the given type.
     *
     * @param name the name of the player
     * @param choice the character class: "Warrior", "Archer", or "Mage"
     */
    public void createCharacter(String name, String choice) {
        PlayerCharacter player;
        switch (choice) {
            case "Warrior" -> {
                player = new Warrior(name);
                placePlayerRandomly(player);
            }
            case "Archer" -> {
                player = new Archer(name);
                placePlayerRandomly(player);
            }
            case "Mage" -> {
                player = new Mage(name);
                placePlayerRandomly(player);
            }
            default -> {
                System.err.println("Choice for player isn't available!");
            }
        }
    }

    /**
     * Places a PlayerCharacter at a random unoccupied position.
     *
     * @param player the player to place
     */
    public void placePlayerRandomly(PlayerCharacter player) {
        Position pos;
        do {
            int row = RandomUtil.getRandomInt(getRows());
            int col = RandomUtil.getRandomInt(getCols());
            pos = new Position(row, col);
        }
        while (isOccupied(pos));

        System.out.println("Added " + player.getName() + " to place: " + pos);
        player.setPosition(pos);
        addEntity(player);
    }

    /**
     * Returns all GameEntities on the map in a flat list.
     *
     * @return a list of all entities on the grid
     */
    public List<GameEntity> getAllEntities() {
        List<GameEntity> all = new ArrayList<>();
        for (List<GameEntity> list : grid.values()) {
            synchronized (list) {
                all.addAll(list);
            }
        }
        return all;
    }

    /**
     * Populates the grid randomly with enemies, items, and walls.
     * Uses fixed probabilities for each type.
     */
    public void populateRandomEntities() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double roll = RandomUtil.getRandomDouble(); // Between 0.0 and 1.0
                Position pos = new Position(row, col);

                if (isOccupied(pos))
                    continue;
                if (roll < 0.10) {
                    // 10% → Wall
                    addEntity(new Wall(pos, "Wall"));
                    System.out.println("Placing wall at: " + pos);
                }
                else if (roll < 0.40) {
                    // Next 30% → Random enemy
                    addEntity(RandomUtil.randomEnemy(pos));
                    System.out.println("Placing Enemy at: " + pos);
                }
                else if (roll < 0.55) {
                    // Next 15% → Regular potion
                    addEntity(new Potion(pos, true, "Healing Potion, restores 10–50 HP. Use to recover health."));
                    System.out.println("Placing Potion at: " + pos);
                }
                else if (roll < 0.60) {
                    // Next 5% → Power potion
                    addEntity(new PowerPotion(pos, true, "Power Potion, grants 1-5 Power. boosts damage potential by increasing power."));
                    System.out.println("Placing Power Potion at: " + pos);
                }
                // Else: 40% chance to place nothing — do nothing
            }
        }
    }

    /**
     * Updates which entities are visible based on the player's position.
     * Uses Manhattan distance of 2 for visibility range.
     *
     * @param playerPos the player's current position
     */
    public void updatePlayerView(Position playerPos) {
        for (List<GameEntity> list : grid.values()) {
            synchronized (list) {
                for (GameEntity entity : list) {
                    entity.setVisible(false);
                }
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                if (playerPos.distanceTo(pos) <= 2) {
                    List<GameEntity> list = grid.get(pos);
                    if (list != null) {
                        synchronized (list) {
                            for (GameEntity entity : list) {
                                entity.setVisible(true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Attempts to move the given enemy one tile in the specified direction.
     * If the target position is valid and not blocked, moves the enemy there.
     *
     * @param enemy the enemy to move
     * @param newPos new enemy position if possible to move there
     * @return true if the move was successful, false otherwise
     */
    public boolean tryMoveEnemyRandomly(Enemy enemy, Position newPos) {
        Position currentPos = enemy.getPosition();

        if (!isValidPosition(newPos) || isGameItemBlocking(newPos) || isPlayerBlocking(newPos) || isEnemyBlocking(newPos)) {
            return false; // Can't move there
        }
        if(currentPos.equals(newPos)) {
            return true; // Worked but chosen to not move
        }

        // Update position
        removeEntity(enemy);
        enemy.setPosition(newPos);
        addEntity(enemy);
        return true;
    }

    public boolean moveEnemy(Enemy enemy, Position position) {
        removeEntity(enemy);
        enemy.setPosition(position);
        addEntity(enemy);
        return true;
    }


    /**
     * Retrieves all entities currently marked as visible.
     *
     * @return set of visible entities
     */
    public Set<GameEntity> getVisibleEntities() {
        Set<GameEntity> visible = new HashSet<>();
        for (List<GameEntity> list : grid.values()) {
            synchronized (list) {
                for (GameEntity entity : list) {
                    if (entity.isVisible()) {
                        visible.add(entity);
                    }
                }
            }
        }
        return visible;
    }

    // For debugging only
    /**
     * Prints the player's view of the map.
     * Visible entities are shown with their symbols; others as ⟨#⟩.
     *
     * @param playerPos the player's current position
     */
    public void printPlayerView(Position playerPos) {
        for (List<GameEntity> list : grid.values()) {
            synchronized (list) {
                for (GameEntity entity : list) {
                    entity.setVisible(false);
                }
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> list = grid.get(pos);

                if (playerPos.distanceTo(pos) <= 2) {
                    if (list != null) {
                        synchronized (list) {
                            for (GameEntity entity : list) {
                                entity.setVisible(true);
                            }
                        }
                    }
                    if (list != null && !list.isEmpty()) {
                        GameEntity top;
                        synchronized (list) {
                            top = list.getLast();
                        }
                        System.out.print(top.getDisplaySymbol());
                    } else {
                        System.out.print("⟨.⟩");
                    }
                } else {
                    System.out.print("⟨#⟩");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    // For debugging only
    /**
     * Prints all entities grouped by their tile coordinates.
     * Helpful for debugging what's on each tile.
     */
    public void printEntitiesPerTile() {
        System.out.println("\n--- Entities Per Tile ---");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> list = grid.get(pos);
                if (list != null && !list.isEmpty()) {
                    synchronized (list) {
                        System.out.print("[" + pos + "]: ");
                        for (GameEntity entity : list) {
                            System.out.print(entity.getClass().getSimpleName() + "(" + entity.getDisplaySymbol() + ": " + entity + ") ");
                        }
                        System.out.println();
                    }
                }
            }
        }
    }
}
