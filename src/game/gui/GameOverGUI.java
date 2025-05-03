package game.gui;

import game.characters.PlayerCharacter;
import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A modal dialog that displays when the game ends, showing each player's treasure points.
 * Uses a pixel-art background if available, otherwise falls back to clean styling.
 */
public class GameOverGUI extends JDialog {

    // Data Members
    private JLabel titleLabel;
    private JTextArea summaryTextArea;
    private JScrollPane summaryScrollPane;
    private JButton exitButton;
    private final JPanel contentPanel;
    private JPanel buttonPanel;

    // Methods
    /**
     * Constructs the Game Over dialog, displaying the final player rankings.
     *
     * @param parent  the parent JFrame to center the dialog relative to
     * @param players the list of players to display, ranked by treasure points
     */
    public GameOverGUI(JFrame parent, List<PlayerCharacter> players) {
        super(parent, "Game Over", true);  // true = modal

        Image backgroundImage = loadBackgroundImage("/images/gameover.jpeg");

        contentPanel = (backgroundImage != null) ? new BackgroundPanel(backgroundImage) : createFallbackPanel();
        contentPanel.setLayout(new BorderLayout(15, 15));
        setContentPane(contentPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(parent);  // Center on screen

        SoundManager.playMusic("gameOver", false);  // Optional dramatic ending

        setupTitleLabel();
        setupSummaryArea(players);
        setupExitButton();
    }

    /**
     * Displays the Game Over dialog as a modal window.
     */
    public void showDialog() {
        setVisible(true);
    }

    /**
     * Initializes and adds the title label to the dialog.
     * This label displays "★ GAME OVER ★" centered at the top.
     */
    private void setupTitleLabel() {
        titleLabel = new JLabel("★ GAME OVER ★", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
    }

    /**
     * Prepares the scrollable summary area that lists players and their treasure points.
     * The list is sorted in descending order of treasure points.
     *
     * @param players the list of players to rank and display
     */
    private void setupSummaryArea(List<PlayerCharacter> players) {
        players.sort(Comparator.comparingInt(PlayerCharacter::getTreasurePoints).reversed());

        summaryTextArea = new JTextArea();
        summaryTextArea.setOpaque(false);
        summaryTextArea.setEditable(false);
        summaryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        summaryTextArea.setForeground(new Color(255, 255, 255));
        summaryTextArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        int rank = 1;
        for (PlayerCharacter p : players) {
            summaryTextArea.append(String.format("%d. %-12s  -  %3d pts%n", rank++, p.getName(), p.getTreasurePoints()));
        }

        summaryScrollPane = new JScrollPane(summaryTextArea);
        summaryScrollPane.setOpaque(false);
        summaryScrollPane.getViewport().setOpaque(false);
        summaryScrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(summaryScrollPane, BorderLayout.CENTER);
    }

    /**
     * Configures the exit button which allows the player to close the game.
     * Adds styling and behavior for immediate termination on click.
     */
    private void setupExitButton() {
        exitButton = new JButton("Exit Game");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setForeground(new Color(255, 255, 255)); // red text
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(160, 50));
        exitButton.setContentAreaFilled(false);  // keep it transparent
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));  // red border
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(exitButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads a background image from the given path within the classpath.
     *
     * @param path the relative path to the image resource
     * @return the Image object if successfully loaded; otherwise, null
     */
    private Image loadBackgroundImage(String path) {
        try {
            return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a simple fallback panel with a solid dark background.
     * Used when the background image is unavailable.
     *
     * @return the fallback JPanel
     */
    private JPanel createFallbackPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        return panel;
    }

    /**
     * Inner class to paint the background image if present.
     */
    private static class BackgroundPanel extends JPanel {

        // Data Members
        private final Image backgroundImage;

        // Methods
        /**
         * Constructs the background panel with a specified image.
         *
         * @param image the image to use as the background
         */
        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        /**
         * Paints the background image scaled to fit the panel.
         *
         * @param g the Graphics context in which to paint
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
