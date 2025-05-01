package game.gui;

import game.characters.PlayerCharacter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PlayerStatusDialog extends JDialog {

    private JPanel mainPanel;
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel typeLabel;
    private JLabel healthLabel;
    private JProgressBar healthBar;
    private JLabel powerLabel;
    private JLabel treasureLabel;
    private JButton closeButton;

    public PlayerStatusDialog(JFrame parentFrame, PlayerCharacter player) {
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
        ImageIcon icon = new ImageIcon(image);
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

        healthBar = new JProgressBar(0, player.getMaxHealth());
        healthBar.setValue(player.getHealth());
        healthBar.setStringPainted(true);
        updateHealthColor(healthBar);
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

    private void updateHealthColor(JProgressBar bar) {
        int health = bar.getValue();
        int max = bar.getMaximum();
        float percent = (float) health / max;

        if (percent <= 0.25f) {
            bar.setForeground(Color.RED);
        } else if (percent <= 0.49f) {
            bar.setForeground(Color.YELLOW);
        } else {
            bar.setForeground(new Color(0, 128, 0)); // Green
        }

        bar.repaint(); // Ensure UI updates properly
    }
}
