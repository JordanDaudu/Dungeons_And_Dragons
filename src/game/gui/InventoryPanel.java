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
import java.net.URL;
import java.util.*;

/**
 * A modal dialog that displays the inventory of a given player.
 * Allows the player to use items such as Potions and PowerPotions.
 * Notifies the controller when an item is used and the turn ends.
 */
public class InventoryPanel extends JDialog {

    // Data Members
    private final PlayerCharacter player;
    private final JPanel itemsPanel;
    private final Map<String, ImageIcon> itemIcons = new HashMap<>(); // Using a map for future extendability
    private final ImageIcon emptyPotion;
    private final ScreenListener controllerListener;

    // Methods
    /**
     * Constructs the inventory panel for a player, showing usable items.
     *
     * @param parent             the parent frame to center this dialog on
     * @param player             the player whose inventory is to be shown
     * @param controllerListener the listener to notify game state changes like ending turn
     */
    public InventoryPanel(JFrame parent, PlayerCharacter player, ScreenListener controllerListener) {
        super(parent, "Inventory - " + player.getName(), true);
        this.player = player;
        this.controllerListener = controllerListener;

        // Load icons, we are making sure they are scaled down to look normal in the inventory
        itemIcons.put(Potion.class.getSimpleName(), loadImage("/images/life_potion.png", 64, 64));
        itemIcons.put(PowerPotion.class.getSimpleName(), loadImage("/images/power_potion.png", 64, 64));
        emptyPotion = loadImage("/images/empty_potion.png", 32, 32);

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

    /**
     * Refreshes the inventory UI by:
     * - Clearing existing components
     * - Listing each unique item type with counts
     * - Creating buttons for using items
     * - Handling interactions and item usage logic
     */
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
                JButton useButton = getButton(entry, itemName);

                useButton.addActionListener((ActionEvent e) -> {
                    boolean used = false;
                    String interactionDetails = null;

                    for (Interactable item : player.getInventory().getItems()) {
                        if (item.getClass().getSimpleName().equals(itemName)) {
                            interactionDetails = item.getInteractionDetails(); // no cast needed
                            break;
                        }
                    }
                    if (itemName.equals(Potion.class.getSimpleName())) {
                        used = player.usePotion();
                    }
                    else if (itemName.equals(PowerPotion.class.getSimpleName())) {
                        used = player.usePowerPotion();
                    }

                    if (used) {
                        if(interactionDetails != null)
                            JOptionPane.showMessageDialog(this, itemName + " used!\nGained " + interactionDetails,
                                    "Item Used", JOptionPane.INFORMATION_MESSAGE, emptyPotion);
                        else
                            JOptionPane.showMessageDialog(this, itemName + " used!",
                                    "Item Used", JOptionPane.INFORMATION_MESSAGE, emptyPotion);

                        // Close the inventory panel after use
                        dispose();

                        controllerListener.onAction(ScreenAction.END_TURN, (Object) null);
                        refreshInventory(); // Refresh after use
                        revalidate();
                        repaint();
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "No " + itemName + " available.");
                    }
                });

                itemsPanel.add(useButton);
            }
        }
    }

    private JButton getButton(Map.Entry<String, Integer> entry, String itemName) {
        int count = entry.getValue();

        String buttonText = "Use " + itemName + (count > 1 ? " x" + count : "");
        ImageIcon icon = itemIcons.get(itemName); // Getting the specific Image of each item

        // Button to use specific interactable
        JButton useButton = new JButton(buttonText, icon);
        useButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        useButton.setHorizontalAlignment(SwingConstants.LEFT); // Aligns text left
        return useButton;
    }

    /**
     * Loads an image from the classpath and scales it to the specified dimensions.
     * If the image cannot be found, attempts to load a fallback 'missing.png' image.
     *
     * @param path   the path to the image in the resources
     * @param width  desired width of the scaled image
     * @param height desired height of the scaled image
     * @return the scaled ImageIcon or null if no image could be loaded
     */
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
