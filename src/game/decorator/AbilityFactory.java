package game.decorator;

import game.characters.PlayerCharacter;
import game.characters.Enemy;
import game.engine.RandomUtil;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

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

    // Apply an ability decorator to a PlayerCharacter
    public static PlayerCharacter applyPlayerAbility(String abilityName, PlayerCharacter basePlayer) {
        Function<PlayerCharacter, PlayerDecorator> decoratorFunction = PLAYER_ABILITY_MAP.get(abilityName);

        if (decoratorFunction == null) {
            throw new IllegalArgumentException("Unknown ability: " + abilityName);
        }

        return decoratorFunction.apply(basePlayer); // Return decorated PlayerCharacter
    }

    // Apply an ability decorator to an Enemy
    public static Enemy applyEnemyAbility(String abilityName, Enemy baseEnemy) {
        Function<Enemy, EnemyDecorator> decoratorFunction = ENEMY_ABILITY_MAP.get(abilityName);

        if (decoratorFunction == null) {
            throw new IllegalArgumentException("Unknown ability: " + abilityName);
        }

        return decoratorFunction.apply(baseEnemy); // Return decorated Enemy
    }

    // Get a random enemy ability to apply
    public static Function<Enemy, EnemyDecorator> getRandomEnemyAbilityConstructor() {
        var abilities = ENEMY_ABILITY_MAP.values().toArray(new Function[0]);

        if (abilities.length == 0) {
            throw new IllegalStateException("No enemy abilities available");
        }

        return abilities[RandomUtil.getRandomInt(abilities.length)];
    }
}
