package game.decorator;

import game.characters.AbstractCharacter;
import game.characters.Enemy;
import game.characters.PlayerCharacter;
import game.map.GameMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportingEnemyDecorator extends CharacterDecorator {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean teleportScheduled = false;

    public TeleportingEnemyDecorator(AbstractCharacter character) {
        super(character);
    }

    public boolean teleportIfNeeded() {
        if (getCharacter().getHealth() <= getCharacter().getMaxHealth() * 0.3) {
            GameMap.getInstance().removeEntity(getCharacter());
            if (getCharacter() instanceof PlayerCharacter player)
                GameMap.getInstance().placePlayerRandomly(player);
            else if (getCharacter() instanceof Enemy enemy)
                GameMap.getInstance().placeEnemyRandomly(enemy);
            teleportScheduled = false;
            scheduler.shutdown(); // stop checking once done
            return true;
        }
        return false;
    }

    public boolean teleporting() {
        if (teleportScheduled) return false;

        teleportScheduled = true;

        scheduler.scheduleAtFixedRate(() -> {
            if (teleportIfNeeded()) {
                // done, no need to reschedule
            }
        }, 0, 4, TimeUnit.SECONDS);

        return false;
    }


    @Override
    public boolean useAbility() {
        teleporting();
        return true;
    }

    @Override
    public String getAbilityName() {
        return "Teleporting";
    }

    @Override
    public String getAbilityInfo() {
        return "When reduced to 30% HP, the character will teleport to a random free cell on the map";
    }

    @Override
    public int abilityTimeInMilliseconds() {
        return -1; // Not a timed effect
    }





}
