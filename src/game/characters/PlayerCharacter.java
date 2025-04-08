package game.characters;

import game.combat.Combatant;
import game.map.Position;

public class PlayerCharacter extends AbstractCharacter {

    // AUTO GENERATED USING COMPILER NEEDS REWRITING
    public PlayerCharacter(Position position, double evasionChance) {
        super(position, evasionChance);
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public boolean setHealth() {
        return false;
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {

    }

    @Override
    public String getDisplaySymbol() {
        return "";
    }

    @Override
    public void setVisible(boolean visible) {

    }
}
