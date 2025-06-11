package game.decorator;

import game.characters.Enemy;
import game.combat.Combatant;
import game.engine.SoundManager;
import game.logging.GameLogger;
import game.map.GameMap;
import game.map.Position;

public class ExplodingEnemyDecorator extends EnemyDecorator {

    public ExplodingEnemyDecorator(Enemy character) {
        super(character);
    }

    public ExplodingEnemyDecorator(ExplodingEnemyDecorator other) {
        super(other);
    }

    @Override
    protected ExplodingEnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) getDecoratedEnemy().callClone(); // Deep clone the base character
        return new ExplodingEnemyDecorator(clonedBase); // Correct return type
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage(amount, source);
        if(getDecoratedEnemy().isDead()) {
            SoundManager.playEffect("explosion");
            explode();
            GameLogger.getInstance().log(getBaseCharacter().getLogName() + " exploded after dying damaging surrounding.");
        }

    }

//    @Override
//    public String getAbilityName() {
//        return "Explosion";
//    }
//
//    @Override
//    public String getAbilityInfo() {
//        return "When dying explodes and damage all adjacent positions by 2% of max HP";
//    }

    @Override
    public boolean useAbility() {
        return false; // PassiveAbility when dying
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return 0;
    }

    private void explode() {
        GameMap map = GameMap.getInstance();
        Position characterPosition = getDecoratedEnemy().getPosition();
        int damage = (int) (getDecoratedEnemy().getMaxHealth() * 0.02);
        map.damageCharactersAround(characterPosition, damage);
    }
}
