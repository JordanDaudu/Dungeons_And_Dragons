package game.memento;

import java.io.*;

/**
 * SaveAdapter implementation that saves and loads game state
 * using files in the "saves/" directory.
 */
public class FileSaveAdapter implements SaveAdapter {

    // Data Members
    private static final String SAVE_FOLDER = "saves/";

    // Methods
    /**
     * Creates the saves folder if it doesn't exist.
     */
    public FileSaveAdapter() {
        File dir = new File(SAVE_FOLDER);
        if (!dir.exists()) dir.mkdirs();
    }

    /**
     * Saves the game state memento to a file.
     *
     * @param memento the game state to save
     * @param filename the filename to save to
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void saveToFile(GameWorldMemento memento, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(memento);
        }
    }

    /**
     * Loads the game state memento from a file.
     *
     * @param filename the filename to load from
     * @return the loaded game state memento
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    @Override
    public GameWorldMemento loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (GameWorldMemento) in.readObject();
        }
    }

}

