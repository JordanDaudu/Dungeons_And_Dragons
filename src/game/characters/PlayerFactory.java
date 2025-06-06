package game.characters;

import game.combat.MagicElement;

import java.util.HashMap;
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

        public abstract T build();
    }

    // Warrior Builder
    public static class WarriorBuilder extends PlayerBuilder<Warrior> {
        private int defence;

        public WarriorBuilder setDefence(int defence) { this.defence = defence; return this; }

        @Override
        public Warrior build() {
            return new Warrior(super.name, super.health, super.power, defence);
        }
    }

    // Archer Builder
    public static class ArcherBuilder extends PlayerBuilder<Archer> {
        private double accuracy;

        public ArcherBuilder setAccuracy(double accuracy) { this.accuracy = accuracy; return this; }

        @Override
        public Archer build() {
            return new Archer(super.name, super.health, super.power, accuracy);
        }
    }

    // Mage Builder
    public static class MageBuilder extends PlayerBuilder<Mage> {
        private MagicElement magicElement;

        public MageBuilder setMagicElement(MagicElement magicElement) { this.magicElement = magicElement; return this; }

        @Override
        public Mage build() {
            return new Mage(super.name, super.health, super.power, magicElement);
        }
    }



//    // Abstract base builder with common fields
//    public static abstract class PlayerBuilder<T extends PlayerBuilder<T>> {
//        private String name;
//        private int power;
//
//        public T name(String name) {
//            this.name = name;
//            return self();
//        }
//
//        public T power(int power) {
//            this.power = power;
//            return self();
//        }
//
//        protected abstract T self();
//
//        public abstract PlayerCharacter build();
//    }
//
//    public static class WarriorBuilder extends PlayerBuilder<WarriorBuilder> {
//        private int health;
//        private int defense;
//
//        public WarriorBuilder health(int health) {
//            this.health = health;
//            return this;
//        }
//
//        public WarriorBuilder defense(int defense) {
//            this.defense = defense;
//            return this;
//        }
//
//        @Override
//        protected WarriorBuilder self() {
//            return this;
//        }
//
//        @Override
//        public Warrior build() {
//            return new Warrior(super.name, health, super.power, defense);
//        }
//    }
//
//    public static class ArcherBuilder extends PlayerBuilder<ArcherBuilder> {
//        private int health;
//        private double accuracy;
//
//        public ArcherBuilder health(int health) {
//            this.health = health;
//            return this;
//        }
//
//        public ArcherBuilder accuracy(double accuracy) {
//            this.accuracy = accuracy;
//            return this;
//        }
//
//        @Override
//        protected ArcherBuilder self() {
//            return this;
//        }
//
//        @Override
//        public Archer build() {
//            return new Archer(super.name, health, super.power, accuracy);
//        }
//    }
//
//    public static class MageBuilder extends PlayerBuilder<MageBuilder> {
//        private int health;
//        private MagicElement magicElement = null;
//
//        public MageBuilder health(int health) {
//            this.health = health;
//            return this;
//        }
//
//        public MageBuilder magicElement(MagicElement magicElement) {
//            this.magicElement = magicElement;
//            return this;
//        }
//
//        @Override
//        protected MageBuilder self() {
//            return this;
//        }
//
//        @Override
//        public Mage build() {
//            return new Mage(super.name, health, super.power, magicElement);
//        }
//    }
}
