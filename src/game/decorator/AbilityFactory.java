package game.decorator;

import game.characters.PlayerCharacter;
import game.characters.Enemy;
import game.engine.RandomUtil;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for applying and retrieving ability decorators for {@link PlayerCharacter}
 * and {@link Enemy} instances.
 * <p>
 * This factory uses the Decorator pattern to dynamically enhance entities with
 * additional behaviors such as boosted attack, regeneration, teleportation, etc.
 */
public class AbilityFactory {
    // Player Ability Map - Using PlayerDecorator instead of PlayerCharacter
    private static final Map<String, Function<PlayerCharacter, PlayerDecorator>> PLAYER_ABILITY_MAP = new HashMap<>();

    static {
        PLAYER_ABILITY_MAP.put("BoostedAttackPlayerDecorator", BoostedAttackPlayerDecorator::new);
        PLAYER_ABILITY_MAP.put("RegenerationPlayerDecorator", RegenerationPlayerDecorator::new);
        PLAYER_ABILITY_MAP.put("ShieldedPlayerDecorator", ShieldedPlayerDecorator::new);
    }

    // Enemy Ability Map - Using EnemyDecorator instead of Enemy
    private static final Map<String, Function<Enemy, EnemyDecorator>> ENEMY_ABILITY_MAP = new HashMap<>();

    static {
        ENEMY_ABILITY_MAP.put("EnragedEnemyDecorator", EnragedEnemyDecorator::new);
        ENEMY_ABILITY_MAP.put("ExplodingEnemyDecorator", ExplodingEnemyDecorator::new);
        ENEMY_ABILITY_MAP.put("TeleportingEnemyDecorator", TeleportingEnemyDecorator::new);
    }

    /**
     * Applies the specified ability to the given {@link PlayerCharacter}.
     *
     * @param abilityName the name of the ability (e.g., "BoostedAttackPlayerDecorator")
     * @param basePlayer the base player to decorate
     * @return a decorated {@code PlayerCharacter} with the ability applied
     * @throws IllegalArgumentException if the ability name is unknown
     */
    public static PlayerCharacter applyPlayerAbility(String abilityName, PlayerCharacter basePlayer) {
        Function<PlayerCharacter, PlayerDecorator> decoratorFunction = PLAYER_ABILITY_MAP.get(abilityName);

        if (decoratorFunction == null) {
            throw new IllegalArgumentException("Unknown ability: " + abilityName);
        }

        return decoratorFunction.apply(basePlayer); // Return decorated PlayerCharacter
    }

    /**
     * Applies the specified ability to the given {@link Enemy}.
     *
     * @param abilityName the name of the ability (e.g., "ExplodingEnemyDecorator")
     * @param baseEnemy the base enemy to decorate
     * @return a decorated {@code Enemy} with the ability applied
     * @throws IllegalArgumentException if the ability name is unknown
     */
    public static Enemy applyEnemyAbility(String abilityName, Enemy baseEnemy) {
        Function<Enemy, EnemyDecorator> decoratorFunction = ENEMY_ABILITY_MAP.get(abilityName);

        if (decoratorFunction == null) {
            throw new IllegalArgumentException("Unknown ability: " + abilityName);
        }

        return decoratorFunction.apply(baseEnemy); // Return decorated Enemy
    }

    /**
     * Retrieves a random enemy ability constructor function.
     * <p>
     * This can be used to assign a random decorator to an enemy during spawning or mutation.
     *
     * @return a function that decorates an {@code Enemy} with a random ability
     * @throws IllegalStateException if no enemy abilities are registered
     */
    public static Function<Enemy, EnemyDecorator> getRandomEnemyAbilityConstructor() {
        var abilities = ENEMY_ABILITY_MAP.values().toArray(new Function[0]);

        if (abilities.length == 0) {
            throw new IllegalStateException("No enemy abilities available");
        }

        return abilities[RandomUtil.getRandomInt(abilities.length)];
    }
}
