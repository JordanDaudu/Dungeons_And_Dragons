package game.gui;

import game.characters.PlayerCharacter;

import java.util.List;
import javax.swing.JFrame;

public class CongratulationsGUI extends GameEndDialogGUI {

    public CongratulationsGUI(JFrame parent, List<PlayerCharacter> players) {
        super(parent, players);
    }

    @Override
    protected String getDialogTitle() {
        return "★ CONGRATULATIONS ★\n★ YOU WON ★"; // Title
    }

    @Override
    protected String getBackgroundImagePath() {
        return "/images/winning.jpg"; // Image Path
    }

    @Override
    protected String getMusicTrack() {
        return "winning"; // Music track
    }
}

