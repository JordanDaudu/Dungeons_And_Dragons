package game.gui;

import game.characters.PlayerCharacter;
import game.core.ScreenListener;
import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
    public PlayerStatusDialogGUI(JFrame parentFrame, PlayerCharacter player, ScreenListener gameController) {
        super(parentFrame, "Player Status", true); // modal

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the reusable panel
        PlayerStatusPanelGUI statusPanel = new PlayerStatusPanelGUI(player, gameController);
        add(statusPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            SoundManager.playEffect("closingSound");
            dispose();
        });

        // Bind ESC key
        String escActionKey = "ESCAPE_CLOSE";
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), escActionKey);
        getRootPane().getActionMap().put(escActionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.playEffect("closingSound");
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
}
