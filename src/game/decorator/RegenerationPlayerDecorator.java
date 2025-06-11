package game.decorator;

import game.characters.PlayerCharacter;

import java.util.Timer;
import java.util.TimerTask;

public class RegenerationPlayerDecorator extends PlayerDecorator {
    private final int healAmount = 6;
    private final int interval = 3000; // 3 seconds in ms
    private final int totalDuration = 15000; // 15 seconds in ms

    public RegenerationPlayerDecorator(PlayerCharacter character) {
        super(character);
    }

    public RegenerationPlayerDecorator(RegenerationPlayerDecorator other) {
        super(other);
    }

    @Override
    protected RegenerationPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new RegenerationPlayerDecorator(clonedBase); // Correct return type
    }

    @Override
    public boolean useAbility() {
        return regenerateHealth();
    }

//    @Override
//    public String getAbilityName() {
//        return "Regenerate Health";
//    }
//
//    @Override
//    public String getAbilityInfo() {
//        return "Regenerate " + healAmount + " every " + interval / 1000 + " for " + totalDuration / 1000 + " seconds.";
//    }
//
//    @Override
//    public int abilityTimeInMilliseconds() {
//        return totalDuration;
//    }

    public int getInterval() {return interval;}

    public int getTotalDuration() {return totalDuration;}

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

    private void superHeal(int amount) {
        super.heal(amount);
    }
}
