package game.characters;

import game.combat.MagicElement;
import game.decorator.AbilityFactory;
import game.decorator.PlayerDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PlayerFactory {

    // Map of player type names to builder suppliers
    private static final Map<String, Supplier<? extends PlayerBuilder<?>>> builders = new HashMap<>();

    static {
        builders.put("Warrior", WarriorBuilder::new);
        builders.put("Archer", ArcherBuilder::new);
        builders.put("Mage", MageBuilder::new);
    }

    /**
     * Factory method returning a builder of the specified type.
     *
     * @param type the player type string ("Warrior", "Archer", "Mage")
     * @return a builder instance of type B
     */
    @SuppressWarnings("unchecked")
    public static <T extends PlayerBuilder<?>> T getBuilder(String type) {
        Supplier<? extends PlayerBuilder<?>> supplier = builders.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown player type: " + type);
        }
        return (T) supplier.get(); // **Safe casting ensures correct return type**
    }

    // Abstract Base Builder Class
    public static abstract class PlayerBuilder<T extends PlayerCharacter> {
        private String name;
        private int health;
        private int power;
        private final List<String> abilities = new ArrayList<>();

        public PlayerBuilder<T> setName(String name) {
            this.name = name;
            return this;
        }

        public PlayerBuilder<T> setHealth(int health) {
            this.health = health;
            return this;
        }

        public PlayerBuilder<T> setPower(int power) {
            this.power = power;
            return this;
        }

        public PlayerBuilder<T> addAbility(String ability) {
            abilities.add(ability);
            return this;
        }

        protected PlayerCharacter applyAbilities(PlayerCharacter player) {
            for (String ability : abilities) {
                player = AbilityFactory.applyPlayerAbility(ability, player); // Apply decorator
                if (player instanceof PlayerDecorator) {
                    ((PlayerDecorator) player).useAbility(); // Call useAbility() right after adding decoration
                }
            }
            return player; // Return decorated player
        }

        public abstract PlayerCharacter build();
    }

    // Warrior Builder
    public static class WarriorBuilder extends PlayerBuilder<Warrior> {
        private int defence;

        public WarriorBuilder setDefence(int defence) { this.defence = defence; return this; }

        @Override
        public PlayerCharacter build() {
            PlayerCharacter warrior = new Warrior(super.name, super.health, super.power, defence);
            return applyAbilities(warrior); // No explicit casting
        }
    }

    // Archer Builder
    public static class ArcherBuilder extends PlayerBuilder<Archer> {
        private double accuracy;

        public ArcherBuilder setAccuracy(double accuracy) { this.accuracy = accuracy; return this; }

        @Override
        public PlayerCharacter build() {
            PlayerCharacter archer = new Archer(super.name, super.health, super.power, accuracy);
            return applyAbilities(archer);
        }
    }

    // Mage Builder
    public static class MageBuilder extends PlayerBuilder<Mage> {
        private MagicElement magicElement;

        public MageBuilder setMagicElement(MagicElement magicElement) { this.magicElement = magicElement; return this; }

        @Override
        public PlayerCharacter build() {
            PlayerCharacter mage = new Mage(super.name, super.health, super.power, magicElement);
            return applyAbilities(mage);
        }
    }
}
