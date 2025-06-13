package game.memento;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Singleton class that manages saving and loading game state mementos.
 * Keeps a fixed number of save slots and persists them to disk.
 */
public class SaveManager {

    // Caretaker (GameWorld will be used as originator)
    // Singleton instance
    private static final SaveManager instance = new SaveManager();

    // Data Members
    private final Deque<GameWorldMemento> saveSlots = new LinkedList<>();
    private static final int MAX_SLOTS = 10;

    // Methods
    /**
     * Private constructor that loads saved files from disk when the instance is created.
     */
    private SaveManager() {loadSavedFiles();}

    /**
     * Returns the singleton instance of SaveManager.
     *
     * @return the SaveManager instance
     */
    public static SaveManager getInstance() {
        return instance;
    }

    /**
     * Saves the given game world memento to a slot.
     * If the maximum number of slots is reached, removes the oldest save.
     * Saves all slots to disk.
     *
     * @param memento the game state snapshot to save
     */
    public void save(GameWorldMemento memento) {
        if (saveSlots.size() >= MAX_SLOTS) {
            saveSlots.removeLast();
        }
        saveSlots.addFirst(memento); // Add newest at top

        try {
            // Save all current slots to disk in order
            int index = 0;
            for (GameWorldMemento m : saveSlots) {
                new FileSaveAdapter().saveToFile(m, "saves/slot" + index + ".ser");
                index++;
            }
        } catch (IOException e) {
            System.err.println("Failed to save slot to file: " + e.getMessage());
        }
    }

    /**
     * Loads a saved memento from the specified slot index.
     *
     * @param index the index of the save slot
     * @return the game state snapshot saved at the given slot
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public GameWorldMemento loadMemento(int index) {
        if (index < 0 || index >= saveSlots.size()) throw new IndexOutOfBoundsException();
        return new ArrayList<>(saveSlots).get(index); // indexable access
    }

    /**
     * Returns a queue of all save slots, ordered so that the newest save is first.
     *
     * @return queue of saved game state snapshots
     */
    public Queue<GameWorldMemento> getSaveSlots() {
        LinkedList<GameWorldMemento> reversed = new LinkedList<>(saveSlots);
        java.util.Collections.reverse(reversed); // so index 0 = newest
        return reversed;
    }

    /**
     * Loads saved files from disk into the save slots on startup.
     * Any errors during loading are printed to stderr.
     */
    private void loadSavedFiles() {
        File dir = new File("saves/");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        if (files != null) {
            FileSaveAdapter adapter = new FileSaveAdapter();

            for (File file : files) {
                try {
                    GameWorldMemento memento = adapter.loadFromFile(file.getPath());
                    saveSlots.offer(memento);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Failed to load save from " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }
}
