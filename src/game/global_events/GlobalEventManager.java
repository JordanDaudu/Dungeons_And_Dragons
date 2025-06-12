package game.global_events;

import game.core.ScreenListener;
import game.engine.RandomUtil;
import game.logging.GameLogger;
import game.map.GameMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the scheduling and execution of global events in the game.
 * Periodically triggers random global events from a pool, such as sandstorms or magic waves.
 */
public class GlobalEventManager {

    // Data Members
    private final List<GlobalEvent> eventPool = new ArrayList<>();
    private final GameMap map;
    private final ScreenListener gameController;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean running;

    // Methods
    /**
     * Constructs a GlobalEventManager that operates on the given map and game controller.
     * Initializes a pool of available global events.
     *
     * @param map the game map affected by global events
     * @param gameController the controller to communicate GUI updates or event effects
     */
    public GlobalEventManager(GameMap map, ScreenListener gameController, AtomicBoolean running) {
        this.map = map;
        this.gameController = gameController;
        this.running = running;
        eventPool.add(new MagicWaveEvent());
        eventPool.add(new SandstormEvent());
    }

    /**
     * Starts the periodic scheduling of global events.
     * Triggers the first event after a random delay between 30 and 90 seconds.
     */
    public void start() {
        if (!running.get()) return; // Prevent starting when it's false
        scheduleNextEvent(); // Start scheduling events
    }

    /**
     * Schedules the next global event after a randomized delay.
     * When the scheduled time elapses, a random event from the pool is executed.
     *
     * Logs the triggered global event via GameLogger.
     */
    private void scheduleNextEvent() {
        if (!running.get()) return;

        int delayMillis = RandomUtil.getRandomInt(30_000, 90_000); // 30s to 90s
        scheduler.schedule(() -> {
            if (!running.get()) return;

            try {
                GlobalEvent event = eventPool.get(RandomUtil.getRandomInt(eventPool.size()));
                event.execute(map, gameController);
                GameLogger.getInstance().log("Global Event: " + event.getName());
            }
            catch (Exception e) {
                e.printStackTrace(); // Log and continue
            }

            // If still running, schedule the next event
            if (running.get()) {
                scheduleNextEvent();
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the global event manager and cancels any scheduled events.
     * Interrupts any waiting threads and shuts down the scheduler.
     */
    public void stop() {
        running.set(false);
        scheduler.shutdownNow(); // interrupt any waiting
    }

    public boolean isSchedulerShutdown() {
        return scheduler.isShutdown() || scheduler.isTerminated();
    }

    public void restartScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor(); // Reinitialize scheduler
    }
}
