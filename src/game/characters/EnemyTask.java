package game.characters;

import game.engine.RandomUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A task that controls the periodic behavior of an {@link Enemy} in the game.
 * This task is scheduled to run repeatedly using a {@link ScheduledExecutorService},
 * and performs the enemy's AI behavior through the {@code threadAction()} method.
 */
public class EnemyTask implements Runnable {

    // Data Members
    private final Enemy enemy;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running;
    private static final Set<Enemy> scheduledEnemies = ConcurrentHashMap.newKeySet(); // Tracks number of scheduled enemies

    // Methods
    /**
     * Constructs a new {@code EnemyTask} instance.
     *
     * @param enemy      The enemy associated with this task.
     * @param scheduler  The scheduler used to reschedule the task.
     * @param running    A flag indicating whether the task should continue running.
     */
    public EnemyTask(Enemy enemy, ScheduledExecutorService scheduler, AtomicBoolean running) {
        this.enemy = enemy;
        this.scheduler = scheduler;
        this.running = running;
    }

    /**
     * Executes the enemy's action and reschedules the task if conditions allow.
     * The task will not reschedule itself if the {@code running} flag is false
     * or if the enemy is dead. Otherwise, it performs the enemy's logic and
     * reschedules itself to run again after a random delay between 1500 and 3000 milliseconds.
     */
    @Override
    public void run() {
        if(!running.get()) {
            scheduledEnemies.remove(enemy); // Clean up if paused
            return; // Don’t reschedule
        }

        if (enemy.isDead()) {
            scheduledEnemies.remove(enemy); // Clean up if dead
            return; // Don’t reschedule
        }

        scheduledEnemies.add(enemy); // Ensure tracking

        enemy.threadAction(); // Defined in enemy

        int nextDelayMillis = RandomUtil.getRandomInt(1500, 3001); // Random between 1500 (inclusive) and 3000 (inclusive)
        scheduler.schedule(new EnemyTask(enemy, scheduler, running), nextDelayMillis, TimeUnit.MILLISECONDS);
    }

    public static int getScheduledEnemyCount() {
        return scheduledEnemies.size();
    }

    public static int calculateStartingEnemyThreadPoolSize(int mapSize) {
        int poolSize = (int) Math.max(1, mapSize * 0.03);
        poolSize = Math.min(poolSize, 10);
        return poolSize;
    }
}
