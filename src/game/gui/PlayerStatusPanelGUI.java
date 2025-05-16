package game.gui;

import game.characters.PlayerCharacter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A reusable panel that displays the current status of a player character.
 * Can be embedded in dialogs or shown directly in the main game UI.
 */
public class PlayerStatusPanelGUI extends JPanel {

    private PlayerCharacter player;
    private final JLabel imageLabel;
    private final JLabel nameLabel;
    private final JLabel typeLabel;
    private final JLabel healthLabel;
    private final HealthBarPanelGUI healthBar;
    private final JLabel powerLabel;
    private final JLabel treasureLabel;

    public PlayerStatusPanelGUI(PlayerCharacter player) {
        this.player = player;
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Player image
        Image image = player.getDisplayImage();
        Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(imageLabel);

        // Player name
        nameLabel = new JLabel("Name: " + player.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(nameLabel);

        // Player type
        typeLabel = new JLabel("Type: " + player.getType());
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(typeLabel);

        // Health
        healthLabel = new JLabel("Health: " + player.getHealth() + "/" + player.getMaxHealth());
        healthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(10));
        add(healthLabel);

        // Health bar
        healthBar = new HealthBarPanelGUI(player.getHealth(), player.getMaxHealth());
        healthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        healthBar.setPreferredSize(new Dimension(200, 40));
        add(healthBar);

        // Power
        powerLabel = new JLabel("Power: " + player.getPower());
        powerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(powerLabel);

        // Treasure
        treasureLabel = new JLabel("Treasure Points: " + player.getTreasurePoints());
        treasureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(treasureLabel);
    }

    public void updatePlayer(PlayerCharacter newPlayer) {
        if(!this.player.equals(newPlayer)) {
            // Update player image
            Image image = newPlayer.getDisplayImage();
            Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));

            // Update all labels
            nameLabel.setText("Name: " + newPlayer.getName());
            typeLabel.setText("Type: " + newPlayer.getType());
        }
        healthLabel.setText("Health: " + newPlayer.getHealth() + "/" + newPlayer.getMaxHealth());
        powerLabel.setText("Power: " + newPlayer.getPower());
        treasureLabel.setText("Treasure Points: " + newPlayer.getTreasurePoints());

        // Update health bar
        boolean isSamePlayer = this.player.equals(newPlayer);
        healthBar.updateHealth(newPlayer.getHealth(), isSamePlayer);

        this.player = newPlayer;
        revalidate();
        repaint();
    }

}
