package game.gui;

import game.characters.PlayerCharacter;

import java.util.List;
import javax.swing.JFrame;


public class GameOverGUI extends GameEndDialogGUI {

    public GameOverGUI(JFrame parent, List<PlayerCharacter> players) {
        super(parent, players);
    }

    @Override
    protected String getDialogTitle() {
        return "★ GAME OVER ★"; // Title
    }

    @Override
    protected String getBackgroundImagePath() {
        return "/images/gameover.jpeg"; // Image Path
    }

    @Override
    protected String getMusicTrack() {
        return "gameOver"; // Music track
    }
}
