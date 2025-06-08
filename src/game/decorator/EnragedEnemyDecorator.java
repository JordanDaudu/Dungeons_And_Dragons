package game.decorator;

import game.characters.AbstractCharacter;

import java.util.Timer;
import java.util.TimerTask;

public class EnragedEnemyDecorator extends CharacterDecorator {

    private final int originalPower;
    private final int maxPowerIncrease; // 5% of original power scaled by maxHealth
    private final Timer checkTimer = new Timer(true); // daemon timer

    public EnragedEnemyDecorator(AbstractCharacter character) {
        super(character);
        this.originalPower = character.getPower();
        this.maxPowerIncrease = (int) Math.ceil(originalPower * 0.05 * (character.getMaxHealth() / 100.0));
    }

    @Override
    public boolean useAbility() {
        startPeriodicCheck();
        return true;
    }

    private void updateEnragedPower() {
        int lostHealth = getCharacter().getMaxHealth() - getCharacter().getHealth();
        double lostPercentage = (double) lostHealth / getCharacter().getMaxHealth();

        int newPower = originalPower + (int) Math.floor(maxPowerIncrease * lostPercentage);
        getCharacter().setPower(newPower);
    }

    private void startPeriodicCheck() {
        checkTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getCharacter().isDead()) {
                    updateEnragedPower();
                }
                else {
                    checkTimer.cancel(); // Stop checking when dead
                }
            }
        }, 0, 2000); // Check every 2 seconds
    }

    @Override
    public String getAbilityName() {
        return "Enraged";
    }

    @Override
    public String getAbilityInfo() {
        return "Power increases with damage taken (up to +" + maxPowerIncrease + ").";
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return -1; // Not a timed effect
    }
}
