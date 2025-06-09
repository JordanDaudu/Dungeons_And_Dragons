package game.characters;

import game.combat.MagicElement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory class to construct different types of PlayerCharacters using builder classes.
 * Provides a generic interface to create Warriors, Archers, and Mages with configurable attributes.
 */
public class PlayerFactory {

    // Map of player type names to builder suppliers
    private static final Map<String, Supplier<? extends PlayerBuilder<?>>> builders = new HashMap<>();

    static {
        builders.put("Warrior", WarriorBuilder::new);
        builders.put("Archer", ArcherBuilder::new);
        builders.put("Mage", MageBuilder::new);
    }

    // Methods
    /**
     * Returns a builder instance corresponding to the specified player type.
     *
     * @param type the player type string ("Warrior", "Archer", or "Mage")
     * @param <T>  the specific builder type
     * @return a builder of the requested type
     * @throws IllegalArgumentException if the player type is not recognized
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
    /**
     * Abstract base builder for all player characters.
     *
     * @param <T> the type of PlayerCharacter this builder creates
     */
    public static abstract class PlayerBuilder<T extends PlayerCharacter> {

        // Data Members
        private String name;
        private int health;
        private int power;

        /**
         * Sets the name for the player.
         *
         * @param name the player's name
         * @return this builder instance for chaining
         */
        public PlayerBuilder<T> setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the additional health to be added to the base character.
         *
         * @param health the bonus health
         * @return this builder instance for chaining
         */
        public PlayerBuilder<T> setHealth(int health) {
            this.health = health;
            return this;
        }

        /**
         * Sets the additional power to be added to the base character.
         *
         * @param power the bonus power
         * @return this builder instance for chaining
         */
        public PlayerBuilder<T> setPower(int power) {
            this.power = power;
            return this;
        }

        /**
         * Builds and returns the specific player character.
         *
         * @return a new instance of T
         */
        public abstract T build();
    }

    // Warrior Builder
    /**
     * Builder class for creating Warrior instances.
     */
    public static class WarriorBuilder extends PlayerBuilder<Warrior> {

        // Data Members
        private int defence;

        // Methods
        /**
         * Sets the defense stat for the Warrior.
         *
         * @param defence the defense value
         * @return this builder instance for chaining
         */
        public WarriorBuilder setDefence(int defence) { this.defence = defence; return this; }

        /**
         * Builds and returns a Warrior instance with the configured attributes.
         *
         * @return a new Warrior instance
         */
        @Override
        public Warrior build() {
            return new Warrior(super.name, super.health, super.power, defence);
        }
    }

    // Archer Builder
    /**
     * Builder class for creating Archer instances.
     */
    public static class ArcherBuilder extends PlayerBuilder<Archer> {

        // Data Members
        private double accuracy;

        // Methods
        /**
         * Sets the accuracy stat for the Archer.
         *
         * @param accuracy the accuracy value (e.g., 0.8 for 80% accuracy)
         * @return this builder instance for chaining
         */
        public ArcherBuilder setAccuracy(double accuracy) { this.accuracy = accuracy; return this; }

        /**
         * Builds and returns an Archer instance with the configured attributes.
         *
         * @return a new Archer instance
         */
        @Override
        public Archer build() {
            return new Archer(super.name, super.health, super.power, accuracy);
        }
    }

    // Mage Builder
    /**
     * Builder class for creating Mage instances.
     */
    public static class MageBuilder extends PlayerBuilder<Mage> {

        // Data Members
        private MagicElement magicElement;

        // Methods
        /**
         * Sets the magic element for the Mage.
         *
         * @param magicElement the element type (e.g., FIRE, WATER)
         * @return this builder instance for chaining
         */
        public MageBuilder setMagicElement(MagicElement magicElement) { this.magicElement = magicElement; return this; }

        /**
         * Builds and returns a Mage instance with the configured attributes.
         *
         * @return a new Mage instance
         */
        @Override
        public Mage build() {
            return new Mage(super.name, super.health, super.power, magicElement);
        }
    }
}
