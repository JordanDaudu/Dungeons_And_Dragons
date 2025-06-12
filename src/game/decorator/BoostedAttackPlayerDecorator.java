package game.decorator;

import game.characters.AbstractCharacter;
import game.characters.PlayerCharacter;

public class BoostedAttackPlayerDecorator extends PlayerDecorator {

    // Data Members
    private final int boostAmount = 5;

    // Methods
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
    protected BoostedAttackPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new BoostedAttackPlayerDecorator(clonedBase); // Correct return type
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return -1;
    }

    private boolean boostAttack() {
        int currentPower = getDecoratedPlayer().getPower();
        int boostedPower = currentPower + boostAmount;
        if(getDecoratedPlayer() instanceof AbstractCharacter character) {
            character.setPower(boostedPower);
            return true;
        }
        return false;
    }
}
