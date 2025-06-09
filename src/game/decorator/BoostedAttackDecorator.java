package game.decorator;

import game.characters.AbstractCharacter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A character decorator that temporarily boosts the character's attack power.
 * When the ability is used, the character's power is increased by a fixed amount
 * for a duration of 15 seconds.
 */
public class BoostedAttackDecorator extends CharacterDecorator {

    // Data Members
    private final int boostAmount = 5;
    private final int abilityTimeInMilliseconds = 15000;

    // Methods
    /**
     * Constructs a new {@code BoostedAttackDecorator} that wraps the given character.
     *
     * @param character the character to decorate with the boosted attack ability
     */
    public BoostedAttackDecorator(AbstractCharacter character) {
        super(character);
    }

    /**
     * Activates the boosted attack ability, increasing the character's power temporarily.
     *
     * @return true if the ability was successfully applied
     */
    @Override
    public boolean useAbility() {
        return boostAttack();
    }

    /**
     * Returns the display name of the ability.
     *
     * @return the name "Boost Attack"
     */
    @Override
    public String getAbilityName() {
        return "Boost Attack";
    }

    /**
     * Returns a description of the ability effect.
     *
     * @return a string explaining the boost effect and duration
     */
    @Override
    public String getAbilityInfo() {
        return "Boosts power by +" + boostAmount + " for 15 seconds.";
    }

    /**
     * Returns the duration of the ability's effect in milliseconds.
     *
     * @return the duration in milliseconds (15000 ms)
     */
    @Override
    public int abilityTimeInMilliseconds() {
        return abilityTimeInMilliseconds;
    }

    /**
     * Applies the power boost to the character and schedules a task to remove it after 15 seconds.
     *
     * @return true if the boost was successfully applied
     */
    private boolean boostAttack() {
        int currentPower = getCharacter().getPower();
        int boostedPower = currentPower + boostAmount;

        getCharacter().setPower(boostedPower);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getCharacter().setPower(getCharacter().getPower() - boostAmount);
                timer.cancel();
            }
        }, abilityTimeInMilliseconds); // 15 seconds

        return true;
    }
}
