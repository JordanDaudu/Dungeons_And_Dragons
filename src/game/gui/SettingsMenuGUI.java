package game.gui;

import game.engine.GameController;
import game.engine.GameWorld;
import game.engine.SoundManager;
import game.logging.GameLogger;
import game.memento.GameWorldMemento;
import game.memento.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
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
    private static boolean lastSelectedHPBar = true;
    private JCheckBox showPlayerInformationCheckbox;
    private static boolean showPlayerInformation;
    private static boolean lastSelectedPlayerInformation = true; // default starting
    private JPanel buttonPanel;
    private JButton viewControlsButton;
    private JButton backButton;
    private JButton quitButton;
    private JButton saveButton;
    private JButton loadButton;
    private final SaveManager manager = SaveManager.getInstance();

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

        GameController.pauseEnemyTasks();
        GameController.pauseManagerEvent();
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
        musicSlider = new JSlider(0, 100, (int) (GameWorld.getInstance().getGameSettings().getMusicVolume() * 100));
        musicSlider.setMajorTickSpacing(25);
        musicSlider.setPaintTicks(true);
        musicSlider.setPaintLabels(true);

        sfxPanel = new JPanel(new BorderLayout(10, 10));
        sfxLabel = new JLabel("SFX Volume:");
        sfxSlider = new JSlider(0, 100, (int) (GameWorld.getInstance().getGameSettings().getSFXVolume() * 100));
        sfxSlider.setMajorTickSpacing(25);
        sfxSlider.setPaintTicks(true);
        sfxSlider.setPaintLabels(true);
        colorThemePanel = new JPanel(new BorderLayout(10, 10));
        colorThemeLabel = new JLabel("Color Tile Background:");
        colorThemeComboBox = new JComboBox<>(TileColorBackgroundTheme.values());
        colorThemeComboBox.setSelectedItem(GameWorld.getInstance().getGameSettings().getSelectedTheme());

        checkBoxesPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        showHPBarCheckbox = new JCheckBox("Show HP Bar On Map", showHPBar);
        showHPBarCheckbox.setSelected(GameWorld.getInstance().getGameSettings().getShowHPBar());

        showPlayerInformationCheckbox = new JCheckBox("Show Player Information", showPlayerInformation);
        showPlayerInformationCheckbox.setSelected(GameWorld.getInstance().getGameSettings().getShowPlayerInformation());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        viewControlsButton = new JButton("View Controls");
        saveButton = new JButton("Save Game");
        loadButton = new JButton("Load Game");
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
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(viewControlsButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(quitButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(loadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Attaches listeners to UI components to respond to user input,
     * such as changing volume, theme selection, checkbox toggles,
     * and button clicks.
     */
    private void attachListeners() {
        musicSlider.addChangeListener(e -> {
            float volume = musicSlider.getValue() / 100f;
            SoundManager.setMusicVolume(volume);
            GameWorld.getInstance().getGameSettings().setMusicVolume(volume);
        });
        sfxSlider.addChangeListener(e -> {
            float volume = sfxSlider.getValue() / 100f;
            SoundManager.setSFXVolume(volume);
            GameWorld.getInstance().getGameSettings().setSfxVolume(volume);
        });

        colorThemeComboBox.addActionListener(e -> {
            SoundManager.playEffect("clickSound");
            TileColorBackgroundTheme selectedColorTheme = (TileColorBackgroundTheme) colorThemeComboBox.getSelectedItem();
            if (getParent() instanceof GameMapGUI gameMap) {
                lastSelectedTheme = selectedColorTheme; // Save selection
                GameWorld.getInstance().getGameSettings().setSelectedTheme(selectedColorTheme);
                gameMap.setTileBackgroundTheme(selectedColorTheme);
            }
        });

        showHPBarCheckbox.addActionListener(e -> {
            SoundManager.playEffect("clickSound");
            showHPBar = showHPBarCheckbox.isSelected();
            if (getParent() instanceof GameMapGUI gameMap) {
                lastSelectedHPBar = showHPBarCheckbox.isSelected();
                GameWorld.getInstance().getGameSettings().setShowHPBar(showHPBar);
                gameMap.setShowHPBar(showHPBar);
            }
        });

        showPlayerInformationCheckbox.addActionListener(e -> {
            SoundManager.playEffect("clickSound");
            showPlayerInformation = showPlayerInformationCheckbox.isSelected();
            if (getParent() instanceof GameMapGUI gameMap) {
                lastSelectedPlayerInformation = showPlayerInformationCheckbox.isSelected();
                GameWorld.getInstance().getGameSettings().setShowPlayerInformation(showPlayerInformation);
                gameMap.toggleSidePanels(showPlayerInformation);
            }
        });

        viewControlsButton.addActionListener(e -> {
            SoundManager.playEffect("openingSound");
            new ControlsDialogGUI((JFrame) getParent()).setVisible(true);
        });
        backButton.addActionListener(e -> {
            SoundManager.playEffect("closingSound");
            dispose();
            GameController.resumeEnemyTasks();
            GameController.resumeManagerEvent();
        });
        quitButton.addActionListener(e -> {
            GameLogger.getInstance().log("Quitting game...");
            System.exit(0);
        });

        saveButton.addActionListener(e -> {
            SoundManager.playEffect("clickSound");

            try {
                GameWorldMemento memento = GameWorld.getInstance().createMemento();
                manager.save(memento);
                JOptionPane.showMessageDialog(this, "Game saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
                GameLogger.getInstance().log("Saving game...");
            }
            catch (CloneNotSupportedException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save game: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        loadButton.addActionListener(e -> {
            SoundManager.playEffect("openingSound");

            Queue<GameWorldMemento> saves = manager.getSaveSlots();
            int totalSlots = saves.size();

            if (totalSlots == 0) {
                JOptionPane.showMessageDialog(this, "No saved games available.", "Load", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<GameWorldMemento> saveList = new ArrayList<>(saves);

            String[] slotOptions = new String[saveList.size()];
            for (int i = 0; i < saveList.size(); i++) {
                // Reverse order: show newest first
                GameWorldMemento m = saveList.get(saveList.size() - 1 - i);
                slotOptions[i] = "Slot " + (i + 1) + " @ " + m.getFormattedTimestamp();
            }

            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a save slot to load:",
                    "Load Game",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    slotOptions,
                    slotOptions[0]
            );

            if (selected != null) {
                int selectedIndex = -1;
                for (int j = 0; j < slotOptions.length; j++) {
                    if (slotOptions[j].equals(selected)) {
                        selectedIndex = j;
                        break;
                    }
                }
                try {
                    // Load from memento
                    GameWorldMemento memento = manager.loadMemento(selectedIndex);
                    if (memento == null) {
                        JOptionPane.showMessageDialog(this, "Selected save slot is empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    GameWorld.getInstance().loadFromMemento(memento);
                    GameLogger.getInstance().log("Loading game...");

                    // Close settings window
                    Window window = SwingUtilities.getWindowAncestor(SettingsMenuGUI.this);
                    if (window != null) window.dispose();

                } catch (IndexOutOfBoundsException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid save slot selected.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (CloneNotSupportedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Set custom close operation
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                SoundManager.playEffect("closingSound");
                GameController.resumeEnemyTasks();
                GameController.resumeManagerEvent();
                dispose(); // Closes the window manually
            }
        });
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
                SoundManager.playEffect("closingSound");
                dispose();
                GameController.resumeEnemyTasks();
                GameController.resumeManagerEvent();
            }
        });
    }
}