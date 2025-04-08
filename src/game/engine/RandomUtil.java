package game.engine;

import java.util.Random;

// Singleton Style
public class RandomUtil {

    private static final Random random = new Random();

    private RandomUtil() {}

    public static Random getRandomInstance() {
        return random;
    }

    public static int getRandomInt(int bound) {
        return random.nextInt(bound);
    }

    public static int getRandomInt(int origin, int bound) {
        return random.nextInt(origin, bound);
    }
}
