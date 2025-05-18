package game.gui;

import game.characters.Archer;
import game.characters.Mage;
import game.characters.PlayerCharacter;
import game.characters.Warrior;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

/**
 * A panel that displays the current status of a player character.
 * Includes their image, name, type, health, power, treasure, and a visual health bar.
 * This panel can be reused in dialogs or embedded in the main game interface.
 */
public class PlayerStatusPanelGUI extends JPanel {

    // Data Members
    private PlayerCharacter player;
    private JLabel imageLabel;
    private JTextArea nameLabel;
    private JTextArea typeLabel;
    private JLabel healthLabel;
    private HealthBarPanelGUI healthBar;
    private JLabel powerLabel;
    private JLabel treasureLabel;
    private PassiveSkillPanelGUI passiveSkillPanelGUI;

    // Methods
    /**
     * Constructs the panel and initializes its components based on the given player.
     *
     * @param player the player character whose status should be shown
     */
    public PlayerStatusPanelGUI(PlayerCharacter player) {
        this.player = player;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        initComponents();
        layoutComponents();
    }

    /**
     * Updates the panel to reflect the current state of the given player.
     * If the player is the same, only dynamic stats are updated. If it's a different player,
     * identity and image data are also updated.
     *
     * @param newPlayer the new player data to display
     */
    public void updatePlayer(PlayerCharacter newPlayer) {
        boolean isSamePlayer = this.player.equals(newPlayer);

        if (!isSamePlayer) {
            Image scaledImage = newPlayer.getDisplayImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            nameLabel.setText("Name: " + newPlayer.getName());
            typeLabel.setText("Type: " + newPlayer.getType());
        }

        passiveSkillSet(newPlayer);

        healthLabel.setText("Health: " + newPlayer.getHealth() + "/" + newPlayer.getMaxHealth());
        powerLabel.setText("Power: " + newPlayer.getPower());
        treasureLabel.setText("Treasure points: " + newPlayer.getTreasurePoints());
        healthBar.updateHealth(newPlayer.getHealth(), isSamePlayer);
        passiveSkillPanelGUI.setVisible(true);

        this.player = newPlayer;
        revalidate();
        repaint();
    }

    /**
     * Initializes all Swing components used in the panel.
     */
    private void initComponents() {
        // === Stats Images ===
        ImageIcon heartIcon = loadAndScaleIcon("/icons/heart.png", 24, 24);
        ImageIcon powerIcon = loadAndScaleIcon("/icons/power.png", 24, 24);
        ImageIcon treasureIcon = loadAndScaleIcon("/icons/treasure_chest.png", 24, 24);

        // === Player Image ===
        Image scaledImage = player.getDisplayImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Identity Group ===
        nameLabel = createWrappedLabel("Name: " + player.getName(), new Font("Serif", Font.BOLD, 20));
        typeLabel = createWrappedLabel("Type: " + player.getType(), new Font("SansSerif", Font.BOLD, 14));

        // === Stats Group ===
        healthLabel = new JLabel("Health: " + player.getHealth() + "/" + player.getMaxHealth(), heartIcon, JLabel.LEFT);
        healthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        healthLabel.setForeground(new Color(200, 30, 30));
        healthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        healthBar = new HealthBarPanelGUI(player.getHealth(), player.getMaxHealth());
        healthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        healthBar.setPreferredSize(new Dimension(200, 40));

        powerLabel = new JLabel("Power: " + player.getPower(), powerIcon, JLabel.LEFT);
        powerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        powerLabel.setForeground(new Color(30, 60, 200));
        powerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        treasureLabel = new JLabel("Treasure points: " + player.getTreasurePoints(), treasureIcon, JLabel.LEFT);
        treasureLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        treasureLabel.setForeground(new Color(212, 175, 55));
        treasureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // === Passive Skill Panel ===
        ImageIcon passiveIcon = loadAndScaleIcon("/images/missing.png", 40, 40);
        passiveSkillPanelGUI = new PassiveSkillPanelGUI(passiveIcon, "NULL", "NULL");
        passiveSkillPanelGUI.setVisible(false); // initially hidden
    }

    /**
     * Lays out all components vertically in the panel using BoxLayout.
     */
    private void layoutComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // === Player Image ===
        add(imageLabel);

        // === Identity Group ===
        JPanel identityPanel = new JPanel();
        identityPanel.setLayout(new BoxLayout(identityPanel, BoxLayout.Y_AXIS));
        identityPanel.setOpaque(false);
        identityPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        identityPanel.add(nameLabel);
        identityPanel.add(typeLabel);
        add(identityPanel);

        // === Stats Group ===
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Stats"));

        statsPanel.add(healthLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(healthBar);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(powerLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(treasureLabel);
        add(statsPanel);

        // === Passive skill panel ===
        add(Box.createVerticalStrut(10));
        add(passiveSkillPanelGUI);
    }

    /**
     * Loads an image from a path and scales it to the specified size.
     *
     * @param path the resource path of the image
     * @param width the desired width
     * @param height the desired height
     * @return the scaled ImageIcon
     */
    private ImageIcon loadAndScaleIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Creates a non-editable, transparent text area for displaying a label with word wrapping.
     *
     * @param text the text to display
     * @param font the font to apply
     * @return a configured JTextArea component
     */
    private JTextArea createWrappedLabel(String text, Font font) {
        JTextArea area = new JTextArea(text);
        area.setFont(font);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setAlignmentX(Component.CENTER_ALIGNMENT);
        area.setMargin(new Insets(0, 0, 0, 0));
        // doing *2 in preferredSize to get more vertical space (for at least 2 lines)
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, area.getPreferredSize().height * 2));
        return area;
    }

    private void passiveSkillSet(PlayerCharacter character) {
        if(character instanceof Warrior)
            passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/icons/defence.png", 40, 40), "Special Defence", "Has higher defence and durability.");
        else if(character instanceof Archer)
            passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/icons/accuracy.png", 40, 40), "Special Accuracy", "Has higher accuracy when attacking.");
        else if(character instanceof Mage)
            passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/icons/multiplier.png", 40, 40), "Special Attack", "Has elemental attack with higher multipliers.");
        else
            passiveSkillPanelGUI = new PassiveSkillPanelGUI(loadAndScaleIcon("/images/missing.png", 40, 40), "NULL", "NULL");
    }
}
