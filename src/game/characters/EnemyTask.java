package game.characters;

import game.engine.RandomUtil;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnemyTask implements Runnable {
    private final Enemy enemy;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running;

    public EnemyTask(Enemy enemy, ScheduledExecutorService scheduler, AtomicBoolean running) {
        this.enemy = enemy;
        this.scheduler = scheduler;
        this.running = running;
    }

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
