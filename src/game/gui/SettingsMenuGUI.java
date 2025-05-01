package game.gui;

import game.engine.SoundManager;

import javax.swing.*;
import java.awt.*;

public class SettingsMenuGUI extends JDialog {

    private JPanel slidersPanel;
    private JPanel musicPanel;
    private JPanel sfxPanel;
    private JLabel musicLabel;
    private JLabel sfxLabel;
    private JSlider musicSlider;
    private JSlider sfxSlider;
    private JPanel buttonPanel;
    private JButton backButton;
    private JButton quitButton;

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

    private void initComponents() {
        slidersPanel = new JPanel(new GridLayout(2, 1, 10, 10));

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

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        backButton = new JButton("Back");
        quitButton = new JButton("Quit Game");
    }

    private void layoutComponents() {
        musicPanel.add(musicLabel, BorderLayout.WEST);
        musicPanel.add(musicSlider, BorderLayout.CENTER);

        sfxPanel.add(sfxLabel, BorderLayout.WEST);
        sfxPanel.add(sfxSlider, BorderLayout.CENTER);

        slidersPanel.add(musicPanel);
        slidersPanel.add(sfxPanel);
        add(slidersPanel, BorderLayout.CENTER);

        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(backButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        musicSlider.addChangeListener(e -> SoundManager.setMusicVolume(musicSlider.getValue() / 100f));
        sfxSlider.addChangeListener(e -> SoundManager.setSFXVolume(sfxSlider.getValue() / 100f));

        backButton.addActionListener(e -> dispose());
        quitButton.addActionListener(e -> System.exit(0));
    }
}