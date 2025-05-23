package game.gui;

import game.core.ScreenAction;
import game.core.ScreenListener;
import game.engine.SoundManager;
import game.logging.GameLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private final JDialog dialog;
    private final JTextField nameField;
    private final JRadioButton warriorButton, archerButton, mageButton;
    private ImageIcon warriorIcon, archerIcon, mageIcon;
    private final ButtonGroup classGroup;
    private final JPanel classPanel;
    private final JButton startButton;
    private String playerName;
    private String selectedClass;
    private final JLabel nameLabel, classLabel;
    private final JTextPane classDescriptionPane;
    private final JScrollPane scrollPane;

    // Methods
    /**
     * Constructs a new PlayerCreationPanel with the required listener.
     *
     * @param listener the ScreenListener used to trigger game start events
     */
    public PlayerCreationPanelGUI(ScreenListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        playerName = null;
        selectedClass = null;

        // Player Name Field
        nameLabel = new JLabel("Enter your name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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

        // Add components
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(nameLabel);
        add(nameField);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(classLabel);
        add(classPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startButton);

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
     * Displays the player creation panel as a modal dialog.
     * Blocks input to other windows until the dialog is closed.
     */
    public void showModal() {
        dialog.setModal(true);
        dialog.setVisible(true);
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

        radioButton.addActionListener(new ActionListener() {
            /**
             * Sets the selected class to the one associated with this button
             * and updates the class description pane accordingly.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedClass = className;
                updateClassDescription(className);
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
}
