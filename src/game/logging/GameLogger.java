package game.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GameLogger is a singleton logger class that runs on a dedicated daemon thread.
 * It asynchronously writes log messages with timestamps to a log file using a blocking queue.
 * This ensures thread-safe, non-blocking logging across the game.
 */
public class GameLogger implements Runnable {

    // Data Members
    private static volatile GameLogger instance = null;
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final PrintWriter writer;

    // Methods
    /**
     * Private constructor to initialize the logger.
     * Opens the log file and starts a background thread to consume and write messages.
     */
    private GameLogger() throws IOException {
        writer = new PrintWriter(new FileWriter("src/game/logging/game.log", true), true);
        Thread loggerThread = new Thread(this, "GameLoggerThread");
        loggerThread.setDaemon(true); // So it doesn't block shutdown
        loggerThread.start();
    }

    /**
     * Returns the singleton instance of the logger.
     * If not yet created, it initializes the instance in a thread-safe manner.
     *
     * @return the singleton instance of GameLogger
     */
    public static GameLogger getInstance() {
        if (instance == null) {
            synchronized (GameLogger.class) {
                if(instance == null) {
                    try {
                        instance = new GameLogger();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to start logger.");
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Adds a log message to the queue with a timestamp.
     * The message will be written to the log file by the logger thread.
     *
     * @param message the log message to record
     */
    public void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logQueue.offer("[" + timestamp + "] " + message);
    }

    /**
     * Signals the logger to stop running after all queued messages are written.
     */
    public void stop() {
        running.set(false);
    }

    /**
     * Continuously runs in the background, taking log messages from the queue
     * and writing them to the log file until stopped.
     */
    @Override
    public void run() {
        try {
            while (running.get() || !logQueue.isEmpty()) {
                String logMessage = logQueue.take();  // Blocks if empty
                writer.println(logMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writer.close();
        }
    }
}
