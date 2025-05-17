package game.gui;

import game.characters.PlayerCharacter;
import game.core.ScreenListener;

import javax.swing.*;
import java.awt.*;

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
    public InventoryDialogGUI(JFrame parent, PlayerCharacter player, ScreenListener controllerListener) {
        super(parent, "Inventory - " + player.getName(), true);

        setLayout(new BorderLayout());

        InventoryPanelGUI panel = new InventoryPanelGUI(player, controllerListener);
        add(panel, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(close);

        add(bottomPanel, BorderLayout.SOUTH);

        setSize(300, 400);
        setLocationRelativeTo(parent);
    }
}
