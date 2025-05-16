package game.gui;

import game.characters.PlayerCharacter;
import game.core.ScreenListener;

import javax.swing.*;
import java.awt.*;

public class InventoryDialogGUI extends JDialog {

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
