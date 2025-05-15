package game.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameLogger implements Runnable {
    private static volatile GameLogger instance = null;
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final PrintWriter writer;

    private GameLogger() throws IOException {
        writer = new PrintWriter(new FileWriter("src/game/logging/game.log", true), true);
        Thread loggerThread = new Thread(this, "GameLoggerThread");
        loggerThread.setDaemon(true); // So it doesn't block shutdown
        loggerThread.start();
    }

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

    public void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logQueue.offer("[" + timestamp + "] " + message);
    }

    public void stop() {
        running.set(false);
    }

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
