package game.gui;

import game.characters.PlayerCharacter;
import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for end-game dialogs (e.g., victory or defeat screens).
 * Displays a title, final scores for all players, and a background image with music.
 */
public abstract class GameEndDialogGUI extends JDialog {

    // Data Members
    private JLabel titleLabel;
    private JPanel summaryScorePanel;
    private JScrollPane summaryScrollPane;
    private JButton exitButton;
    private final JPanel contentPanel;
    private JPanel buttonPanel;

    // Methods
    /**
     * Constructs the end-game dialog with a background image, title, final scores, and exit button.
     *
     * @param parent  the parent frame from which the dialog is launched
     * @param players the list of player characters to summarize in the dialog
     */
    public GameEndDialogGUI(JFrame parent, List<PlayerCharacter> players) {
        super(parent, "", true);

        Image backgroundImage = loadBackgroundImage(getBackgroundImagePath());

        contentPanel = (backgroundImage != null) ? new BackgroundPanel(backgroundImage) : createFallbackPanel();
        contentPanel.setLayout(new BorderLayout(15, 15));
        setContentPane(contentPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(parent);

        SoundManager.playMusic(getMusicTrack(), false);

        setupTitleLabel();
        setupSummaryArea(players);
        setupExitButton();
    }

    /**
     * Displays the dialog to the user.
     */
    public void showDialog() {
        setVisible(true);
    }

    /**
     * Sets up and adds the title label to the dialog using the string from {@link #getDialogTitle()}.
     */
    private void setupTitleLabel() {
        titleLabel = new JLabel(getDialogTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
    }

    /**
     * Sets up and adds a scrollable summary area showing final scores of all players.
     *
     * @param players list of player characters to display, sorted by score
     */
    private void setupSummaryArea(List<PlayerCharacter> players) {
        players.sort(Comparator.comparingInt(PlayerCharacter::getTreasurePoints).reversed());

        summaryScorePanel = new JPanel();
        summaryScorePanel.setOpaque(false);
        summaryScorePanel.setLayout(new GridLayout(players.size() + 1, 1, 10, 5));
        summaryScorePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Final Scores", 0, 0, new Font("Arial", Font.BOLD, 20), Color.WHITE));
        summaryScorePanel.setBackground(new Color(0, 0, 0, 150));
        summaryScorePanel.setPreferredSize(new Dimension(350, players.size() * 40 + 60));

        int rank = 1;
        for (PlayerCharacter p : players) {
            JLabel label = new JLabel(String.format("%d. %-12s - %3d pts", rank++, p.getName(), p.getTreasurePoints()), SwingConstants.CENTER);
            label.setFont(new Font("Monospaced", Font.BOLD, 18));
            label.setForeground(Color.WHITE);
            summaryScorePanel.add(label);
        }

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(summaryScorePanel);

        // Making it scrollable
        summaryScrollPane = new JScrollPane(wrapperPanel);
        summaryScrollPane.setOpaque(false);
        summaryScrollPane.getViewport().setOpaque(false);
        summaryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        summaryScrollPane.setPreferredSize(new Dimension(400, 250)); // Adjust as needed

        contentPanel.add(summaryScrollPane, BorderLayout.CENTER);

    }

    /**
     * Sets up and adds an "Exit Game" button to the bottom of the dialog.
     * Exits the application when clicked.
     */
    private void setupExitButton() {
        exitButton = new JButton("Exit Game");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setForeground(new Color(255, 255, 255));
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(160, 50));
        exitButton.setContentAreaFilled(false);
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(exitButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Attempts to load the background image from the given resource path.
     *
     * @param path the image path returned by {@link #getBackgroundImagePath()}
     * @return the loaded image, or {@code null} if loading failed
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
     * Creates a fallback panel to use in case the background image cannot be loaded.
     *
     * @return a plain dark gray panel
     */
    private JPanel createFallbackPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        return panel;
    }

    /**
     * Returns the dialog title to display.
     *
     * @return the title string, potentially with HTML formatting
     */
    protected abstract String getDialogTitle();

    /**
     * Returns the path to the background image resource.
     *
     * @return the image path string
     */
    protected abstract String getBackgroundImagePath();

    /**
     * Returns the name of the music track to play.
     *
     * @return the music track name
     */
    protected abstract String getMusicTrack();

    /**
     * A panel that paints a background image stretched to fill the component area.
     */
    private static class BackgroundPanel extends JPanel {

        // Data Members
        private final Image backgroundImage;

        // Methods
        /**
         * Constructs a background panel using the given image.
         *
         * @param image the background image to display
         */
        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        /**
         * Paints the background image to fill the entire panel.
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

