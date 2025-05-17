package game.gui;

import game.characters.PlayerCharacter;

import javax.swing.*;
import java.awt.*;

/**
 * A dialog window that displays the current status of a player character.
 * It uses a PlayerStatusPanelGUI to show the player's information and provides a button to close the dialog.
 */
public class PlayerStatusDialogGUI extends JDialog {

    // Methods
    /**
     * Creates a modal dialog to display the status of the given player character.
     *
     * @param parentFrame the parent frame for the dialog
     * @param player the player character whose status is displayed
     */
    public PlayerStatusDialogGUI(JFrame parentFrame, PlayerCharacter player) {
        super(parentFrame, "Player Status", true); // modal

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the reusable panel
        PlayerStatusPanelGUI statusPanel = new PlayerStatusPanelGUI(player);
        add(statusPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
}
