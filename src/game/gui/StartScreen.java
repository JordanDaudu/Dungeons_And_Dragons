package game.gui;

import game.engine.ScreenAction;
import game.engine.ScreenListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class StartScreen extends JPanel {
    private JDialog dialog;
    private JTextField nameField;
    private JRadioButton warriorButton, archerButton, mageButton;
    private ImageIcon warriorIcon, archerIcon, mageIcon;
    private ButtonGroup classGroup;
    private JButton startButton;
    private String playerName;
    private String selectedClass;
    private JLabel nameLabel, classLabel;
    private JTextPane classDescriptionPane;

    private ScreenListener listener;

    public StartScreen(ScreenListener listener) {
        this.listener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        playerName = null;
        selectedClass = null;

        // Player Name Field
        nameLabel = new JLabel("Enter your name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField = new JTextField(20);
        nameField.setMaximumSize(nameField.getPreferredSize());
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Class Selection Label
        classLabel = new JLabel("Choose your class:");
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load icons
        warriorIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/warrior.png")));
        archerIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/archer.png")));
        mageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/mage.png")));

        // Scale icons
        warriorIcon = scaleIcon(warriorIcon, 100, 100);
        archerIcon = scaleIcon(archerIcon, 100, 100);
        mageIcon = scaleIcon(mageIcon, 100, 100);

        // Create radio buttons
        warriorButton = createClassRadioButton("Warrior", warriorIcon);
        archerButton = createClassRadioButton("Archer", archerIcon);
        mageButton = createClassRadioButton("Mage", mageIcon);

        // Group buttons
        classGroup = new ButtonGroup();
        classGroup.add(warriorButton);
        classGroup.add(archerButton);
        classGroup.add(mageButton);

        // Panel for class buttons
        JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        classPanel.add(warriorButton);
        classPanel.add(archerButton);
        classPanel.add(mageButton);

        // Class Description Panel
        classDescriptionPane = new JTextPane();
        classDescriptionPane.setEditable(false);
        classDescriptionPane.setContentType("text/html");
        classDescriptionPane.setBackground(getBackground());
        classDescriptionPane.setText("<html><body style='font-family:sans-serif; font-size:14px;'>Select a class to see its description.</body></html>");
        classDescriptionPane.setMaximumSize(new Dimension(500, 100));

        // Start Button
        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText();
                if (selectedClass != null && !playerName.trim().isEmpty()) {
                    System.out.println("Player Name: " + playerName);
                    System.out.println("Chosen Class: " + selectedClass);
                    listener.onAction(ScreenAction.START_GAME, playerName, selectedClass);
                    dialog.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(StartScreen.this, "Please enter your name and select a class.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add components
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(nameLabel);
        add(nameField);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(classLabel);
        add(classPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(classDescriptionPane);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startButton);

        // Setup Dialog
        dialog = new JDialog();
        dialog.setTitle("Character Creation");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.add(this);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("StartScreen was closed without starting the game.");
                // Optionally:
                System.exit(0); // Exit the game
            }
        });
    }

    public void showModal() {
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private JRadioButton createClassRadioButton(final String className, ImageIcon classImage) {
        JRadioButton radioButton = new JRadioButton(className);
        radioButton.setIcon(classImage);
        radioButton.setHorizontalTextPosition(SwingConstants.CENTER);
        radioButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        radioButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        radioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedClass = className;
                updateClassDescription(className);
            }
        });
        return radioButton;
    }

    private void updateClassDescription(String className) {
        String description = "";
        switch (className) {
            case "Warrior":
                description = "<b><u>Warrior</u></b><br>" +
                        "A melee-focused fighter skilled in close-range physical combat.<br>" +
                        "Excels in defense and durability.";
                break;
            case "Archer":
                description = "<b><u>Archer</u></b><br>" +
                        "A ranged combatant who excels at striking from afar with physical attacks.<br>" +
                        "Boasts high accuracy, making them less likely to miss.";
                break;
            case "Mage":
                description = "<b><u>Mage</u></b><br>" +
                        "A master of magic who uses powerful ranged spells to defeat enemies.<br>" +
                        "Specializes in high-power elemental attacks.";
                break;
        }
        classDescriptionPane.setText("<html><body style='font-family:sans-serif; font-size:14px;'>" + description + "</body></html>");
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
