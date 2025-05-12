package game.gui;

import game.characters.PlayerCharacter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A modal dialog that displays the current status of a player character,
 * including name, class type, health bar, power level, and treasure points.
 *
 * This dialog is meant to be opened from the main game UI to show a snapshot
 * of the player's stats in a user-friendly interface.
 */
public class PlayerStatusDialogGUI extends JDialog {

    // Data Members
    private final JPanel mainPanel;
    private final JLabel imageLabel;
    private final JLabel nameLabel;
    private final JLabel typeLabel;
    private final JLabel healthLabel;
    private final HealthBarPanelGUI healthBar;
    private final JLabel powerLabel;
    private final JLabel treasureLabel;
    private final JButton closeButton;

    // Methods
    /**
     * Constructs a PlayerStatusDialog displaying detailed information about the specified player.
     *
     * @param parentFrame the parent JFrame that owns this dialog
     * @param player the PlayerCharacter whose status should be displayed
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

        // Main panel with padding
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Player image
        Image image = player.getDisplayImage();
        Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(imageLabel);

        // Player name
        nameLabel = new JLabel("Name: " + player.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(nameLabel);

        // Player type
        typeLabel = new JLabel("Type: " + player.getType());
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(typeLabel);

        // Health bar
        healthLabel = new JLabel("Health: " + player.getHealth() + "/" + player.getMaxHealth());
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(healthLabel);

        healthBar = new HealthBarPanelGUI(player.getHealth(), player.getMaxHealth());
        mainPanel.add(healthBar);

        // Power
        powerLabel = new JLabel("Power: " + player.getPower());
        powerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(powerLabel);

        // Treasure points
        treasureLabel = new JLabel("Treasure Points: " + player.getTreasurePoints());
        treasureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(treasureLabel);

        // Close button
        closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(closeButton);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null); // center on screen
    }
}
