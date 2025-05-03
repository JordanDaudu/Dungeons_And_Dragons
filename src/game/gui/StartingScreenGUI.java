package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * A modal dialog that prompts the user to select the number of players
 * before starting the game. Displays a resizable background image and a
 * spinner input for choosing between 1 and 4 players.
 */
public class StartingScreenGUI extends JDialog {

    // Data Members
    private int selectedPlayers = -1; // -1 means cancelled or closed
    private ImageIcon backgroundImage;
    private final JLabel backgroundLabel;

    // Methods
    /**
     * Private constructor for the StartingScreenGUI dialog.
     * Initializes UI components including the background image,
     * player count spinner, and confirmation button.
     *
     * @param owner the parent Frame of this dialog (can be null)
     */
    private StartingScreenGUI(Frame owner) {
        super(owner, "Select Number of Players", true); // Modal dialog (true here)
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Background image
        backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/Main photo.png")));
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new GridBagLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // Shutdown the system
            }
        });

        // Add component listener to resize the background image only once
        addComponentListener(new java.awt.event.ComponentAdapter() {
            /**
             * Called when the dialog is resized. Triggers a rescaling of the background image
             * to match the new dimensions of the window.
             *
             * @param e the component event containing resize details
             */
            public void componentResized(java.awt.event.ComponentEvent e) {
                resizeBackgroundImage();
            }
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(300, 150));
        contentPanel.setBackground(new Color(0, 0, 0, 150));
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Choose Players", 0, 0, null, Color.WHITE));
        contentPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel prompt = new JLabel("How many players?", SwingConstants.CENTER);
        prompt.setForeground(Color.WHITE);
        prompt.setFont(new Font("Arial", Font.BOLD, 18));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 4, 1);
        JSpinner playerSpinner = new JSpinner(spinnerModel);
        playerSpinner.setFont(new Font("Arial", Font.BOLD, 16));

        JButton confirmButton = new JButton("Start Game");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 16));
        confirmButton.addActionListener(e -> {
            selectedPlayers = (int) playerSpinner.getValue();
            dispose();
        });

        contentPanel.add(prompt);
        contentPanel.add(playerSpinner);
        contentPanel.add(confirmButton);

        backgroundLabel.add(contentPanel);
        setContentPane(backgroundLabel);
    }

    /**
     * Displays the dialog and blocks until the user selects a number
     * of players or closes the window. Returns the chosen player count.
     *
     * @return number of players (1–4), or -1 if the dialog was closed
     */
    public static int askForPlayers() {
        StartingScreenGUI dialog = new StartingScreenGUI(null);
        dialog.setVisible(true); // Blocks until user closes dialog
        return dialog.selectedPlayers;
    }

    /**
     * Rescales and updates the background image to fit the current size of the dialog.
     * Ensures that the image is scaled from the original resource to preserve quality.
     */
    private void resizeBackgroundImage() {
        if (backgroundImage == null) return;

        int width = getWidth();
        int height = getHeight();

        // Load the original image each time from the resource (instead of the previously scaled one)
        Image originalImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/Main photo.png"))).getImage();

        // Scale from the original image to prevent progressive quality loss
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // Update the background image and label
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
        int players = StartingScreenGUI.askForPlayers();
        if (players != -1) {
            System.out.println("User chose " + players + " players.");
        } else {
            System.out.println("User cancelled.");
        }
    }
}
