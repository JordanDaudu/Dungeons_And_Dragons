package game.decorator;

import game.characters.AbstractCharacter;
import game.combat.MagicAttacker;

import java.util.Timer;
import java.util.TimerTask;

public class MagicAmplifierDecorator extends CharacterDecorator {

    private final int abilityTimeInMilliseconds = 15000;

    public MagicAmplifierDecorator(AbstractCharacter character) {
        super(character);
    }

    @Override
    public boolean useAbility() {
        return amplifyMagic();
    }

    @Override
    public String getAbilityName() {
        return "Amplify Magic";
    }

    @Override
    public String getAbilityInfo() {
        return "Boosts magic power by 4% for 15 seconds.";
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return abilityTimeInMilliseconds;
    }

    private boolean amplifyMagic() {
        if(getCharacter() instanceof MagicAttacker) {
            int currentMagicPower = getCharacter().getPower();
            int boostedMagicPowerToAdd = (int) (currentMagicPower * 0.04);

            getCharacter().setPower(currentMagicPower + boostedMagicPowerToAdd);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getCharacter().setPower(getCharacter().getPower() - boostedMagicPowerToAdd);
                    timer.cancel();
                }
            }, abilityTimeInMilliseconds); // 15 seconds

            return true;
        }
        else
            return false;
    }
}
