package game.decorator;

import game.characters.Enemy;
import game.combat.Combatant;
import game.logging.GameLogger;
import game.map.GameMap;

public class TeleportingEnemyDecorator extends EnemyDecorator {

    public TeleportingEnemyDecorator(Enemy character) {
        super(character);
    }

    public TeleportingEnemyDecorator(TeleportingEnemyDecorator other) {
        super(other);
    }

    @Override
    protected TeleportingEnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) getDecoratedEnemy().callClone(); // Deep clone the base character
        return new TeleportingEnemyDecorator(clonedBase); // Correct return type
    }

    @Override
    public boolean useAbility() {
        return false; // Passive Skill
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage(amount, source);
        if(!isDead() && getHealth() < getMaxHealth() * 0.3) {
            teleporting();
            GameLogger.getInstance().log(getBaseCharacter().getLogName() + " teleported away");
        }
    }

    public void teleporting() {
        GameMap.getInstance().removeEntity(this);
        GameMap.getInstance().placeEnemyRandomly(this);
    }

//    @Override
//    public String getAbilityName() {
//        return "Teleporting";
//    }
//
//    @Override
//    public String getAbilityInfo() {
//        return "When reduced to 30% HP, the character will teleport to a random free cell on the map";
//    }

    @Override
    public int abilityTimeInMilliseconds() {
        return -1; // Not a timed effect
    }
}
