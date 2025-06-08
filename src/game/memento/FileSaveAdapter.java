package game.memento;

import java.io.*;

public class FileSaveAdapter implements SaveAdapter {
    private static final String SAVE_FOLDER = "saves/";

    public FileSaveAdapter() {
        File dir = new File(SAVE_FOLDER);
        if (!dir.exists()) dir.mkdirs();
    }

    @Override
    public void saveToFile(GameWorldMemento memento, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(memento);
        }
    }
}

