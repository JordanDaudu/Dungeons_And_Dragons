package game.engine;

import game.characters.Dragon;
import game.characters.Enemy;
import game.characters.Goblin;
import game.characters.Orc;
import game.map.Position;

import java.util.Random;

// Singleton Style
/**
 * Utility class for generating random numbers and randomized game entities.
 * This class follows the Singleton style with a single shared {@link Random} instance.
 */
public class RandomUtil {

    // Data Members
    private static final Random random = new Random();

    // Methods

    // Default Constructor
    private RandomUtil() {}

    /**
     * Returns a random integer between 0 (inclusive) and the specified bound (exclusive).
     *
     * @param bound the upper bound (exclusive). Must be positive.
     * @return a random integer between 0 (inclusive) and {@code bound} (exclusive).
     */
    public static int getRandomInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Returns a random integer between the specified origin (inclusive) and bound (exclusive).
     *
     * @param origin the lower bound (inclusive).
     * @param bound the upper bound (exclusive).
     * @return a random integer between {@code origin} (inclusive) and {@code bound} (exclusive).
     */
    public static int getRandomInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }

    /**
     * Returns a random double between 0.0 (inclusive) and 1.0 (exclusive).
     *
     * @return a random double between 0.0 and 1.0.
     */
    public static double getRandomDouble() {return random.nextDouble();}

    /**
     * Generates a random enemy (Goblin, Orc, or Dragon) and assigns it the given position.
     *
     * @param pos the position to assign to the generated enemy.
     * @return a randomly chosen enemy instance with its position set.
     */
    public static Enemy randomEnemy(Position pos) {
        int roll = getRandomInt(3);
        Enemy enemy;
        switch(roll) {
            case 0 -> {
                enemy = new Goblin();
                enemy.setPosition(pos);
                return enemy;
            }
            case 1 -> {
                enemy = new Orc();
                enemy.setPosition(pos);
                return enemy;
            }
            default -> {
                enemy = new Dragon();
                enemy.setPosition(pos);
                return enemy;
            }
        }
    }

    /**
     * Returns a new position one tile away from the given position in a random cardinal direction.
     *
     * @param current the current position
     * @return a new position one step up, down, left, or right
     */
    public static Position getRandomAdjacentPosition(Position current) {
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        int[] dir = directions[getRandomInt(4)];
        return new Position(current.getRow() + dir[0], current.getCol() + dir[1]);
    }

}
