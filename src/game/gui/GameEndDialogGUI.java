package game.gui;

import game.characters.PlayerCharacter;
import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract class GameEndDialogGUI extends JDialog {

    private JLabel titleLabel;
    private JTextArea summaryTextArea;
    private JScrollPane summaryScrollPane;
    private JButton exitButton;
    private final JPanel contentPanel;
    private JPanel buttonPanel;

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

    public void showDialog() {
        setVisible(true);
    }

    private void setupTitleLabel() {
        titleLabel = new JLabel(getDialogTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
    }

    private void setupSummaryArea(List<PlayerCharacter> players) {
        players.sort(Comparator.comparingInt(PlayerCharacter::getTreasurePoints).reversed());

        JPanel scorePanel = new JPanel();
        scorePanel.setOpaque(false);
        scorePanel.setLayout(new GridLayout(players.size() + 1, 1, 10, 5));
        scorePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "Final Scores", 0, 0, new Font("Arial", Font.BOLD, 20), Color.WHITE));
        scorePanel.setBackground(new Color(0, 0, 0, 150));
        scorePanel.setPreferredSize(new Dimension(350, players.size() * 40 + 60));

        int rank = 1;
        for (PlayerCharacter p : players) {
            JLabel label = new JLabel(String.format("%d. %-12s - %3d pts", rank++, p.getName(), p.getTreasurePoints()), SwingConstants.CENTER);
            label.setFont(new Font("Monospaced", Font.BOLD, 18));
            label.setForeground(Color.WHITE);
            scorePanel.add(label);
        }

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(scorePanel);

        // Making it scrollable
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(400, 250)); // Adjust as needed

        contentPanel.add(scrollPane, BorderLayout.CENTER);

    }


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

    private Image loadBackgroundImage(String path) {
        try {
            return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
        return null;
    }

    private JPanel createFallbackPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        return panel;
    }

    protected abstract String getDialogTitle();
    protected abstract String getBackgroundImagePath();
    protected abstract String getMusicTrack();

    private static class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}

