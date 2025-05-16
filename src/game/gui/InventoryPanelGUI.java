package game.gui;

import game.characters.PlayerCharacter;
import game.core.ScreenAction;
import game.core.ScreenListener;
import game.items.Interactable;
import game.items.Potion;
import game.items.PowerPotion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.*;

public class InventoryPanelGUI extends JPanel {

    private PlayerCharacter player;
    private final ScreenListener controllerListener;
    private final JPanel itemsPanel;
    private final Map<String, ImageIcon> itemIcons = new HashMap<>();
    private final ImageIcon emptyPotionIcon;

    public InventoryPanelGUI(PlayerCharacter player, ScreenListener controllerListener) {
        this.player = player;
        this.controllerListener = controllerListener;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Load icons
        itemIcons.put(Potion.class.getSimpleName(), loadImage("/images/life_potion.png", 64, 64));
        itemIcons.put(PowerPotion.class.getSimpleName(), loadImage("/images/power_potion.png", 64, 64));
        emptyPotionIcon = loadImage("/images/empty_potion.png", 32, 32);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        refreshInventory();

        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);
    }

    public void updatePlayer(PlayerCharacter newPlayer) {
        this.player = newPlayer;
        refreshInventory();
    }

    private void refreshInventory() {
        itemsPanel.removeAll();

        if (player.isEmpty()) {
            JLabel emptyLabel = new JLabel("Inventory is empty.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
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
                JButton useButton = createItemButton(entry, itemName);
                itemsPanel.add(Box.createVerticalStrut(10));
                itemsPanel.add(useButton);
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JButton createItemButton(Map.Entry<String, Integer> entry, String itemName) {
        int count = entry.getValue();
        String label = "Use " + itemName + (count > 1 ? " x" + count : "");
        ImageIcon icon = itemIcons.get(itemName);

        JButton button = new JButton(label, icon);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.addActionListener((ActionEvent e) -> {
            boolean used = false;
            String interactionDetails = null;

            for (Interactable item : player.getInventory().getItems()) {
                if (item.getClass().getSimpleName().equals(itemName)) {
                    interactionDetails = item.getInteractionDetails();
                    break;
                }
            }

            used = switch (itemName) {
                case "Potion" -> player.usePotion();
                case "PowerPotion" -> player.usePowerPotion();
                default -> false;
            };

            if (used) {
                String message = itemName + " used!";
                if (interactionDetails != null) {
                    message += "\nGained " + interactionDetails;
                }

                JOptionPane.showMessageDialog(this, message, "Item Used", JOptionPane.INFORMATION_MESSAGE, emptyPotionIcon);
                controllerListener.onAction(ScreenAction.END_TURN, (Object) null);
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JDialog) {
                    window.dispose(); // Close dialog if inside one
                }
            }
            else {
                JOptionPane.showMessageDialog(this, "No " + itemName + " available.");
            }
        });

        return button;
    }

    private ImageIcon loadImage(String path, int width, int height) {
        URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("WARNING: Image not found at " + path + ", using fallback.");
            imageUrl = getClass().getResource("/images/missing.png");
            if (imageUrl == null) {
                System.err.println("ERROR: Fallback image '/images/missing.png' also missing.");
                return null;
            }
        }

        Image image = new ImageIcon(imageUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
}
