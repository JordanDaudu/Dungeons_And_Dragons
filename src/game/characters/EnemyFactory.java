package game.characters;

import game.combat.MagicElement;
import game.decorator.AbilityFactory;
import game.decorator.EnemyDecorator;
import game.logging.GameLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Factory class responsible for creating {@link Enemy} instances using the Builder pattern.
 * Supports dynamic creation of different enemy types such as Goblin, Orc, and Dragon.
 * Also applies random decorators via {@link AbilityFactory} to enhance base enemies.
 */
public class EnemyFactory {

    // Map of player type names to builder suppliers
    private static final Map<String, Supplier<? extends EnemyFactory.EnemyBuilder<?>>> builders = new HashMap<>();

    static {
        builders.put("Dragon", DragonBuilder::new);
        builders.put("Goblin", GoblinBuilder::new);
        builders.put("Orc", OrcBuilder::new);
    }

    /**
     * Returns a builder for the given enemy type.
     *
     * @param type the name of the enemy type (e.g., "Goblin", "Orc", "Dragon")
     * @param <T> the specific builder type extending {@link EnemyBuilder}
     * @return a new instance of the corresponding builder
     * @throws IllegalArgumentException if the type is not registered
     */
    @SuppressWarnings("unchecked")
    public static <T extends EnemyFactory.EnemyBuilder<?>> T getBuilder(String type) {
        Supplier<? extends EnemyFactory.EnemyBuilder<?>> supplier = builders.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown player type: " + type);
        }
        return (T) supplier.get();
    }

    /**
     * Abstract base builder class used to construct {@link Enemy} objects.
     * Subclasses define specific enemy creation logic.
     *
     * @param <T> the concrete type of {@link Enemy}
     */
    public static abstract class EnemyBuilder<T extends Enemy> {

        // Data Members
        private int health;
        private int power;
        private int loot;

        // Methods
        /**
         * Sets the amount of loot the enemy will drop upon defeat.
         *
         * @param loot the loot value
         * @return this builder instance
         */
        public EnemyFactory.EnemyBuilder<T> setLoot(int loot) {
            this.loot = loot;
            return this;
        }

        /**
         * Sets the health of the enemy.
         *
         * @param health the health value
         * @return this builder instance
         */
        public EnemyFactory.EnemyBuilder<T> setHealth(int health) {
            this.health = health;
            return this;
        }

        /**
         * Sets the power (attack strength) of the enemy.
         *
         * @param power the power value
         * @return this builder instance
         */
        public EnemyFactory.EnemyBuilder<T> setPower(int power) {
            this.power = power;
            return this;
        }

        /**
         * Constructs the base enemy instance (without decorators).
         *
         * @return a new instance of the specific enemy type
         */
        public abstract T buildBase(); // Separate base enemy creation

        /**
         * Constructs the final {@link Enemy} instance, possibly with a random decorator
         * applied from {@link AbilityFactory}. Logs the result to {@link GameLogger}.
         *
         * @return a decorated or undecorated enemy instance
         */
        public Enemy build() {
            T baseEnemy = buildBase();

            Function<Enemy, EnemyDecorator> decoratorConstructor =
                    AbilityFactory.getRandomEnemyAbilityConstructor();

            if (decoratorConstructor != null) {
                EnemyDecorator decoratedEnemy = decoratorConstructor.apply(baseEnemy);
                GameLogger.getInstance().log("Applying decorator: " + decoratedEnemy.getClass().getSimpleName() + " on " + baseEnemy.getClass().getSimpleName());
                return decoratedEnemy;
            }
            else {
                GameLogger.getInstance().log("No decorator applied, returning base enemy: " + baseEnemy.getClass().getSimpleName());
                return baseEnemy;
            }
        }
    }

    /**
     * Builder class for constructing {@link Goblin} enemies.
     */
    public static class GoblinBuilder extends EnemyFactory.EnemyBuilder<Goblin> {

        // Data Members
        private int agility;

        // Methods
        /**
         * Sets the agility stat for the goblin.
         *
         * @param agility the agility value
         * @return this builder instance
         */
        public EnemyFactory.GoblinBuilder setAgility(int agility) { this.agility = agility; return this; }

        /**
         * Builds a new {@link Goblin} instance with the configured attributes.
         *
         * @return the constructed Goblin
         */
        @Override
        public Goblin buildBase() {
            return new Goblin(super.health, super.power, super.loot, agility);
        }
    }

    /**
     * Builder class for constructing {@link Orc} enemies.
     */
    public static class OrcBuilder extends EnemyFactory.EnemyBuilder<Orc> {

        // Data Members
        private double resistance;

        // Methods
        /**
         * Sets the resistance stat for the orc.
         *
         * @param resistance the resistance value
         * @return this builder instance
         */
        public EnemyFactory.OrcBuilder setResistance(double resistance) { this.resistance = resistance; return this; }

        /**
         * Builds a new {@link Orc} instance with the configured attributes.
         *
         * @return the constructed Orc
         */
        @Override
        public Orc buildBase() {
            return new Orc(super.health, super.power, super.loot, resistance);
        }
    }

    /**
     * Builder class for constructing {@link Dragon} enemies.
     */
    public static class DragonBuilder extends EnemyFactory.EnemyBuilder<Dragon> {

        // Data Members
        private MagicElement magicElement;

        // Methods
        /**
         * Sets the {@link MagicElement} type for the dragon.
         *
         * @param magicElement the element to assign
         * @return this builder instance
         */
        public EnemyFactory.DragonBuilder setMagicElement(MagicElement magicElement) { this.magicElement = magicElement; return this; }

        /**
         * Builds a new {@link Dragon} instance with the configured attributes.
         *
         * @return the constructed Dragon
         */
        @Override
        public Dragon buildBase() {
            return new Dragon(super.health, super.power, super.loot, magicElement);
        }
    }
}

