package game.characters;

import game.engine.RandomUtil;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnemyTask implements Runnable {
    private final Enemy enemy;
    private final ScheduledExecutorService scheduler;

    public EnemyTask(Enemy enemy, ScheduledExecutorService scheduler) {
        this.enemy = enemy;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        if (enemy.isDead()) {
            System.out.println(enemy + " is dead. Stopping task.");
            return; // Don’t reschedule
        }

        enemy.threadAction(); // Defined in enemy

        int nextDelayMillis = RandomUtil.getRandomInt(500, 1501); // Random between 500 (inclusive) and 1500 (inclusive)
        scheduler.schedule(new EnemyTask(enemy, scheduler), nextDelayMillis, TimeUnit.MILLISECONDS);
    }
}
