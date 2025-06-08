package game.decorator;

import game.characters.AbstractCharacter;

import java.util.Timer;
import java.util.TimerTask;

public class BoostedAttackDecorator extends CharacterDecorator {

    private final int boostAmount = 5;
    private final int abilityTimeInMilliseconds = 15000;

    public BoostedAttackDecorator(AbstractCharacter character) {
        super(character);
    }

    @Override
    public boolean useAbility() {
        return boostAttack();
    }

    @Override
    public String getAbilityName() {
        return "Boost Attack";
    }

    @Override
    public String getAbilityInfo() {
        return "Boosts power by +" + boostAmount + " for 15 seconds.";
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return abilityTimeInMilliseconds;
    }

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
