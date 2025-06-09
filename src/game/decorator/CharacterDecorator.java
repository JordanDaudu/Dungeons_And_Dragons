package game.decorator;

import game.characters.AbstractCharacter;

import java.io.Serializable;

/**
 * An abstract decorator class for characters that allows abilities to be dynamically
 * added to characters at runtime. Implements the {@link Ability} interface.
 * Concrete decorators should override {@link #useAbility()} to provide specific behavior.
 */
public abstract class CharacterDecorator implements Ability, Serializable {

    // Data Members
    private final AbstractCharacter character;

    // Methods
    /**
     * Constructs a {@code CharacterDecorator} for the specified character.
     *
     * @param character the character to be decorated
     */
    public CharacterDecorator(AbstractCharacter character) {
        this.character = character;
    }

    /**
     * Gets the character wrapped by this decorator.
     *
     * @return the underlying {@link AbstractCharacter} instance
     */
    protected AbstractCharacter getCharacter() {return character;}

    /**
     * Default implementation of the ability usage method.
     * Concrete decorators should override this method with custom behavior.
     *
     * @return false by default, indicating no ability was used
     */
    @Override
    public boolean useAbility() {
        return false;
    }
}
