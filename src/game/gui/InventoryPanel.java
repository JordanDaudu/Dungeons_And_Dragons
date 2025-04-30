package game.gui;

import game.characters.PlayerCharacter;
import game.engine.ScreenAction;
import game.engine.ScreenListener;
import game.items.Interactable;
import game.items.Potion;
import game.items.PowerPotion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class InventoryPanel extends JDialog {
    private final PlayerCharacter player;
    private final JPanel itemsPanel;
    private final ImageIcon lifePotionIcon, powerPotionIcon;
    private final ScreenListener controllerListener;

    public InventoryPanel(JFrame parent, PlayerCharacter player, ScreenListener controllerListener) {
        super(parent, "Inventory - " + player.getName(), true);
        this.player = player;
        this.controllerListener = controllerListener;

        // Load icons, we are making sure they are scaled down to look normal in the inventory
        lifePotionIcon = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/life_potion.png")))
                        .getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        powerPotionIcon = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/power_potion.png")))
                        .getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));

        setLayout(new BorderLayout());

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        refreshInventory();

        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        add(close, BorderLayout.SOUTH);

        setSize(300, 400);
        setLocationRelativeTo(parent);
    }

    private void refreshInventory() {
        itemsPanel.removeAll();

        player.printInventoryOfPlayer();

        if (player.isEmpty()) {
            JLabel emptyLabel = new JLabel("Inventory is empty.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(emptyLabel);
        }
        else {
            Map<String, Integer> itemCounts = new HashMap<>();

            for (Interactable item : player.getInventory().getItems()) {
                String name = item.getClass().getSimpleName();
                itemCounts.put(name, itemCounts.getOrDefault(name, 0) + 1);
            }

            for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                String itemName = entry.getKey();
                int count = entry.getValue();

                String buttonText = "Use " + itemName + (count > 1 ? " x" + count : "");
                ImageIcon icon = null;

                if (itemName.equals(Potion.class.getSimpleName())) {
                    icon = lifePotionIcon;
                } else if (itemName.equals(PowerPotion.class.getSimpleName())) {
                    icon = powerPotionIcon;
                }

                JButton useButton = new JButton(buttonText, icon);
                useButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                useButton.setHorizontalAlignment(SwingConstants.LEFT); // Aligns text left

                useButton.addActionListener((ActionEvent e) -> {
                    boolean used = false;
                    if (itemName.equals(Potion.class.getSimpleName())) {
                        used = player.usePotion();
                    } else if (itemName.equals(PowerPotion.class.getSimpleName())) {
                        used = player.usePowerPotion();
                    }

                    if (used) {
                        JOptionPane.showMessageDialog(this, itemName + " used!");
                        controllerListener.onAction(ScreenAction.END_TURN, (Object) null);
                        refreshInventory(); // Refresh after use
                        revalidate();
                        repaint();
                    } else {
                        JOptionPane.showMessageDialog(this, "No " + itemName + " available.");
                    }
                });

                itemsPanel.add(useButton);
            }
        }
    }
}
