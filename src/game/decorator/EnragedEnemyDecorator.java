package game.decorator;

import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.combat.Combatant;
import game.logging.GameLogger;

public class EnragedEnemyDecorator extends EnemyDecorator {

    private final int originalPower;
    private final int maxPowerIncrease; // 5% of original power scaled by maxHealth

    public EnragedEnemyDecorator(Enemy character) {
        super(character);
        this.originalPower = character.getPower();
        this.maxPowerIncrease = (int) Math.ceil(originalPower * 0.05 * (character.getMaxHealth() / 100.0));
    }

    public EnragedEnemyDecorator(EnragedEnemyDecorator other) {
        super(other);
        this.originalPower = other.originalPower;
        this.maxPowerIncrease = other.maxPowerIncrease;
    }

    @Override
    protected EnragedEnemyDecorator clone() throws CloneNotSupportedException {
        Enemy clonedBase = (Enemy) getDecoratedEnemy().callClone(); // Deep clone the base character
        return new EnragedEnemyDecorator(clonedBase); // Correct return type
    }

    @Override
    public void receiveDamage(int amount, Combatant source) {
        super.receiveDamage(amount, source);
        int lostHealth = getDecoratedEnemy().getMaxHealth() - getDecoratedEnemy().getHealth();
        double lostPercentage = (double) lostHealth / getDecoratedEnemy().getMaxHealth();

        int newPower = originalPower + (int) Math.floor(maxPowerIncrease * lostPercentage);
        getDecoratedEnemy().setPower(newPower);
        GameLogger.getInstance().log(getBaseCharacter().getLogName() + " is enraged power boosted from " + originalPower + " to " + newPower + ".");
    }

    @Override
    public boolean useAbility() {
        return false; // Passive
    }

//    private void updateEnragedPower() {
//        int lostHealth = getDecoratedEnemy().getMaxHealth() - getDecoratedEnemy().getHealth();
//        double lostPercentage = (double) lostHealth / getDecoratedEnemy().getMaxHealth();
//
//        int newPower = originalPower + (int) Math.floor(maxPowerIncrease * lostPercentage);
//        getDecoratedEnemy().setPower(newPower);
//    }
//
//    private void startPeriodicCheck() {
//        checkTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (!getDecoratedEnemy().isDead()) {
//                    updateEnragedPower();
//                }
//                else {
//                    checkTimer.cancel(); // Stop checking when dead
//                }
//            }
//        }, 0, 2000); // Check every 2 seconds
//    }

//    @Override
//    public String getAbilityName() {
//        return "Enraged";
//    }
//
//    @Override
//    public String getAbilityInfo() {
//        return "Power increases with damage taken (up to +" + maxPowerIncrease + ").";
//    }

    @Override
    public int abilityTimeInMilliseconds() {
        return -1; // Not a timed effect
    }
}
