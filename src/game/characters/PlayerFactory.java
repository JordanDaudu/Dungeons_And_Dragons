package game.characters;

import game.combat.MagicElement;
import game.decorator.AbilityFactory;
import game.decorator.PlayerDecorator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating different types of PlayerCharacter builders.
 * Supports Warrior, Archer, and Mage player types, each configurable through a builder.
 */
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
        return (T) supplier.get();
    }

    /**
     * Abstract builder class for building PlayerCharacter instances.
     *
     * @param <T> the specific subclass of PlayerCharacter
     */
    public static abstract class PlayerBuilder<T extends PlayerCharacter> {

        // Data Members
        private String name;
        private int health;
        private int power;
        private final List<String> abilities = new ArrayList<>();

        //Methods
        /**
         * Sets the character's name.
         *
         * @param name the name to set
         * @return this builder instance
         */
        public PlayerBuilder<T> setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the character's health.
         *
         * @param health the health value
         * @return this builder instance
         */
        public PlayerBuilder<T> setHealth(int health) {
            this.health = health;
            return this;
        }

        /**
         * Sets the character's power.
         *
         * @param power the power value
         * @return this builder instance
         */
        public PlayerBuilder<T> setPower(int power) {
            this.power = power;
            return this;
        }

        /**
         * Adds an ability to the character.
         *
         * @param ability the ability name
         * @return this builder instance
         */
        public PlayerBuilder<T> addAbility(String ability) {
            abilities.add(ability);
            return this;
        }

        /**
         * Applies all added abilities to the given player character using decorators.
         * Immediately triggers useAbility on each decorated player.
         *
         * @param player the base player character
         * @return the decorated player character
         */
        protected PlayerCharacter applyAbilities(PlayerCharacter player) {
            for (String ability : abilities) {
                player = AbilityFactory.applyPlayerAbility(ability, player); // Apply decorator
                if (player instanceof PlayerDecorator) {
                    player.useAbility(); // Call useAbility() right after adding decoration
                }
            }
            return player; // Return decorated player
        }

        /**
         * Builds and returns the fully configured PlayerCharacter.
         *
         * @return the built PlayerCharacter
         */
        public abstract PlayerCharacter build();
    }

    /**
     * Builder for creating Warrior characters.
     */
    public static class WarriorBuilder extends PlayerBuilder<Warrior> {

        // Data Members
        private int defence;

        // Methods
        /**
         * Sets the Warrior's defence value.
         *
         * @param defence the defence value
         * @return this builder instance
         */
        public WarriorBuilder setDefence(int defence) { this.defence = defence; return this; }

        /**
         * Builds and returns the Warrior character with applied abilities.
         *
         * @return the constructed Warrior character
         */
        @Override
        public PlayerCharacter build() {
            PlayerCharacter warrior = new Warrior(super.name, super.health, super.power, defence);
            return applyAbilities(warrior);
        }
    }

    /**
     * Builder for creating Archer characters.
     */
    public static class ArcherBuilder extends PlayerBuilder<Archer> {

        // Data Members
        private double accuracy;

        // Methods
        /**
         * Sets the Archer's accuracy value.
         *
         * @param accuracy the accuracy value
         * @return this builder instance
         */
        public ArcherBuilder setAccuracy(double accuracy) { this.accuracy = accuracy; return this; }

        /**
         * Builds and returns the Archer character with applied abilities.
         *
         * @return the constructed Archer character
         */
        @Override
        public PlayerCharacter build() {
            PlayerCharacter archer = new Archer(super.name, super.health, super.power, accuracy);
            return applyAbilities(archer);
        }
    }

    /**
     * Builder for creating Mage characters.
     */
    public static class MageBuilder extends PlayerBuilder<Mage> {

        // Data Members
        private MagicElement magicElement;

        // Methods
        /**
         * Sets the Mage's magic element.
         *
         * @param magicElement the magic element to assign
         * @return this builder instance
         */
        public MageBuilder setMagicElement(MagicElement magicElement) { this.magicElement = magicElement; return this; }

        /**
         * Builds and returns the Mage character with applied abilities.
         *
         * @return the constructed Mage character
         */
        @Override
        public PlayerCharacter build() {
            PlayerCharacter mage = new Mage(super.name, super.health, super.power, magicElement);
            return applyAbilities(mage);
        }
    }
}
