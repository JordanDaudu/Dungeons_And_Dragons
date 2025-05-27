package game.gui;

import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.SoundManager;
import game.logging.GameLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A panel for creating a new player character, allowing the user to:
 * - Enter a name
 * - Choose a class (Warrior, Archer, Mage)
 * - View class descriptions and game instructions
 * - Start the game via the provided ScreenListener.
 *
 * Displays itself in a modal dialog and exits the game if closed without starting.
 */
public class PlayerCreationPanelGUI extends JPanel {

    // Data Members
    private JDialog dialog;
    private JTextField nameField;
    private JRadioButton warriorButton, archerButton, mageButton;
    private ImageIcon warriorIcon, archerIcon, mageIcon;
    private ButtonGroup classGroup;
    private JPanel classPanel;
    private JButton startButton;
    private String playerName;
    private String selectedClass;
    private JLabel nameLabel, classLabel;
    private JTextPane classDescriptionPane;
    private JScrollPane scrollPane;
    private final ScreenListener listener;

    // Methods
    /**
     * Constructs a new PlayerCreationPanel with the required listener.
     *
     * @param listener the ScreenListener used to trigger game start events
     */
    public PlayerCreationPanelGUI(ScreenListener listener) {
        this.listener = listener;

        initComponents();
        layoutComponents();
        attachListeners();
        initDialog();
    }

    /**
     * Displays the player creation panel as a modal dialog.
     * Blocks input to other windows until the dialog is closed.
     */
    public void showModal() {
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    /**
     * Initializes and configures all GUI components including
     * labels, buttons, text fields, icons, and panes.
     */
    private void initComponents() {
        Color bgColor = new Color(240, 235, 220);  // parchment
        this.setBackground(bgColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        playerName = null;
        selectedClass = null;

        // Player Name Field
        Color accent = new Color(80, 50, 20);      // fantasy brown
        nameLabel = new JLabel("Enter your name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(accent);
        nameField = new JTextField(20);
        nameField.setMaximumSize(nameField.getPreferredSize());
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Class Selection Label
        classLabel = new JLabel("Choose your class:");
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load icons
        warriorIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/warrior.png")));
        archerIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/archer.png")));
        mageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/mage.png")));

        // Scale icons
        warriorIcon = scaleIcon(warriorIcon, 100, 100);
        archerIcon = scaleIcon(archerIcon, 100, 100);
        mageIcon = scaleIcon(mageIcon, 100, 100);

        // Create radio buttons
        warriorButton = createClassRadioButton("Warrior", warriorIcon);
        archerButton = createClassRadioButton("Archer", archerIcon);
        mageButton = createClassRadioButton("Mage", mageIcon);

        // Group buttons
        classGroup = new ButtonGroup();
        classGroup.add(warriorButton);
        classGroup.add(archerButton);
        classGroup.add(mageButton);

        // Panel for class buttons
        classPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        classPanel.add(warriorButton);
        classPanel.add(archerButton);
        classPanel.add(mageButton);
        classPanel.setBackground(bgColor);

        // Class Description Panel with scroll
        classDescriptionPane = new JTextPane();
        classDescriptionPane.setEditable(false);
        classDescriptionPane.setContentType("text/html");
        classDescriptionPane.setBackground(getBackground());
        classDescriptionPane.setText(getDefaultDescription());

        scrollPane = new JScrollPane(classDescriptionPane);
        scrollPane.setMaximumSize(new Dimension(500, 150));
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Start Button
        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Organizes and lays out the visual components on the panel,
     * including separators, spacing, and alignment.
     */
    private void layoutComponents() {
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(nameLabel);
        add(nameField);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(classLabel);

        // --- Adding horizontal separator above classPanel ---
        JSeparator topSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        topSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(topSeparator);

        // --- Adding classPanel with a bit of spacing ---
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(classPanel);

        // --- Adding horizontal separator below classPanel ---
        add(Box.createRigidArea(new Dimension(0, 10)));
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(bottomSeparator);

        // --- Adding instructions and description ---
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startButton);
    }

    /**
     * Attaches event listeners to buttons and radio buttons,
     * including logic for starting the game and playing sound effects.
     */
    private void attachListeners() {
        startButton.addActionListener(new ActionListener() {
            /**
             * Handles the "Start Game" button click.
             * Validates user input (name and class), notifies the screen listener,
             * and closes the dialog if successful. Otherwise, displays an error.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText();
                if (selectedClass != null && !playerName.trim().isEmpty()) {
                    try {
                        GameLogger.getInstance().log("Created new player: " + playerName + ", class: " + selectedClass);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace(); // Print to console to see the cause
                        JOptionPane.showMessageDialog(PlayerCreationPanelGUI.this,
                                "Logging failed: " + ex.getMessage(), "Log Error", JOptionPane.ERROR_MESSAGE);
                    }

                    SoundManager.playEffect("clickSound");
                    listener.onAction(ScreenAction.START_GAME, playerName, selectedClass);
                    dialog.setVisible(false);
                }
                else {
                    JOptionPane.showMessageDialog(PlayerCreationPanelGUI.this, "Please enter your name and select a class.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Initializes and configures the modal dialog that wraps this panel.
     * Ensures proper exit behavior and assigns the default button trigger.
     */
    private void initDialog() {
        // Setup Dialog
        dialog = new JDialog();
        dialog.setTitle("Character Creation");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.add(this);

        // Enter trigger the Start Game button
        dialog.getRootPane().setDefaultButton(startButton);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            /**
             * Handles the event where the player creation window is closed manually
             * before starting the game. Logs the closure and exits the application.
             */
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("StartScreen was closed without starting the game.");
                System.exit(0); // Exit the game
            }
        });
    }

    /**
     * Creates a radio button representing a player class, complete with image and listener.
     *
     * @param className  the name of the class (e.g., "Warrior")
     * @param classImage the icon associated with the class
     * @return a configured JRadioButton
     */
    private JRadioButton createClassRadioButton(final String className, ImageIcon classImage) {
        JRadioButton radioButton = new JRadioButton(className);
        radioButton.setIcon(classImage);
        radioButton.setHorizontalTextPosition(SwingConstants.CENTER);
        radioButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        radioButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        radioButton.setContentAreaFilled(false);
        radioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon hoverIcon = scaleIcon(classImage, 110, 110);
        Color glowColor = getGlowColor(className);
        ImageIcon glowIcon = createGlowingIcon(classImage, glowColor, 8);

        radioButton.addActionListener(e -> {
            SoundManager.playEffect("clickSound");
            selectedClass = className;
            updateClassDescription(className);
            updateIcons();
        });

        radioButton.addMouseListener(new MouseAdapter() {
            /**
             * Triggered when the mouse enters the button area.
             * If the button is not currently selected, a slightly enlarged
             * icon is shown to indicate interactivity.
             *
             * @param e the mouse event
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!radioButton.isSelected()) {
                    radioButton.setIcon(hoverIcon); // optional slight scale on hover
                }
            }

            /**
             * Triggered when the mouse exits the button area.
             * If the button is selected, its glowing icon is restored;
             * otherwise, the original base icon is restored.
             *
             * @param e the mouse event
             */
            @Override
            public void mouseExited(MouseEvent e) {
                if (radioButton.isSelected()) {
                    radioButton.setIcon(glowIcon);
                }
                else {
                    radioButton.setIcon(classImage);
                }
            }
        });
        return radioButton;
    }

    /**
     * Updates the class description pane with details about the selected class
     * and game instructions.
     *
     * @param className the name of the selected class
     */
    private void updateClassDescription(String className) {
        String description = switch (className) {
            case "Warrior" -> "<b><u>Warrior</u></b><br>" +
                    "A melee-focused fighter skilled in close-range physical combat.<br>" +
                    "Excels in defense and durability.";
            case "Archer" -> "<b><u>Archer</u></b><br>" +
                    "A ranged combatant who excels at striking from afar with physical attacks.<br>" +
                    "Boasts high accuracy, making them less likely to miss.";
            case "Mage" -> "<b><u>Mage</u></b><br>" +
                    "A master of magic who uses powerful ranged spells to defeat enemies.<br>" +
                    "Specializes in high-power elemental attacks.";
            default -> "";
        };
        classDescriptionPane.setText("<html><body style='font-family:sans-serif; font-size:14px;'>" +
                description + getInstructionsHTML() + "</body></html>");

        // Force scroll to the top after update
        classDescriptionPane.setCaretPosition(0);
    }

    /**
     * Returns the default description to show in the class description pane
     * before a class has been selected.
     *
     * @return an HTML string with default instructions
     */
    private String getDefaultDescription() {
        return "<html><body style='font-family:sans-serif; font-size:14px;'>" +
                "Select a class to see its description." +
                getInstructionsHTML() +
                "</body></html>";
    }

    /**
     * Returns an HTML-formatted string with basic game instructions for display.
     *
     * @return an HTML string describing game controls
     */
    private String getInstructionsHTML() {
        return "<br><br><b><u>Game Instructions</u></b><br>" +
                "Use <b>WASD</b> keys or <b>left-click</b> to move your character.<br>" +
                "Press <b>E</b> or <b>middle-click</b> to open your inventory.<br>" +
                "Use <b>right-click</b> on an entity to view its information.<br>" +
                "Press <b>Q</b> to view your player status.<br>" +
                "Press <b>ESC</b> to open the settings menu";
    }

    /**
     * Scales an ImageIcon to the given width and height using smooth scaling.
     *
     * @param icon  the icon to scale
     * @param width the target width
     * @param height the target height
     * @return a new scaled ImageIcon
     */
    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    /**
     * Refreshes the icons of all class buttons to reflect the selected
     * class (with glow effect) and reset unselected buttons to base icons.
     */
    private void updateIcons() {
        updateButtonIcon(warriorButton, "Warrior", warriorIcon);
        updateButtonIcon(archerButton, "Archer", archerIcon);
        updateButtonIcon(mageButton, "Mage", mageIcon);
    }

    /**
     * Updates a single class radio button’s icon based on whether it is selected.
     *
     * @param button the radio button to update
     * @param className the class associated with the button
     * @param baseIcon the base (non-glowing) icon
     */
    private void updateButtonIcon(JRadioButton button, String className, ImageIcon baseIcon) {
        if (className.equals(selectedClass)) {
            Color glowColor = getGlowColor(className);
            button.setIcon(createGlowingIcon(baseIcon, glowColor, 8));
        }
        else {
            button.setIcon(baseIcon);
        }
    }

    /**
     * Generates an icon with a glowing effect by rendering a blurred
     * colored halo around the original icon.
     *
     * @param originalIcon the base icon
     * @param glowColor the color of the glow effect
     * @param glowSize the size (thickness) of the glow effect
     * @return an ImageIcon with a glow surrounding it
     */
    private ImageIcon createGlowingIcon(ImageIcon originalIcon, Color glowColor, int glowSize) {
        int width = originalIcon.getIconWidth() + glowSize * 2;
        int height = originalIcon.getIconHeight() + glowSize * 2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Enable quality rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw glow by repeatedly painting translucent color
        for (int i = glowSize; i > 0; i--) {
            float opacity = (float) i / (glowSize * 2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2.setColor(glowColor);
            g2.fillRoundRect(i, i, originalIcon.getIconWidth(), originalIcon.getIconHeight(), 20, 20);
        }

        // Draw original image on top
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(originalIcon.getImage(), glowSize, glowSize, null);
        g2.dispose();

        return new ImageIcon(image);
    }

    /**
     * Determines the glow color associated with a specific class.
     *
     * @param className the class name
     * @return the Color used for that class’s glow effect
     */
    private Color getGlowColor(String className) {
        return switch (className) {
            case "Warrior" -> new Color(70, 130, 180);      // SteelBlue
            case "Archer" -> new Color(60, 179, 113);     // MediumSeaGreen
            case "Mage" -> new Color(138, 43, 226);       // BlueViolet
            default -> Color.WHITE;
        };
    }
}
