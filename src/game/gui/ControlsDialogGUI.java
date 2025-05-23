package game.gui;

import game.engine.SoundManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A dialog window that displays the list of key bindings and controls for the game.
 * It shows corresponding icons, action names, and key/mouse triggers for user reference.
 */
public class ControlsDialogGUI extends JDialog {

    // Data Members
    private JPanel mainPanel;
    private JButton closeButton;
    private final Object[][] controls = {
            {"Move Up", "W / Left Click Tile Above", "arrow_upward"},
            {"Move Down", "S / Left Click Tile Below", "arrow_downward"},
            {"Move Left", "A / Left Click Tile Left", "arrow_back"},
            {"Move Right", "D / Left Click Tile Right", "arrow_forward"},
            {"Open Inventory", "E / Middle Click", "inventory"},
            {"Show Status", "Q", "info"},
            {"Open Settings", "Esc", "settings"},
            {"Interact with Item", "Left Click Item Tile", "mouseLeftClick"},
            {"Attack Enemy", "Left Click Enemy Tile", "mouseLeftClick"},
            {"Inspect Entity", "Right Click Entity Tile", "mouseRightClick"}
    };

    // Methods
    /**
     * Constructs the controls dialog window.
     *
     * @param parent the parent JFrame to center this dialog on
     */
    public ControlsDialogGUI(JFrame parent) {
        super(parent, "Game Controls", true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        initComponents();
        layoutComponents();
        attachListeners();
    }

    /**
     * Initializes the UI components, populating the control list and preparing the close button.
     */
    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Controls",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16)
        ));

        Color evenColor = new Color(245, 245, 245);
        Color oddColor = Color.WHITE;

        for (int i = 0; i < controls.length; i++) {
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(i % 2 == 0 ? evenColor : oddColor);
            rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel iconLabel = new JLabel(getIcon(controls[i][2].toString()));
            iconLabel.setPreferredSize(new Dimension(30, 30));
            rowPanel.add(iconLabel, BorderLayout.WEST);

            JLabel actionLabel = new JLabel(controls[i][0].toString());
            actionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JLabel keyLabel = new JLabel("<html>" + controls[i][1].toString() + "</html>", JLabel.RIGHT);
            keyLabel.setFont(new Font("Monospaced", Font.BOLD, 14));

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.add(actionLabel, BorderLayout.CENTER);
            textPanel.add(keyLabel, BorderLayout.EAST);

            rowPanel.add(textPanel, BorderLayout.CENTER);
            mainPanel.add(rowPanel);
        }

        closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(120, 30));
    }

    /**
     * Organizes the layout by adding title, control panel, and close button panel to the dialog.
     */
    private void layoutComponents() {
        JLabel titleLabel = new JLabel("Game Key Bindings", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Attaches listeners to interactive components like the close button and Escape key binding.
     */
    private void attachListeners() {
        closeButton.addActionListener(e -> {
            SoundManager.playEffect("closingSound");
            dispose();
        });
        setupEscapeKey();
    }

    /**
     * Registers the ESC key to close the dialog when pressed.
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
            }
        });
    }

    /**
     * Loads and scales an icon image based on its name.
     * Falls back to a default information icon if not found.
     *
     * @param name the name of the icon file (without path or extension)
     * @return the corresponding icon, or a default one if missing
     */
    private Icon getIcon(String name) {
        String path = "/icons/" + name + ".png";
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaled = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } else {
            return UIManager.getIcon("OptionPane.informationIcon");
        }
    }
}
