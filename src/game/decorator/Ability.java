package game.decorator;

/**
 * The {@code Ability} interface defines a special action or effect
 * that can be used by game entities, typically in combat contexts.
 * <p>
 * Implementations of this interface can be decorated or extended to add functionality
 * such as cooldowns, visual effects, or composite abilities.
 */
public interface Ability {

    /**
     * Activates the ability.
     * <p>
     * This method should trigger the associated effect (e.g., attack, healing,
     * buff, trap) and handle any internal state such as cooldown or usage limits.
     *
     * @return {@code true} if the ability was successfully used,
     *         {@code false} otherwise (e.g., if on cooldown or conditions not met)
     */
    boolean useAbility();

    /**
     * Returns the total time the ability takes to execute or remain active.
     * <p>
     * This could represent an animation delay, cooldown period,
     * or duration for a status effect.
     *
     * @return the ability time in milliseconds
     */
    int abilityTimeInMilliseconds();
}
