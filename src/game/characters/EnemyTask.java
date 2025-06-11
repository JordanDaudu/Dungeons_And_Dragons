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
    private static final Set<Enemy> scheduledEnemies = ConcurrentHashMap.newKeySet();

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
        if (enemy.isDead()) {
            scheduledEnemies.remove(enemy); // Clean up if dead
            return; // Don’t reschedule
        }

        if(running.get()) {
            enemy.threadAction(); // Defined in enemy, only act if not paused
        }

        int nextDelayMillis = RandomUtil.getRandomInt(1500, 3001); // Random between 1500 (inclusive) and 3000 (inclusive)
        scheduler.schedule(new EnemyTask(enemy, scheduler, running), nextDelayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the number of currently scheduled enemies.
     *
     * @return the count of enemies in the scheduled set
     */
    public static int getScheduledEnemyCount() {
        return scheduledEnemies.size();
    }

    /**
     * Adds an enemy to the set of scheduled enemies.
     *
     * @param enemy the enemy to add
     */
    public static void addScheduledEnemy(Enemy enemy) {
        scheduledEnemies.add(enemy);
    }

    /**
     * Clears all enemies from the scheduled set.
     */
    public static void clearScheduledEnemies() {
        scheduledEnemies.clear();
    }

    /**
     * Attempts to schedule a new enemy task if the maximum allowed number of active tasks has not been reached.
     *
     * @param enemy           the enemy to schedule
     * @param maxAllowedCount the maximum number of allowed scheduled enemies
     * @param scheduler       the scheduler used to execute the task
     * @param running         atomic flag indicating whether the system is currently active
     * @return true if the enemy was scheduled and added successfully, false otherwise
     */
    public static boolean tryScheduleAndRegisterEnemy(Enemy enemy, int maxAllowedCount, ScheduledExecutorService scheduler, AtomicBoolean running) {
        synchronized (scheduledEnemies) {
            if (scheduledEnemies.size() >= maxAllowedCount) return false;

            if (scheduledEnemies.add(enemy)) {
                scheduler.schedule(new EnemyTask(enemy, scheduler, running), 1, TimeUnit.SECONDS);
                return true;
            }
            return false;
        }
    }

    /**
     * Calculates the size of the enemy thread pool based on the map size.
     * The size is 3% of the map size, with bounds of 1 to 10.
     *
     * @param mapSize the total size of the game map
     * @return the recommended thread pool size for managing enemy tasks
     */
    public static int calculateStartingEnemyThreadPoolSize(int mapSize) {
        int poolSize = (int) Math.max(1, mapSize * 0.03);
        poolSize = Math.min(poolSize, 10);
        return poolSize;
    }
}
