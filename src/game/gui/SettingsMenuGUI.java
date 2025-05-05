package game.gui;

import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;

/**
 * A modal settings menu dialog for adjusting game audio preferences.
 * Provides sliders for modifying music and sound effects (SFX) volume,
 * along with buttons to return to the game or quit the application.
 */
public class SettingsMenuGUI extends JDialog {

    // Data Members
    private JPanel slidersPanel;
    private JPanel musicPanel;
    private JPanel sfxPanel;
    private JLabel musicLabel;
    private JLabel sfxLabel;
    private JSlider musicSlider;
    private JSlider sfxSlider;
    private JPanel colorThemePanel;
    private JLabel colorThemeLabel;
    private JComboBox<TileColorBackgroundTheme> colorThemeComboBox;
    private static TileColorBackgroundTheme lastSelectedTheme = TileColorBackgroundTheme.CLEAR;
    private JPanel buttonPanel;
    private JButton backButton;
    private JButton quitButton;
    // Methods
    /**
     * Constructs a new SettingsMenuGUI dialog with sliders for audio volume settings
     * and control buttons for navigation.
     *
     * @param parent the parent JFrame that owns this settings dialog
     */
    public SettingsMenuGUI(JFrame parent) {
        super(parent, "Settings", true);  // modal
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(parent);

        initComponents();
        layoutComponents();
        attachListeners();
    }

    /**
     * Initializes all UI components such as sliders, labels, and buttons.
     */
    private void initComponents() {
        slidersPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        musicPanel = new JPanel(new BorderLayout(10, 10));
        musicLabel = new JLabel("Music Volume:");
        musicSlider = new JSlider(0, 100, (int) (SoundManager.getMusicVolume() * 100));
        musicSlider.setMajorTickSpacing(25);
        musicSlider.setPaintTicks(true);
        musicSlider.setPaintLabels(true);

        sfxPanel = new JPanel(new BorderLayout(10, 10));
        sfxLabel = new JLabel("SFX Volume:");
        sfxSlider = new JSlider(0, 100, (int) (SoundManager.getSFXVolume() * 100));
        sfxSlider.setMajorTickSpacing(25);
        sfxSlider.setPaintTicks(true);
        sfxSlider.setPaintLabels(true);

        colorThemePanel = new JPanel(new BorderLayout(10, 10));
        colorThemeLabel = new JLabel("Color Tile Background:");
        colorThemeComboBox = new JComboBox<>(TileColorBackgroundTheme.values());
        colorThemeComboBox.setSelectedItem(lastSelectedTheme);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        backButton = new JButton("Back");
        quitButton = new JButton("Quit Game");
    }

    /**
     * Arranges all UI components within the dialog using appropriate layouts.
     */
    private void layoutComponents() {
        musicPanel.add(musicLabel, BorderLayout.WEST);
        musicPanel.add(musicSlider, BorderLayout.CENTER);

        sfxPanel.add(sfxLabel, BorderLayout.WEST);
        sfxPanel.add(sfxSlider, BorderLayout.CENTER);

        slidersPanel.add(musicPanel);
        slidersPanel.add(sfxPanel);
        add(slidersPanel, BorderLayout.CENTER);

        colorThemePanel.add(colorThemeLabel, BorderLayout.WEST);
        colorThemePanel.add(colorThemeComboBox, BorderLayout.CENTER);
        slidersPanel.add(colorThemePanel);

        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(backButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Attaches action and change listeners to UI components to handle user interaction.
     */
    private void attachListeners() {
        musicSlider.addChangeListener(e -> SoundManager.setMusicVolume(musicSlider.getValue() / 100f));
        sfxSlider.addChangeListener(e -> SoundManager.setSFXVolume(sfxSlider.getValue() / 100f));

        colorThemeComboBox.addActionListener(e -> {
            TileColorBackgroundTheme selectedColorTheme = (TileColorBackgroundTheme) colorThemeComboBox.getSelectedItem();
            if (getParent() instanceof GameMapGUI gameMap) {
                lastSelectedTheme = selectedColorTheme; // Save selection
                gameMap.setTileBackgroundTheme(selectedColorTheme);
            }
        });

        backButton.addActionListener(e -> dispose());
        quitButton.addActionListener(e -> System.exit(0));
    }
}