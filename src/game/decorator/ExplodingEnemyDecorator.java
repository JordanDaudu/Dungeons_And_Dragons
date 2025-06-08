package game.decorator;

import game.characters.AbstractCharacter;
import game.map.GameMap;
import game.map.Position;

public class ExplodingEnemyDecorator extends CharacterDecorator {

    public ExplodingEnemyDecorator(AbstractCharacter character) {
        super(character);
    }

    @Override
    public boolean useAbility() {
        return explode();
    }

    @Override
    public String getAbilityName() {
        return "Explosion";
    }

    @Override
    public String getAbilityInfo() {
        return "When dying explodes and damage all adjacent positions by 2% of max HP";
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return 0;
    }

    public boolean explode() {
        GameMap map = GameMap.getInstance();
        Position characterPosition = getCharacter().getPosition();
        int damage = (int) (getCharacter().getMaxHealth() * 0.02);
        map.damageCharactersAround(characterPosition, damage);
        return true;
    }
}
