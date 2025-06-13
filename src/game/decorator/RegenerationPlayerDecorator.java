package game.decorator;

import game.characters.PlayerCharacter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A decorator that grants the player health regeneration over a fixed period.
 * Heals the player periodically using a timer.
 */
public class RegenerationPlayerDecorator extends PlayerDecorator {

    // Data Members
    private final int healAmount = 6;
    private final int interval = 3000; // 3 seconds in ms
    private final int totalDuration = 15000; // 15 seconds in ms

    // Methods
    /**
     * Constructs a new RegenerationPlayerDecorator for the given player character.
     *
     * @param character the player character to decorate
     */
    public RegenerationPlayerDecorator(PlayerCharacter character) {
        super(character);
    }

    /**
     * Copy constructor used for cloning.
     *
     * @param other the RegenerationPlayerDecorator to copy
     */
    public RegenerationPlayerDecorator(RegenerationPlayerDecorator other) {
        super(other);
    }

    /**
     * Creates a deep clone of this decorator and its decorated player.
     *
     * @return a cloned RegenerationPlayerDecorator instance
     * @throws CloneNotSupportedException if cloning fails
     */
    @Override
    protected RegenerationPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new RegenerationPlayerDecorator(clonedBase); // Correct return type
    }

    /**
     * Activates the regeneration ability, healing the player periodically.
     *
     * @return true if the ability was successfully triggered
     */
    @Override
    public boolean useAbility() {
        return regenerateHealth();
    }

    /**
     * Gets the interval between healing ticks.
     *
     * @return the interval in milliseconds
     */
    public int getInterval() {return interval;}

    /**
     * Gets the total duration of the regeneration effect.
     *
     * @return the total duration in milliseconds
     */
    public int getTotalDuration() {return totalDuration;}

    /**
     * Starts a timer to heal the player every {@code interval} milliseconds
     * for a total of {@code totalDuration} milliseconds.
     *
     * @return true when the regeneration timer is successfully started
     */
    private boolean regenerateHealth() {
        Timer timer = new Timer();
        final int maxTicks = totalDuration / interval;
        final int[] ticks = {0};

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                superHeal(healAmount);
                ticks[0]++;

                if (ticks[0] >= maxTicks) {
                    timer.cancel(); // Stop after 5 ticks (15 seconds total)
                }
            }
        }, 0, interval); // Start immediately, repeat every 3 seconds

        return true;
    }

    /**
     * Heals the player using the inherited {@code heal} method.
     *
     * @param amount the amount of health to restore
     */
    private void superHeal(int amount) {
        super.heal(amount);
    }
}
