package game.gui;

import game.engine.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * A modal dialog that prompts the user to select the number of players
 * and the grid size (rows and columns) before starting the game.
 * Displays a resizable background image with a transparent input panel.
 */
public class StartingScreenGUI extends JDialog {

    // Data Members
    private int selectedPlayers = -1;
    private int selectedRows = 10;
    private int selectedCols = 10;
    private ImageIcon backgroundImage;
    private final JLabel backgroundLabel;

    // Methods
    /**
     * Private constructor for the StartingScreenGUI dialog.
     * Initializes UI components including the background image,
     * player count spinner, grid size spinners, and confirmation button.
     *
     * @param owner the parent Frame of this dialog (can be null)
     */
    private StartingScreenGUI(Frame owner) {
        super(owner, "Select Number of Players", true); // Modal dialog
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Load and display the background image
        backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/Main photo.png")));
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new GridBagLayout()); // Allow centered overlay panel
        setContentPane(backgroundLabel);

        // Force initial resize for correct scaling
        SwingUtilities.invokeLater(this::resizeBackgroundImage);

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

        // === Main content panel ===
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(350, 150));
        contentPanel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Choose Players", 0, 0, null, Color.WHITE));
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // === Player selection ===
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerPanel.setOpaque(false); // Transparent background

        // Label for the player selection
        JLabel playerLabel = new JLabel("How many players?");
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Same font size as rows and columns

        // Spinner for player selection
        JSpinner playerSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        playerSpinner.setFont(new Font("Arial", Font.BOLD, 16)); // Same font size as rows and columns

        // Add label and spinner to the panel
        playerPanel.add(playerLabel);
        playerPanel.add(playerSpinner);

        // Add player panel to content panel
        contentPanel.add(playerPanel);
        contentPanel.add(Box.createVerticalStrut(10)); // Added vertical space

        // === Grid row selection ===
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rowPanel.setOpaque(false); // Transparent background
        JLabel rowLabel = new JLabel("Rows:");
        rowLabel.setForeground(Color.WHITE);
        rowLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Same font size as player spinner
        JSpinner rowSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 100, 1));
        rowSpinner.setFont(new Font("Arial", Font.BOLD, 16)); // Same font size as player spinner
        rowPanel.add(rowLabel);
        rowPanel.add(rowSpinner);

        contentPanel.add(rowPanel);
        contentPanel.add(Box.createVerticalStrut(10)); // Added vertical space

        // === Grid column selection ===
        JPanel colPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        colPanel.setOpaque(false); // Transparent background
        JLabel colLabel = new JLabel("Columns:");
        colLabel.setForeground(Color.WHITE);
        colLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Same font size as player spinner
        JSpinner colSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 100, 1));
        colSpinner.setFont(new Font("Arial", Font.BOLD, 16)); // Same font size as player spinner
        colPanel.add(colLabel);
        colPanel.add(colSpinner);

        contentPanel.add(colPanel);
        contentPanel.add(Box.createVerticalStrut(10)); // Added vertical space


        // === Confirm Button ===
        JButton confirmButton = new JButton("Start Game");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.addActionListener(e -> {
            selectedPlayers = (int) playerSpinner.getValue();
            selectedRows = (int) rowSpinner.getValue();
            selectedCols = (int) colSpinner.getValue();
            dispose(); // Close the dialog
        });

        contentPanel.add(confirmButton);

        // Add content panel to center of background label
        backgroundLabel.add(contentPanel);
    }

    /**
     * Displays the dialog and blocks until the user makes a selection or closes the window.
     *
     * @return number of players selected (1–4), or -1 if the dialog was closed
     */
    public static GameSettings askForSettings() {
        StartingScreenGUI dialog = new StartingScreenGUI(null);
        dialog.setVisible(true); // Blocks until user closes or confirms

        // Return a new GameSettings object with all selected values
        return new GameSettings(dialog.selectedPlayers, dialog.selectedRows, dialog.selectedCols);
    }

    /**
     * Returns the number of rows selected by the user.
     * Only valid if askForPlayers() returns a value >= 1.
     */
    public int getSelectedRows() {
        return selectedRows;
    }

    /**
     * Returns the number of columns selected by the user.
     * Only valid if askForPlayers() returns a value >= 1.
     */
    public int getSelectedCols() {
        return selectedCols;
    }

    /**
     * Rescales and updates the background image to fit the current size of the dialog.
     * Reloads from the original image each time to avoid progressive quality loss.
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
     * Entry point for testing the dialog independently.
     * Prints the selected number of players or a cancellation message to the console.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        StartingScreenGUI gui = new StartingScreenGUI(null);
        gui.setVisible(true);

        if (gui.selectedPlayers != -1) {
            System.out.println("Players: " + gui.selectedPlayers);
            System.out.println("Rows: " + gui.selectedRows);
            System.out.println("Cols: " + gui.selectedCols);
        } else {
            System.out.println("User cancelled.");
        }
    }
}
