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

        int nextDelayMillis = RandomUtil.getRandomInt(1500, 3001); // Random between 1500 (inclusive) and 3000 (inclusive)
        scheduler.schedule(new EnemyTask(enemy, scheduler), nextDelayMillis, TimeUnit.MILLISECONDS);
    }
}
