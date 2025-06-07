package game.memento;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SaveManager {

    // Data Members
    private final Queue<GameWorldMemento> saveSlots = new LinkedList<>();

    // Methods
    public void save(GameWorldMemento memento) {
        saveSlots.offer(memento);
    }

    public GameWorldMemento loadMemento(int index) {
        if (index < 0 || index >= saveSlots.size()) throw new IndexOutOfBoundsException();
        return new ArrayList<>(saveSlots).get(index); // copy to access by index
    }

    public Queue<GameWorldMemento> getSaveSlots() {return saveSlots;}

}

