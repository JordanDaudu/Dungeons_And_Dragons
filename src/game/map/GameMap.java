package game.map;

import game.core.GameEntity;
import game.engine.RandomUtil;
import game.items.Potion;
import game.items.PowerPotion;
import game.items.Wall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {

    private Map<Position, List<GameEntity>> grid;
    private int rows;
    private int cols;
    private static GameMap instance = null;

    private GameMap() {
        grid = new HashMap<>();
    }

    public void init(int rows, int cols) {
        if (rows < 10 || cols < 10)
            throw new IllegalArgumentException("Grid size must be at least 10x10");

        this.rows = rows;
        this.cols = cols;
        grid.clear(); // clear existing grid if re-initialized

        populateRandomEntities();
    }

    public static GameMap getInstance() {
        if(instance == null)
            instance = new GameMap();
        return instance;
    }

    public void addEntity(GameEntity entity) {
        Position pos = entity.getPosition();
        grid.putIfAbsent(pos, new ArrayList<>());
        grid.get(pos).add(entity);
    }

    public boolean isOccupied(Position pos) {
        List<GameEntity> entities = grid.get(pos);
        return entities != null && !entities.isEmpty();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private void populateRandomEntities() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double roll = RandomUtil.getRandomDouble(); // Between 0.0 and 1.0
                Position pos = new Position(row, col);

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
                    addEntity(new Potion(pos, true, "Healing Potion"));
                    System.out.println("Placing Potion at: " + pos);
                }
                else if (roll < 0.60) {
                    // Next 5% → Power potion
                    addEntity(new PowerPotion(pos, true, "Power Potion"));
                    System.out.println("Placing Power Potion at: " + pos);
                }
                // Else: 40% chance to place nothing — do nothing
            }
        }
    }

    public void printDebugGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(row, col);
                List<GameEntity> entities = grid.get(pos);

                if (entities != null && !entities.isEmpty()) {
                    GameEntity top = entities.getLast();
                    System.out.print(top.getDisplaySymbol());
                } else {
                    System.out.print(".");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
