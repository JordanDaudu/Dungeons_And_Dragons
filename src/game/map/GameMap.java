package game.map;

import game.characters.*;
import game.core.GameEntity;
import game.engine.RandomUtil;
import game.items.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ConcurrentMap<Position, ReentrantLock> locks;
    private int rows;
    private int cols;
    private final static GameMap instance = new GameMap();
    private final ReentrantLock mapLock = new ReentrantLock(); // Universal lock used only when updating the player view

    // Methods
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the grid as an empty HashMap.
     */
    private GameMap() {
        grid = new ConcurrentHashMap<>();
        locks = new ConcurrentHashMap<>();
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

        mapLock.lock();
        try {
            this.rows = rows;
            this.cols = cols;
            grid.clear();
            locks.clear();
        }
        finally {
            mapLock.unlock();
        }
    }

    /**
     * Returns the singleton instance of the GameMap.
     * Creates it if it does not already exist.
     *
     * @return the single GameMap instance
     */
    public static GameMap getInstance() {
        return instance;
    }

    // Add a ReentrantLock for each position in the grid
    public ReentrantLock getLockForPosition(Position pos) {
        return locks.computeIfAbsent(pos, k -> new ReentrantLock());
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
        Position pos = entity.getPosition();
        ReentrantLock lock = getLockForPosition(pos);
        lock.lock(); // Lock the position
        try {
            entity.setVisible(false);
            grid.putIfAbsent(pos, Collections.synchronizedList(new ArrayList<>()));
            List<GameEntity> list = grid.get(pos);
            synchronized (list) {
                list.add(entity);
            }
        }
        finally {
            lock.unlock(); // Always unlock after operation
        }
    }

    /**
     * Removes a game entity from its current position on the map.
     *
     * @param entity the game entity to remove
     */
    public void removeEntity(GameEntity entity) {
        Position pos = entity.getPosition();
        ReentrantLock lock = getLockForPosition(pos);
        lock.lock(); // Lock the position
        try {
            List<GameEntity> list = grid.get(pos);
            if (list != null) {
                synchronized (list) {
                    list.remove(entity);
                }
            }
        }
        finally {
            lock.unlock(); // Always unlock after operation
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
            default -> System.err.println("Choice for player isn't available!");
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
        for (Map.Entry<Position, List<GameEntity>> entry : grid.entrySet()) {
            Position pos = entry.getKey();
            ReentrantLock lock = locks.get(pos);
            if (lock != null) {
                lock.lock();
                try {
                    all.addAll(entry.getValue());
                } finally {
                    lock.unlock();
                }
            }
        }
        return all;
    }

    public Set<Position> getAllPositions() {
        mapLock.lock();
        try {
            return new HashSet<>(grid.keySet()); // Snapshot copy to prevent external modification
        } finally {
            mapLock.unlock();
        }
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
        mapLock.lock();
        try {
            for (List<GameEntity> list : grid.values()) {
                for (GameEntity entity : list) {
                    entity.setVisible(false);
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
        finally {
            mapLock.unlock();
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
    public boolean tryMoveEnemy(Enemy enemy, Position newPos) {
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

    /**
     * Retrieves all entities currently marked as visible.
     *
     * @return set of visible entities
     */
    public Set<GameEntity> getVisibleEntities() {
        Set<GameEntity> visible = new HashSet<>();
        for (Map.Entry<Position, List<GameEntity>> entry : grid.entrySet()) {
            Position pos = entry.getKey();
            ReentrantLock lock = locks.get(pos);
            if (lock != null) {
                lock.lock();
                try {
                    for (GameEntity entity : entry.getValue()) {
                        if (entity.isVisible()) {
                            visible.add(entity);
                        }
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        }
        return visible;
    }


    public void applySandstorm(Position delta) {

        int startRow = 0, endRow = rows, stepRow = 1;
        int startCol = 0, endCol = cols, stepCol = 1;

        // Adjust iteration order based on delta direction
        if (delta.getRow() > 0) {
            startRow = rows - 1;
            endRow = -1;
            stepRow = -1;
        } else if (delta.getRow() < 0) {
            startRow = 0;
            endRow = rows;
            stepRow = 1;
        }

        if (delta.getCol() > 0) {
            startCol = cols - 1;
            endCol = -1;
            stepCol = -1;
        } else if (delta.getCol() < 0) {
            startCol = 0;
            endCol = cols;
            stepCol = 1;
        }

        mapLock.lock();
        try {
            for (int row = startRow; row != endRow; row += stepRow) {
                for (int col = startCol; col != endCol; col += stepCol) {
                    Position current = new Position(row, col);
                    List<GameEntity> entities = getEntitiesAt(current);

                    if (entities.isEmpty()) continue;

                    List<GameEntity> moved = new ArrayList<>();

                    for (GameEntity entity : entities) {
                        if (entity instanceof Wall) continue;

                        Position target = new Position(row + delta.getRow(), col + delta.getCol());
                        if (isValidPosition(target) &&
                                !isGameItemBlocking(target) &&
                                !isEnemyBlocking(target) &&
                                !isPlayerBlocking(target)) {

                            removeEntity(entity);
                            entity.setPosition(target);
                            moved.add(entity);
                        }
                    }
                    for (GameEntity e : moved) {
                        addEntity(e);
                    }
                }
            }
        }
        finally {
            mapLock.unlock();
        }
    }


    // For debugging only
    /**
     * Prints the player's view of the map.
     * Visible entities are shown with their symbols; others as ⟨#⟩.
     *
     * @param playerPos the player's current position
     */
    public void printPlayerView(Position playerPos) {
        // First, set all entities as not visible (iterate all lists)
        for (Map.Entry<Position, List<GameEntity>> entry : grid.entrySet()) {
            Position pos = entry.getKey();
            List<GameEntity> list = entry.getValue();
            ReentrantLock lock = locks.get(pos);
            if (lock != null) {
                lock.lock();
                try {
                    for (GameEntity entity : list) {
                        entity.setVisible(false);
                    }
                }
                finally {
                    lock.unlock();
                }
            }
        }
        // Then, for each position in view, set visible and print symbol
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                if (playerPos.distanceTo(pos) <= 2) {
                    List<GameEntity> list = grid.get(pos);
                    ReentrantLock lock = locks.get(pos);
                    if (list != null && lock != null) {
                        lock.lock();
                        try {
                            for (GameEntity entity : list) {
                                entity.setVisible(true);
                            }
                            if (!list.isEmpty()) {
                                GameEntity top = list.getLast(); // getLast equivalent
                                System.out.print(top.getDisplaySymbol());
                            }
                            else {
                                System.out.print("⟨.⟩");
                            }
                        }
                        finally {
                            lock.unlock();
                        }
                    }
                    else {
                        System.out.print("⟨.⟩");
                    }
                }
                else {
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
