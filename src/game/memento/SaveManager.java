package game.memento;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SaveManager {


    // Caretaker (GameWorld will be used as originator)
    // Singleton instance
    private static final SaveManager instance = new SaveManager();

    // Data Members
    private final Queue<GameWorldMemento> saveSlots = new LinkedList<>();
    private static final int MAX_SLOTS = 5;

    // Methods
    private SaveManager() {loadSavedFiles();}

    public static SaveManager getInstance() {
        return instance;
    }

    public void save(GameWorldMemento memento) {
        if (saveSlots.size() >= MAX_SLOTS) {
            saveSlots.poll(); // Remove oldest
        }
        saveSlots.offer(memento);

        try {
            int index = saveSlots.size() - 1;
            new FileSaveAdapter().saveToFile(memento, "saves/slot" + index + ".ser");
        } catch (IOException e) {
            System.err.println("Failed to save slot to file: " + e.getMessage());
        }
    }

    public GameWorldMemento loadMemento(int index) {
        if (index < 0 || index >= saveSlots.size()) throw new IndexOutOfBoundsException();
        return new ArrayList<>(saveSlots).get(index); // indexable access
    }

    public Queue<GameWorldMemento> getSaveSlots() {
        return new LinkedList<>(saveSlots); // prevent external modification
    }

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

    public int getTotalSlots() {
        return saveSlots.size();
    }
}
