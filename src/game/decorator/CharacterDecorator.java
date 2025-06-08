package game.decorator;

import game.characters.AbstractCharacter;

public abstract class CharacterDecorator implements Ability {
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
