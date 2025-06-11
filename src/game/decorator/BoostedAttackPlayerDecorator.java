package game.decorator;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;

public class BoostedAttackPlayerDecorator extends PlayerDecorator {

    private final int boostAmount = 5;

    public BoostedAttackPlayerDecorator(PlayerCharacter character) {
        super(character);
    }

    public BoostedAttackPlayerDecorator(BoostedAttackPlayerDecorator other) {
        super(other);
    }

    @Override
    public boolean useAbility() {
        return boostAttack();
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    protected BoostedAttackPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new BoostedAttackPlayerDecorator(clonedBase); // Correct return type
    }

//    @Override
//    public String getAbilityName() {
//        return "Boost Attack";
//    }
//
//    @Override
//    public String getAbilityInfo() {
//        return "Boosts power by +" + boostAmount + " for 15 seconds.";
//    }
//
    @Override
    public int abilityTimeInMilliseconds() {
        return -1;
    }

    private boolean boostAttack() {
        int currentPower = getDecoratedPlayer().getPower();
        int boostedPower = currentPower + boostAmount;
        if(getDecoratedPlayer() instanceof AbstractCharacter character) {
            character.setPower(boostedPower);
            System.out.println("BOOSTEDATTACK");

//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    character.setPower(getDecoratedPlayer().getPower() - boostAmount);
//                    timer.cancel();
//                }
//            }, abilityTimeInMilliseconds); // 15 seconds

            return true;
        }
        return false;
    }
}
