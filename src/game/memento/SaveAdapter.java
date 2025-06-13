package game.memento;

import java.io.IOException;

/**
 * Interface for saving and loading game state mementos to and from storage.
 */
public interface SaveAdapter {

    /**
     * Saves the given game world memento to a file.
     *
     * @param memento the game state snapshot to save
     * @param filename the name of the file to save to
     * @throws IOException if an I/O error occurs during saving
     */
    void saveToFile(GameWorldMemento memento, String filename) throws IOException;

    /**
     * Loads a game world memento from a file.
     *
     * @param filename the name of the file to load from
     * @return the loaded game state snapshot
     * @throws IOException if an I/O error occurs during loading
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    GameWorldMemento loadFromFile(String filename) throws IOException, ClassNotFoundException;
}
