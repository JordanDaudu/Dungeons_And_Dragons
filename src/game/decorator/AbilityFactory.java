package game.decorator;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

public class AbilityFactory {
    private static final Map<String, Function<AbstractCharacter, CharacterDecorator>> ABILITY_MAP = new HashMap<>();

    static {
        ABILITY_MAP.put("BoostedAttackDecorator", BoostedAttackDecorator::new);
        ABILITY_MAP.put("MagicAmplifierDecorator", MagicAmplifierDecorator::new);
        ABILITY_MAP.put("RegenerationDecorator", RegenerationDecorator::new);
        // Add more mappings here
    }

    public static CharacterDecorator create(String name, AbstractCharacter base) {
        Function<AbstractCharacter, CharacterDecorator> constructor = ABILITY_MAP.get(name);
        if (constructor == null) {
            throw new IllegalArgumentException("Unknown ability: " + name);
        }
        return constructor.apply(base);
    }
}
