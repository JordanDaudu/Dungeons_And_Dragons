package game.decorator;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;

import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class responsible for creating ability decorators based on their string names.
 * Uses a registry pattern to associate ability names with their corresponding constructors.
 */
public class AbilityFactory {

    // Data Members
    private static final Map<String, Function<AbstractCharacter, CharacterDecorator>> ABILITY_MAP = new HashMap<>();

    // Static block to initialize the ability map with known abilities
    static {
        ABILITY_MAP.put("BoostedAttackDecorator", BoostedAttackDecorator::new);
        ABILITY_MAP.put("MagicAmplifierDecorator", MagicAmplifierDecorator::new);
        ABILITY_MAP.put("RegenerationDecorator", RegenerationDecorator::new);
    }

    // Methods
    /**
     * Creates a character decorator based on the ability name and base character.
     *
     * @param name the name of the ability/decorator to create
     * @param base the base character to decorate
     * @return a new instance of the corresponding {@link CharacterDecorator}
     * @throws IllegalArgumentException if the ability name is not recognized
     */
    public static CharacterDecorator create(String name, AbstractCharacter base) {
        Function<AbstractCharacter, CharacterDecorator> constructor = ABILITY_MAP.get(name);
        if (constructor == null) {
            throw new IllegalArgumentException("Unknown ability: " + name);
        }
        return constructor.apply(base);
    }
}
