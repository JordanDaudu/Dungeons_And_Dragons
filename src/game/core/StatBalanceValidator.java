package game.core;

/**
 * Utility class that provides methods to validate stat balance rules
 * for different character classes (Warrior, Archer, Mage).
 * Each method ensures that custom stat modifications sum to a balanced state,
 * preventing overpowering characters during customization.
 */
public class StatBalanceValidator {

    /**
     * Validates that the sum of stat modifications for a warrior equals zero.
     * @param health modification
     * @param power modification
     * @param defense modification
     * @return true if balanced, false otherwise
     */
    public static boolean validateWarrior(int health, int power, int defense) {
        return (health + power + defense) == 0;
    }

    /**
     * Validates that the sum of stat modifications for an archer equals zero.
     * @param health modification
     * @param power modification
     * @param accuracy modification (double)
     * @return true if balanced (within small epsilon), false otherwise
     */
    public static boolean validateArcher(int health, int power, double accuracy) {
        double sum = health + power + (accuracy * 100);
        return Math.abs(sum) < 1e-6;
    }

    /**
     * Validates that the sum of stat modifications for a mage equals zero.
     * Element is categorical and does not affect balancing.
     * @param health modification
     * @param power modification
     * @return true if balanced, false otherwise
     */
    public static boolean validateMage(int health, int power) {
        return (health + power) == 0;
    }
}
