package game.gui;

import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A settings dialog for adjusting audio levels, UI themes, and game preferences.
 * Allows the player to control music and sound effects volume, toggle UI elements,
 * change tile color themes, view controls, or quit the game.
 */
public class SettingsMenuGUI extends JDialog {

    // Data Members
    private JPanel mainPanel;
    private JPanel musicPanel;
    private JPanel sfxPanel;
    private JLabel musicLabel;
    private JLabel sfxLabel;
    private JSlider musicSlider;
    private JSlider sfxSlider;
    private JPanel colorThemePanel;
    private JLabel colorThemeLabel;
    private JPanel checkBoxesPanel;
    private JComboBox<TileColorBackgroundTheme> colorThemeComboBox;
    private static TileColorBackgroundTheme lastSelectedTheme = TileColorBackgroundTheme.CLEAR;
    private JCheckBox showHPBarCheckbox;
    private static boolean showHPBar = true;
    private static boolean lastSelectedHPBar = true; // default starting
    private JCheckBox showPlayerInformationCheckbox;
    private static boolean showPlayerInformation;
    private static boolean lastSelectedPlayerInformation = true; // default starting
    private JPanel buttonPanel;
    private JButton viewControlsButton;
    private JButton backButton;
    private JButton quitButton;

    // Methods
    /**
     * Creates a modal settings dialog attached to the given parent frame.
     * Initializes all components and layouts, and sets up event handling.
     *
     * @param parent the parent JFrame to which this dialog is attached
     */
    public SettingsMenuGUI(JFrame parent) {
        super(parent, "Settings", true);  // modal
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(parent);

        initComponents();
        layoutComponents();
        attachListeners();
        setupEscapeKey();
    }

    /**
     * Initializes all UI components used in the settings menu,
     * including sliders, checkboxes, combo boxes, and buttons.
     */
    private void initComponents() {
        mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));

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

        checkBoxesPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        showHPBarCheckbox = new JCheckBox("Show HP Bar On Map", showHPBar);
        showHPBarCheckbox.setSelected(lastSelectedHPBar);

        showPlayerInformationCheckbox = new JCheckBox("Show Player Information", showPlayerInformation);
        showPlayerInformationCheckbox.setSelected(lastSelectedPlayerInformation);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        viewControlsButton = new JButton("View Controls");
        backButton = new JButton("Back");
        quitButton = new JButton("Quit Game");
    }

    /**
     * Lays out the components visually using layout managers.
     * Groups panels and adds them to the dialog.
     */
    private void layoutComponents() {
        musicPanel.add(musicLabel, BorderLayout.WEST);
        musicPanel.add(musicSlider, BorderLayout.CENTER);

        sfxPanel.add(sfxLabel, BorderLayout.WEST);
        sfxPanel.add(sfxSlider, BorderLayout.CENTER);

        mainPanel.add(musicPanel);
        mainPanel.add(sfxPanel);
        add(mainPanel, BorderLayout.CENTER);

        colorThemePanel.add(colorThemeLabel, BorderLayout.WEST);
        colorThemePanel.add(colorThemeComboBox, BorderLayout.CENTER);
        mainPanel.add(colorThemePanel);

        checkBoxesPanel.add(showHPBarCheckbox);
        checkBoxesPanel.add(showPlayerInformationCheckbox);
        mainPanel.add(checkBoxesPanel);

        viewControlsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(viewControlsButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Attaches listeners to UI components to respond to user input,
     * such as changing volume, theme selection, checkbox toggles,
     * and button clicks.
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

        showHPBarCheckbox.addActionListener(e -> {
            showHPBar = showHPBarCheckbox.isSelected();
            if (getParent() instanceof GameMapGUI gameMap) {
                lastSelectedHPBar = showHPBarCheckbox.isSelected();
                gameMap.setShowHPBar(showHPBar);
            }
        });

        showPlayerInformationCheckbox.addActionListener(e -> {
            showPlayerInformation = showPlayerInformationCheckbox.isSelected();
            if (getParent() instanceof GameMapGUI gameMap) {
                lastSelectedPlayerInformation = showPlayerInformationCheckbox.isSelected();
                gameMap.toggleSidePanels(showPlayerInformation);
            }
        });

        viewControlsButton.addActionListener(e -> new ControlsDialogGUI((JFrame) getParent()).setVisible(true));
        backButton.addActionListener(e -> dispose());
        quitButton.addActionListener(e -> System.exit(0));
    }

    /**
     * Configures the Escape key to close the dialog when pressed.
     */
    private void setupEscapeKey() {
        JRootPane rootPane = getRootPane();
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}