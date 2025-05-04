package game.gui;

import game.characters.PlayerCharacter;

import java.util.List;
import javax.swing.JFrame;

/**
 * The GameOverGUI class represents the dialog displayed when the game ends in failure.
 * It extends the GameEndDialogGUI to provide a themed "Game Over" screen,
 * including a specific title, background image, and music track.
 */
public class GameOverGUI extends GameEndDialogGUI {

    // Methods
    /**
     * Constructs the GameOverGUI dialog.
     *
     * @param parent  the parent frame to anchor the dialog
     * @param players the list of players at the end of the game
     */
    public GameOverGUI(JFrame parent, List<PlayerCharacter> players) {
        super(parent, players);
    }

    /**
     * Returns the title displayed on the dialog window.
     *
     * @return a string representing the dialog title
     */
    @Override
    protected String getDialogTitle() {
        return "★ GAME OVER ★"; // Title
    }

    /**
     * Returns the file path to the background image for the game over screen.
     *
     * @return the image file path
     */
    @Override
    protected String getBackgroundImagePath() {
        return "/images/gameover.jpeg"; // Image Path
    }

    /**
     * Returns the name of the music track to be played on the game over screen.
     *
     * @return the name of the music track
     */
    @Override
    protected String getMusicTrack() {
        return "gameOver"; // Music track
    }
}
