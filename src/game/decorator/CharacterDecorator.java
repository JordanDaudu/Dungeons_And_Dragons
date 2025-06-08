package game.decorator;

import game.characters.AbstractCharacter;

import java.io.Serializable;

public abstract class CharacterDecorator implements Ability, Serializable {
    private final AbstractCharacter character;

    public CharacterDecorator(AbstractCharacter character) {
        this.character = character;
    }

    protected AbstractCharacter getCharacter() {return character;}

    @Override
    public boolean useAbility() {
        return false;
    }
}
