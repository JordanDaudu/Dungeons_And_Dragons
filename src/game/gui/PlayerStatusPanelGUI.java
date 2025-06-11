package game.gui;

import game.characters.Archer;
import game.characters.Mage;
import game.characters.PlayerCharacter;
import game.characters.Warrior;
import game.core.ScreenListener;
import game.decorator.PlayerDecorator;

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
    private final ScreenListener gameController;
    private JLabel imageLabel;
    private JPanel abilityPanel;
    private JTextArea nameLabel;
    private JTextArea typeLabel;
    private JLabel healthLabel;
    private HealthBarPanelGUI healthBar;
    private JLabel powerLabel;
    private JLabel treasureLabel;
    private PassiveSkillPanelGUI passiveSkillPanelGUI;

//    private final Map<String, Long> abilityCooldownEndTimes = new HashMap<>();

    // Track active timers per button to cancel when switching player
//    private Timer cooldownTimer1;
//    private Timer cooldownTimer2;
//
//    private JButton abilityButton1;
//    private JButton abilityButton2;

    // Methods
    /**
     * Constructs the panel and initializes its components based on the given player.
     *
     * @param player the player character whose status should be shown
     */
    public PlayerStatusPanelGUI(PlayerCharacter player, ScreenListener gameController) {
        this.player = player;
        this.gameController = gameController;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        initComponents();
        layoutComponents();
//        attachListeners();
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
            // Cancel any existing cooldown timers when switching player
//            if (cooldownTimer1 != null) cooldownTimer1.stop();
//            if (cooldownTimer2 != null) cooldownTimer2.stop();

//            updateAbilityButtons(newPlayer);
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
        UIManager.put("ToolTip.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.foreground", Color.BLACK);
        // === Stats Images ===
        ImageIcon heartIcon = loadAndScaleIcon("/icons/heart.png", 24, 24);
        ImageIcon powerIcon = loadAndScaleIcon("/icons/power.png", 24, 24);
        ImageIcon treasureIcon = loadAndScaleIcon("/icons/treasure_chest.png", 24, 24);

        // === Player Image ===
        Image scaledImage = player.getDisplayImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === Ability buttons ===
        abilityPanel = new JPanel();
        abilityPanel.setLayout(new BoxLayout(abilityPanel, BoxLayout.X_AXIS));
        abilityPanel.setOpaque(false);
        abilityPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

//        updateAbilityButtons(player);

//        abilityButton1 = new JButton(player.getAbility1().getAbilityName());
//        abilityButton2 = new JButton(player.getAbility2().getAbilityName());
//
//        abilityButton1.setToolTipText(player.getAbility1().getAbilityInfo());
//        abilityButton2.setToolTipText(player.getAbility2().getAbilityInfo());
//        abilityButton1.setFocusable(false);
//        abilityButton2.setFocusable(false);

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

        // === Ability Buttons ===
        add(abilityPanel);

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

//    private void attachListeners() {
//        // === Ability buttons ===
//        if (abilityButton1 != null) {
//            abilityButton1.addActionListener(e -> {
//                if (player != null) {
//                    CharacterDecorator ability = player.getAbility1();
//                    if (canUseAbility(player, ability)) {
//                        if(ability.useAbility()) {
//                            startCooldownForAbility(player, ability, abilityButton1);
//                            gameController.onAction(ScreenAction.REFRESH_GUI, player.getAbility1());
//                        }
//                    }
//                }
//            });
//        }
//
//        if(abilityButton2 != null) {
//            abilityButton2.addActionListener(e -> {
//                if (player != null) {
//                    CharacterDecorator ability = player.getAbility2();
//                    if (canUseAbility(player, ability)) {
//                        if(ability.useAbility()) {
//                            startCooldownForAbility(player, ability, abilityButton2);
//                            gameController.onAction(ScreenAction.REFRESH_GUI, player.getAbility2());
//                        }
//                    }
//                }
//            });
//        }
//    }

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

    /**
     * Updates the passive skill panel based on the character's class type.
     * Displays a corresponding icon, subtitle, and description depending on whether
     * the character is a Warrior, Archer, or Mage. If the character type is unrecognized,
     * the panel is reset to a default "NULL" state.
     *
     * @param character the player character whose passive skill information is to be displayed
     */
    private void passiveSkillSet(PlayerCharacter character) {
        PlayerCharacter player;
        if(character instanceof PlayerDecorator playerDecorator) {
            player = playerDecorator.getBaseCharacter();
        }
        else {
            player = character;
        }
        switch (player) {
            case Warrior ignored ->
                    passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/icons/defence.png", 40, 40), "Special Defence", "Has higher defence and durability.");
            case Archer ignored ->
                    passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/icons/accuracy.png", 40, 40), "Special Accuracy", "Has higher accuracy when attacking.");
            case Mage ignored ->
                    passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/icons/multiplier.png", 40, 40), "Special Attack", "Has elemental attack with higher damage multipliers.");
            case null, default ->
                    passiveSkillPanelGUI.updateContent(loadAndScaleIcon("/images/missing.png", 40, 40), "NULL", "NULL");
        }
    }

//    private boolean canUseAbility(PlayerCharacter player, CharacterDecorator ability) {
//        if (player == null || ability == null) return false;
//
//        String key = getCooldownKey(player, ability); // Unique player ID
//        Long cooldownEnd = abilityCooldownEndTimes.get(key);
//        return cooldownEnd == null || cooldownEnd <= System.currentTimeMillis();
//    }

//    private void startCooldownForAbility(PlayerCharacter player, CharacterDecorator ability, JButton button) {
//        String key = getCooldownKey(player, ability); // Unique player ID
//        long cooldownDuration = 60 * 1000; // 1 minute cooldown in ms
//        long cooldownEndTime = System.currentTimeMillis() + cooldownDuration;
//        abilityCooldownEndTimes.put(key, cooldownEndTime);
//
//        button.setEnabled(false);
//        startCooldownTimer(button, cooldownDuration, ability.getAbilityName(), player, ability);
//    }
//
//    private void checkAndStartCooldown(PlayerCharacter player, CharacterDecorator ability, JButton button) {
//        if (player == null || ability == null || button == null) return;
//
//        String key = getCooldownKey(player, ability); // Unique player ID
//        Long cooldownEnd = abilityCooldownEndTimes.get(key);
//        long now = System.currentTimeMillis();
//
//        if (cooldownEnd == null || cooldownEnd <= now) {
//            // Cooldown expired or none
//            button.setEnabled(true);
//            button.setText(ability.getAbilityName());
//        }
//        else {
//            // Cooldown active - disable and start timer
//            long remaining = cooldownEnd - now;
//            button.setEnabled(false);
//            startCooldownTimer(button, remaining, ability.getAbilityName(), player, ability);
//        }
//    }
//
//    private void startCooldownTimer(JButton button, long durationMs, String abilityName, PlayerCharacter player, CharacterDecorator ability) {
//        if (button == null || abilityName == null || player == null) return;
//
//        int delay = 1000; // 1 second intervals
//        final long endTime = System.currentTimeMillis() + durationMs;
//
//        // Stop previous timer on this button if any
//        if (button == abilityButton1 && cooldownTimer1 != null) cooldownTimer1.stop();
//        if (button == abilityButton2 && cooldownTimer2 != null) cooldownTimer2.stop();
//
//        Timer timer = new Timer(delay, null);
//        timer.addActionListener(e -> {
//            long remaining = endTime - System.currentTimeMillis();
//            if (remaining <= 0) {
//                button.setEnabled(true);
//                button.setText(abilityName);
//                timer.stop();
//                // Remove cooldown when finished (optional)
//                abilityCooldownEndTimes.remove(getCooldownKey(player, ability));
//            } else {
//                int secondsLeft = (int) (remaining / 1000);
//                button.setText("Cooldown: " + secondsLeft + "s");
//                button.setEnabled(false);
//            }
//        });
//        timer.start();
//
//        // Save timer reference to stop if player changes
//        if (button == abilityButton1) cooldownTimer1 = timer;
//        else if (button == abilityButton2) cooldownTimer2 = timer;
//    }
//
//    private void updateAbilityButtons(PlayerCharacter player) {
//        abilityPanel.removeAll();
//        abilityPanel.add(Box.createHorizontalGlue());
//
//        CharacterDecorator ability1 = player.getAbility1();
//        CharacterDecorator ability2 = player.getAbility2();
//
//        if (ability1 != null) {
//            abilityButton1 = new JButton(ability1.getAbilityName());
//            abilityButton1.setToolTipText(ability1.getAbilityInfo());
//            abilityButton1.setFocusable(false);
//            abilityButton1.addActionListener(e -> {
//                if (canUseAbility(player, ability1)) {
//                    if (ability1.useAbility()) {
//                        startCooldownForAbility(player, ability1, abilityButton1);
//                        gameController.onAction(ScreenAction.REFRESH_GUI, ability1);
//                    }
//                }
//            });
//            abilityPanel.add(abilityButton1);
//            abilityPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//            checkAndStartCooldown(player, ability1, abilityButton1);
//        }
//        else {
//            abilityButton1 = null;
//        }
//
//        if (ability2 != null) {
//            abilityButton2 = new JButton(ability2.getAbilityName());
//            abilityButton2.setToolTipText(ability2.getAbilityInfo());
//            abilityButton2.setFocusable(false);
//            abilityButton2.addActionListener(e -> {
//                if (canUseAbility(player, ability2)) {
//                    if (ability2.useAbility()) {
//                        startCooldownForAbility(player, ability2, abilityButton2);
//                        gameController.onAction(ScreenAction.REFRESH_GUI, ability2);
//                    }
//                }
//            });
//            abilityPanel.add(abilityButton2);
//            checkAndStartCooldown(player, ability2, abilityButton2);
//        }
//        else {
//            abilityButton2 = null;
//        }
//
//        abilityPanel.add(Box.createHorizontalGlue());
//        abilityPanel.revalidate();
//        abilityPanel.repaint();
//    }
//
//    private String getCooldownKey(PlayerCharacter player, CharacterDecorator ability) {
//        return player.getId() + ":" + ability.getAbilityName();
//    }
}
