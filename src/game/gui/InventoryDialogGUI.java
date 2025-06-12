package game.gui;

import game.characters.PlayerCharacter;
import game.core.ScreenListener;
import game.engine.GameController;
import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A modal dialog window that displays a player's inventory.
 *
 * This dialog shows the inventory of the specified player character
 * and provides a button to close the dialog.
 */
public class InventoryDialogGUI extends JDialog {

    /**
     * Constructs an InventoryDialogGUI tied to the specified parent frame,
     * showing the inventory of the given player character.
     *
     * @param parent             the parent JFrame over which this dialog will be centered
     * @param player             the PlayerCharacter whose inventory is displayed
     * @param controllerListener the ScreenListener that handles inventory interactions
     */
    public InventoryDialogGUI(JFrame parent, PlayerCharacter player, ScreenListener controllerListener, GameController gameController) {
        super(parent, "Inventory - " + player.getName(), true);

        setLayout(new BorderLayout());

        InventoryPanelGUI panel = new InventoryPanelGUI(player, controllerListener);
        add(panel, BorderLayout.CENTER);

        // Pausing threads
        GameController.pauseEnemyTasks();
        GameController.pauseManagerEvent();

        JButton close = new JButton("Close");
        close.addActionListener(e -> {
            SoundManager.playEffect("closingSound");
            // Activating threads back
            GameController.resumeEnemyTasks();
            GameController.resumeManagerEvent();
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
                GameController.resumeEnemyTasks();
                GameController.resumeManagerEvent();
                dispose();
            }
        });

        // Set custom close operation
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                SoundManager.playEffect("closingSound");
                GameController.resumeEnemyTasks();
                GameController.resumeManagerEvent();
                dispose(); // Closes the window manually
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(close);

        add(bottomPanel, BorderLayout.SOUTH);

        setSize(300, 400);
        setLocationRelativeTo(parent);
    }
}
