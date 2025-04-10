package game.engine;

import game.characters.Dragon;
import game.characters.Enemy;
import game.characters.Goblin;
import game.characters.Orc;
import game.combat.MagicElement;
import game.map.Position;

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

    public static double getRandomDouble() {return random.nextDouble();}

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
}
