package game.characters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory class responsible for creating Enemy instances.
 */
public class EnemyFactory {

    private static final Map<String, Supplier<Enemy>> ENEMY_CREATORS = new HashMap<>();

    static {
        ENEMY_CREATORS.put("Dragon", Dragon::new);
        ENEMY_CREATORS.put("Goblin", Goblin::new);
        ENEMY_CREATORS.put("Orc", Orc::new);
    }

    /**
     * Creates an Enemy instance based on the given type name.
     *
     * @param type the name of the enemy type (e.g., "Dragon", "Goblin", "Orc")
     * @return a new Enemy instance, or null if the type is unknown
     */
    public static Enemy createEnemy(String type) {
        Supplier<Enemy> creator = ENEMY_CREATORS.get(type);
        if (creator != null) {
            return creator.get();
        }
        throw new IllegalArgumentException("Unknown enemy type: " + type);
    }
}

