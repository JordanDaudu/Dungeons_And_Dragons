package game.decorator;

/**
 * Represents a special ability that can be used by a character or item.
 * Implementations define the behavior, name, info text, and duration of the ability.
 */
public interface Ability {

    /**
     * Activates the ability.
     *
     * @return true if the ability was successfully used, false otherwise.
     */
    boolean useAbility();

    /**
     * Returns the name of the ability.
     *
     * @return a short string describing the ability name.
     */
    String getAbilityName();

    /**
     * Provides detailed information or description of the ability.
     *
     * @return a string explaining the ability's function or effect.
     */
    String getAbilityInfo();

    /**
     * Returns the duration of the ability's effect in milliseconds.
     *
     * @return the ability effect duration in ms.
     */
    int abilityTimeInMilliseconds();
}
