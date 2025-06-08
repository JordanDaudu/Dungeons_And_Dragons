// SaveAdapter.java
package game.memento;

import java.io.IOException;

public interface SaveAdapter {

    void saveToFile(GameWorldMemento memento, String filename) throws IOException;
    GameWorldMemento loadFromFile(String filename) throws IOException, ClassNotFoundException;
}
