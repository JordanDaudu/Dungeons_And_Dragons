package game.memento;

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

    private SaveManager() {}

    public static SaveManager getInstance() {
        return instance;
    }

    public void save(GameWorldMemento memento) {
        if (saveSlots.size() >= MAX_SLOTS) {
            saveSlots.poll(); // Remove oldest
        }
        saveSlots.offer(memento);
    }

    public GameWorldMemento loadMemento(int index) {
        if (index < 0 || index >= saveSlots.size()) throw new IndexOutOfBoundsException();
        return new ArrayList<>(saveSlots).get(index); // indexable access
    }

    public Queue<GameWorldMemento> getSaveSlots() {
        return new LinkedList<>(saveSlots); // prevent external modification
    }

    public int getTotalSlots() {
        return saveSlots.size();
    }
}
