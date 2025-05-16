package game.characters;

import game.engine.RandomUtil;

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
            return; // Don’t reschedule
        }

        if (enemy.isDead()) {
            return; // Don’t reschedule
        }

        enemy.threadAction(); // Defined in enemy

        int nextDelayMillis = RandomUtil.getRandomInt(1500, 3001); // Random between 1500 (inclusive) and 3000 (inclusive)
        scheduler.schedule(new EnemyTask(enemy, scheduler, running), nextDelayMillis, TimeUnit.MILLISECONDS);
    }
}
