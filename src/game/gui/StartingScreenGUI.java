package game.gui;

import game.engine.GameSettings;
import game.engine.SoundManager;
import game.logging.GameLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * A modal dialog that prompts the user to select the number of players
 * and the grid size (rows and columns) before starting the game.
 * Displays a resizable background image with a transparent input panel.
 * Grid size is acceptable up to 50x50.
 */
public class StartingScreenGUI extends JDialog {

    // Data Members
    private int selectedPlayers = -1;
    private int selectedRows = 10;
    private int selectedCols = 10;
    private ImageIcon backgroundImage;
    private JLabel backgroundLabel;
    private JSpinner playerSpinner, rowSpinner, colSpinner;
    private JButton confirmButton;

    // Methods
    /**
     * Constructs the modal dialog and initializes its layout and components.
     *
     * @param owner the parent frame
     */
    private StartingScreenGUI(Frame owner) {
        super(owner, "Select Number of Players", true); // Modal dialog
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents();   // Initialize components
        layoutComponents();  // Set up layout
        resizeBackgroundImage();  // Resize background immediately after showing the dialog
        attachListeners();  // Attach listeners
        getRootPane().setDefaultButton(confirmButton);  // Enter trigger the Start Game button
    }

    /**
     * Shows the dialog and returns a GameSettings object
     * containing the values selected by the user.
     *
     * @return a GameSettings instance based on user input
     */
    public static GameSettings askForSettings() {
        StartingScreenGUI dialog = new StartingScreenGUI(null);
        dialog.setVisible(true); // Blocks until user closes or confirms
        GameSettings settings = new GameSettings(dialog.selectedPlayers, dialog.selectedRows, dialog.selectedCols);
        logStartingScreen(settings);

        // Return a new GameSettings object with all selected values
        return settings;
    }

    /**
     * Logs the game settings selected by the user to the game logger.
     *
     * @param settings the game settings to log
     */
    private static void logStartingScreen(GameSettings settings) {
        String message = "Game initialized with " +
                settings.getPlayers() + " player(s), " +
                settings.getRows() + " rows and " +
                settings.getCols() + " columns.";
        GameLogger.getInstance().log(message);
    }
    /**
     * Initializes GUI components, including spinners and background.
     */
    private void initComponents() {
        backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/Main photo.png")));
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new GridBagLayout());
        setContentPane(backgroundLabel);

        // Initialize Spinners
        playerSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        rowSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 50, 1));
        colSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 50, 1));
    }

    /**
     * Lays out the GUI components using nested panels.
     */
    private void layoutComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setPreferredSize(new Dimension(350, 200));
        contentPanel.setBackground(new Color(0, 0, 0, 150));
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Game Setup", 0, 0, null, Color.WHITE));

        // === Player selection ===
        JPanel playerSelectionPanel = createPlayerSelectionPanel();

        // === Row and Column selection ===
        JPanel rowColPanel = createRowColSelectionPanel();

        // === Confirm Button ===
        JButton confirmButton = createConfirmButton();

        // === Adding everything to contentPanel ===
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(playerSelectionPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(rowColPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(confirmButton);
        contentPanel.add(Box.createVerticalStrut(10));

        // Add content panel to center of background label
        backgroundLabel.add(contentPanel);
    }

    /**
     * Creates a panel for selecting the number of players.
     *
     * @return the player selection JPanel
     */
    private JPanel createPlayerSelectionPanel() {
        JPanel playerSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerSelectionPanel.setOpaque(false);

        JLabel playerLabel = new JLabel("How many players?");
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        playerSelectionPanel.add(playerLabel);
        playerSelectionPanel.add(playerSpinner);
        playerSelectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return playerSelectionPanel;
    }

    /**
     * Creates a panel for selecting grid rows and columns.
     *
     * @return the row and column selection JPanel
     */
    private JPanel createRowColSelectionPanel() {
        JPanel rowColPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rowColPanel.setOpaque(false);

        JLabel rowLabel = new JLabel("Rows:");
        rowLabel.setForeground(Color.WHITE);
        rowLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rowColPanel.add(rowLabel);
        rowColPanel.add(rowSpinner);

        JLabel colLabel = new JLabel("Columns:");
        colLabel.setForeground(Color.WHITE);
        colLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rowColPanel.add(Box.createHorizontalStrut(20)); // Space between row and col
        rowColPanel.add(colLabel);
        rowColPanel.add(colSpinner);
        rowColPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return rowColPanel;
    }

    /**
     * Creates the confirm/start button.
     *
     * @return the confirm JButton
     */
    private JButton createConfirmButton() {
        confirmButton = new JButton("Start Game");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        return confirmButton;
    }

    /**
     * Rescales and updates the background image based on current dialog size.
     */
    private void resizeBackgroundImage() {
        if (backgroundImage == null) return;

        int width = getWidth();
        int height = getHeight();

        Image originalImage = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/images/Main photo.png"))).getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        backgroundImage = new ImageIcon(scaledImage);
        backgroundLabel.setIcon(backgroundImage);
    }

    /**
     * Attaches event listeners to handle resizing, button click, and closing.
     */
    private void attachListeners() {
        // Handle window closing: exit app completely if user closes this dialog
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Resize background image smoothly when the dialog is resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeBackgroundImage();
            }
        });

        // Confirm Button ActionListener
        confirmButton.addActionListener(e -> {
            SoundManager.playEffect("clickSound");
            selectedPlayers = (int) playerSpinner.getValue();
            selectedRows = (int) rowSpinner.getValue();
            selectedCols = (int) colSpinner.getValue();
            dispose(); // Close the dialog
        });
    }
}
