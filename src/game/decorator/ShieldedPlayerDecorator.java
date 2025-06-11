package game.decorator;

import game.characters.PlayerCharacter;
import game.combat.Combatant;

public class ShieldedPlayerDecorator extends PlayerDecorator{
    public ShieldedPlayerDecorator(PlayerCharacter character) {
        super(character);
    }

    public ShieldedPlayerDecorator(ShieldedPlayerDecorator other) {
        super(other);
    }

    @Override
    public boolean useAbility() {
        return false;
    }

    @Override
    protected ShieldedPlayerDecorator clone() throws CloneNotSupportedException {
        PlayerCharacter clonedBase = (PlayerCharacter) getDecoratedPlayer().callClone(); // Deep clone the base character
        return new ShieldedPlayerDecorator(clonedBase);
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return -1;
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage((int) (amount * 0.95), source);
    }
}
