package game.gui;

import game.characters.PlayerCharacter;

import java.util.List;
import javax.swing.JFrame;

/**
 * A GUI dialog that displays a congratulatory message when the player(s) win the game.
 * It extends {@link GameEndDialogGUI} and customizes the appearance, background, and music for the victory state.
 */
public class CongratulationsGUI extends GameEndDialogGUI {

    /**
     * Constructs a new CongratulationsGUI dialog.
     *
     * @param parent  the parent frame from which this dialog is displayed
     * @param players the list of player characters to display in the dialog
     */
    public CongratulationsGUI(JFrame parent, List<PlayerCharacter> players) {
        super(parent, players);
    }

    /**
     * Returns the HTML-formatted dialog title displayed in the victory screen.
     *
     * @return a formatted string representing the dialog title
     */
    @Override
    protected String getDialogTitle() {
        return "<html>★ CONGRATULATIONS ★<br>★ YOU WON ★</html>"; // Title
    }

    /**
     * Returns the file path to the background image used in the victory dialog.
     *
     * @return a string path to the background image
     */
    @Override
    protected String getBackgroundImagePath() {
        return "/images/winning.jpg"; // Image Path
    }

    /**
     * Returns the name of the music track to play during the victory dialog.
     *
     * @return the name of the victory music track
     */
    @Override
    protected String getMusicTrack() {
        return "winning"; // Music track
    }
}

