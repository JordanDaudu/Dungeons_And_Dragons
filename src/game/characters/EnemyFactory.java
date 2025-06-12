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
 * Factory class responsible for creating Enemy instances.
 */
public class EnemyFactory {

    // Map of player type names to builder suppliers
    private static final Map<String, Supplier<? extends EnemyFactory.EnemyBuilder<?>>> builders = new HashMap<>();

    static {
        builders.put("Dragon", DragonBuilder::new);
        builders.put("Goblin", GoblinBuilder::new);
        builders.put("Orc", OrcBuilder::new);
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnemyFactory.EnemyBuilder<?>> T getBuilder(String type) {
        Supplier<? extends EnemyFactory.EnemyBuilder<?>> supplier = builders.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown player type: " + type);
        }
        return (T) supplier.get();
    }

    // Abstract Base Builder Class
    public static abstract class EnemyBuilder<T extends Enemy> {
        private int health;
        private int power;
        private int loot;

        public EnemyFactory.EnemyBuilder<T> setLoot(int loot) {
            this.loot = loot;
            return this;
        }

        public EnemyFactory.EnemyBuilder<T> setHealth(int health) {
            this.health = health;
            return this;
        }

        public EnemyFactory.EnemyBuilder<T> setPower(int power) {
            this.power = power;
            return this;
        }

        public abstract T buildBase(); // Separate base enemy creation

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

    // Goblin Builder
    public static class GoblinBuilder extends EnemyFactory.EnemyBuilder<Goblin> {
        private int agility;

        public EnemyFactory.GoblinBuilder setAgility(int agility) { this.agility = agility; return this; }

        @Override
        public Goblin buildBase() {
            return new Goblin(super.health, super.power, super.loot, agility);
        }
    }

    // Orc Builder
    public static class OrcBuilder extends EnemyFactory.EnemyBuilder<Orc> {
        private double resistance;

        public EnemyFactory.OrcBuilder setResistance(double resistance) { this.resistance = resistance; return this; }

        @Override
        public Orc buildBase() {
            return new Orc(super.health, super.power, super.loot, resistance);
        }
    }

    // Dragon Builder
    public static class DragonBuilder extends EnemyFactory.EnemyBuilder<Dragon> {
        private MagicElement magicElement;

        public EnemyFactory.DragonBuilder setMagicElement(MagicElement magicElement) { this.magicElement = magicElement; return this; }

        @Override
        public Dragon buildBase() {
            return new Dragon(super.health, super.power, super.loot, magicElement);
        }
    }
}

