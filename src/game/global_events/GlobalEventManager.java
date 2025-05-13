package game.global_events;

import game.core.ScreenListener;
import game.engine.RandomUtil;
import game.map.GameMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GlobalEventManager {
    private final List<GlobalEvent> eventPool = new ArrayList<>();
    private final GameMap map;
    private final ScreenListener gameController;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean running = true;

    public GlobalEventManager(GameMap map, ScreenListener gameController) {
        this.map = map;
        this.gameController = gameController;
        eventPool.add(new MagicWaveEvent());
        // Add more events if needed
    }

    public void start() {
        scheduleNextEvent(); // start the cycle
    }

    private void scheduleNextEvent() {
        if (!running) return;

        int delayMillis = RandomUtil.getRandomInt(30_000, 90_000); // 30s to 90s
        scheduler.schedule(() -> {
            if (!running) return;

            try {
                GlobalEvent event = eventPool.get(RandomUtil.getRandomInt(eventPool.size()));
                System.out.println("Global Event: " + event.getName());
                event.execute(map, gameController);
            }
            catch (Exception e) {
                e.printStackTrace(); // Log and continue
            }

            // Schedule the next event after this one finishes
            scheduleNextEvent();
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        running = false;
        scheduler.shutdownNow(); // interrupt any waiting
    }
}
