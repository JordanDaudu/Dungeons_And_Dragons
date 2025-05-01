package game.map;

import game.characters.*;
import game.core.GameEntity;
import game.engine.RandomUtil;
import game.items.*;

import java.util.*;

public class GameMap {

    // Important! GameMap is a singleton (One object exists only)

    // Data Members
    private final Map<Position, List<GameEntity>> grid;
    private int rows;
    private int cols;
    private static GameMap instance = null;

    // Methods
    private GameMap() {
        grid = new HashMap<>();
    }

    public void init(int rows, int cols) {
        if (rows < 10 || cols < 10)
            throw new IllegalArgumentException("Grid size must be at least 10x10");

        this.rows = rows;
        this.cols = cols;
        grid.clear(); // clear existing grid if re-initialized
    }

    public static GameMap getInstance() {
        if(instance == null)
            instance = new GameMap();
        return instance;
    }

    public void addEntity(GameEntity entity) {
        entity.setVisible(false);
        Position pos = entity.getPosition();
        grid.putIfAbsent(pos, new ArrayList<>());
        grid.get(pos).add(entity);
    }

    public void removeEntity(GameEntity entity) {
        Position pos = entity.getPosition();
        List<GameEntity> list = grid.get(pos);
        if (list != null) {
            list.remove(entity);
        }
    }

    public boolean isValidPosition(Position pos) {
        int row = pos.getRow();
        int col = pos.getCol();
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public List<GameEntity> getEntitiesAt(Position pos) {
        return grid.getOrDefault(pos, new ArrayList<>());
    }

    public GameItem getEntityGameItemAt(Position pos) {
        List<GameEntity> entities = grid.get(pos);
        if (entities != null && !entities.isEmpty()) {
            for (int i = entities.size() - 1; i >= 0; i--) {
                GameEntity entity = entities.get(i);
                if (entity instanceof GameItem) {
                    return (GameItem) entity;
                }
            }
        }
        return null;
    }

    public boolean isOccupied(Position pos) {
        List<GameEntity> entities = grid.get(pos);
        return entities != null && !entities.isEmpty();
    }

    public boolean isGameItemBlocking(Position pos) {
        List<GameEntity> entities = getEntitiesAt(pos);
        if (entities == null)
            return false;
        for (GameEntity entity : entities)
            if (entity instanceof GameItem item && item.getBlockMovement())
                return true;
        return false;
    }

    public boolean isEnemyBlocking(Position pos) {
        List<GameEntity> entities = getEntitiesAt(pos);
        if (entities == null)
            return false;
        for (GameEntity entity : entities)
            if (entity instanceof Enemy)
                return true;
        return false;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public PlayerCharacter createCharacter(String name, String choice) {
        PlayerCharacter player;
        switch (choice) {
            case "Warrior" -> {
                player = new Warrior(name);
                placePlayerRandomly(player);
                return player;
            }
            case "Archer" -> {
                player = new Archer(name);
                placePlayerRandomly(player);
                return player;
            }
            case "Mage" -> {
                player = new Mage(name);
                placePlayerRandomly(player);
                return player;
            }
            default -> {
                System.err.println("Choice for player isn't available!");
                return null;
            }
        }
    }

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

    public List<GameEntity> getAllEntities() {
        List<GameEntity> all = new ArrayList<>();
        for (List<GameEntity> list : grid.values()) {
            all.addAll(list);
        }
        return all;
    }

    public void populateRandomEntities() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double roll = RandomUtil.getRandomDouble(); // Between 0.0 and 1.0
                Position pos = new Position(row, col);

                // Skip if something is already placed there
                if (isOccupied(pos)) {
                    continue;
                }

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

    public void updatePlayerView(Position playerPos) {
        // Reset visibility for all entities
        for (List<GameEntity> entities : grid.values()) {
            for (GameEntity entity : entities) {
                entity.setVisible(false);
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> entities = grid.get(pos);

                // Check visibility range
                if (playerPos.distanceTo(pos) <= 2) {
                    // Within view range → reveal
                    if (entities != null) {
                        for (GameEntity entity : entities) {
                            entity.setVisible(true);
                        }
                    }
                }
            }
        }
    }

    public Set<GameEntity> getVisibleEntities() {
        Set<GameEntity> visibleEntities = new HashSet<>();
        for (List<GameEntity> entities : grid.values()) {
            for (GameEntity entity : entities) {
                if (entity.isVisible()) {
                    visibleEntities.add(entity);
                }
            }
        }
        return visibleEntities;
    }


    public void printPlayerView(Position playerPos) {
        // Reset visibility for all entities
        for (List<GameEntity> entities : grid.values()) {
            for (GameEntity entity : entities) {
                entity.setVisible(false);
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> entities = grid.get(pos);

                // Check visibility range
                if (playerPos.distanceTo(pos) <= 2) {
                    // Within view range → reveal and print
                    if (entities != null) {
                        for (GameEntity entity : entities) {
                            entity.setVisible(true);
                        }
                    }
                    // getting top item in list to show
                    if (entities != null && !entities.isEmpty()) {
                        GameEntity top = entities.getLast();
                        System.out.print(top.getDisplaySymbol());
                    } else {
                        System.out.print("⟨.⟩");
                    }
                } else {
                    // Outside visible range
                    System.out.print("⟨#⟩");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void printEntitiesPerTile() {
        System.out.println("\n--- Entities Per Tile ---");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> entities = grid.get(pos);

                if (entities != null && !entities.isEmpty()) {
                    System.out.print("[" + pos + "]: ");
                    for (GameEntity entity : entities) {
                        System.out.print(entity.getClass().getSimpleName() + "(" + entity.getDisplaySymbol() + ": " + entity + ") ");
                    }
                    System.out.println();
                }
            }
        }
    }
}
