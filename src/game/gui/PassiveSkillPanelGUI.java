package game.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A GUI panel for displaying information about a passive skill in the game.
 * This includes a title, icon, subtitle (skill name), and a description.
 *
 * The panel uses a styled layout and consistent fonts/colors to fit the game's theme.
 */
public class PassiveSkillPanelGUI extends JPanel {

    // Data Members
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color SUBTITLE_COLOR = new Color(255, 200, 0);
    private static final Color DESCRIPTION_COLOR = Color.LIGHT_GRAY;
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font DESCRIPTION_FONT = new Font("Monospaced", Font.PLAIN, 16);

    private final JLabel iconLabel;
    private final JLabel subtitleLabel;
    private final JTextArea descriptionArea;

    private JLabel titleLabel;
    private JSeparator lineSeparator;

    // Methods
    /**
     * Constructs a PassiveSkillPanelGUI with the given icon, subtitle, and description.
     *
     * @param icon        the image icon representing the passive skill
     * @param subtitle    the subtitle or name of the passive skill
     * @param description the description text of what the skill does
     */
    public PassiveSkillPanelGUI(ImageIcon icon, String subtitle, String description) {
        setBackground(new Color(50, 50, 50)); // Warm dark gray background
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Initialize all components
        iconLabel = new JLabel();
        subtitleLabel = new JLabel();
        descriptionArea = new JTextArea();

        initComponents(icon, subtitle, description);   // Setup visual properties
        layoutComponents();                            // Add components to panel
    }

    /**
     * Updates the icon, subtitle, and description displayed in the panel.
     *
     * @param newIcon        the new icon to display
     * @param newSubtitle    the new subtitle or skill name
     * @param newDescription the new description text
     */
    public void updateContent(ImageIcon newIcon, String newSubtitle, String newDescription) {
        iconLabel.setIcon(newIcon);
        subtitleLabel.setText(newSubtitle);
        descriptionArea.setText(newDescription);
        revalidate();
        repaint();
    }

    /**
     * Initializes all components with the given icon, subtitle, and description.
     *
     * @param icon        the image icon for the skill
     * @param subtitle    the name or label of the skill
     * @param description the detailed description of the skill
     */
    private void initComponents(ImageIcon icon, String subtitle, String description) {
        // Title label
        titleLabel = new JLabel("PASSIVE SKILL");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TITLE_COLOR);

        // Icon
        iconLabel.setIcon(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Subtitle
        subtitleLabel.setText(subtitle);
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(SUBTITLE_COLOR);

        // Description area
        descriptionArea.setText(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setFont(DESCRIPTION_FONT);
        descriptionArea.setForeground(DESCRIPTION_COLOR);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Line separator
        lineSeparator = new JSeparator();
        lineSeparator.setForeground(Color.GRAY);
    }

    /**
     * Arranges the components visually in the panel using layout managers.
     * Divides the layout into three sections: title (NORTH), center (icon and subtitle),
     * and bottom (separator and description).
     */
    private void layoutComponents() {
        // Top section – Title
        add(titleLabel, BorderLayout.NORTH);

        // Center section – Icon and subtitle in horizontal layout
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.add(iconLabel);
        centerPanel.add(subtitleLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom section – Line + Description area
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(lineSeparator, BorderLayout.NORTH);
        southPanel.add(descriptionArea, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
}
