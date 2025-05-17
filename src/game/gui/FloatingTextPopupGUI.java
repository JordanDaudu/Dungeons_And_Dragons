package game.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;

/**
 * A GUI component that displays a floating text popup animation.
 * The text fades and moves upward over time, then disappears.
 */
public class FloatingTextPopupGUI extends JComponent {

    // Data Members
    private final String text;
    private final Color color;
    private final int fontSize;

    // Methods
    /**
     * Constructs a new FloatingTextPopupGUI with the specified text, color, and font size.
     *
     * @param text     the text to display
     * @param color    the color of the text
     * @param fontSize the size of the font
     */
    public FloatingTextPopupGUI(String text, Color color, int fontSize) {
        this.text = text;
        this.color = color;
        this.fontSize = fontSize;
        setPreferredSize(new Dimension(200, 50));
        setLayout(null); // Uses absolute positioning allows free movement of components
    }

    /**
     * Displays the floating text popup in the specified parent panel.
     * The text appears, floats upward while fading out, and is then removed.
     *
     * @param parentPanel the panel on which to display the floating popup
     */
    public void showFloatingTextPopup(JPanel parentPanel) {
        // Creating the label with the given text and styling
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        textLabel.setForeground(color);
        textLabel.setSize(parentPanel.getWidth(), parentPanel.getHeight());
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setOpaque(false);


        parentPanel.setLayout(null); // Ensure parent uses absolute positioning so we can move label freely
        parentPanel.add(textLabel, 0); // Adding the label to the parent panel, on top
        textLabel.setLocation(parentPanel.getWidth() / 4, parentPanel.getHeight() / 4); // Initial position

        animateFloatingText(textLabel, parentPanel, 20);
    }

    /**
     * Displays the floating text popup on the content pane of a JFrame.
     *
     * @param frame the JFrame to show the floating text on
     */
    public void showFloatingTextPopup(JFrame frame) {
        Container contentPane = frame.getContentPane();

        // Create a transparent overlay panel for the floating text
        JPanel overlayPanel = new JPanel(null); // Use null layout for absolute positioning
        overlayPanel.setOpaque(false); // Let the background show through
        overlayPanel.setSize(contentPane.getSize());
        overlayPanel.setLocation(0, 0);

        // Add the overlay to the content pane
        contentPane.setLayout(null); // Needed to position the overlay
        contentPane.add(overlayPanel, 0); // Add it at the top of the Z-order

        // Create and position the label
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        textLabel.setForeground(color);

        Dimension labelSize = textLabel.getPreferredSize();
        textLabel.setSize(labelSize);
        textLabel.setLocation(
                (overlayPanel.getWidth() - labelSize.width) / 2,
                overlayPanel.getHeight() / 4
        );

        overlayPanel.add(textLabel);
        overlayPanel.repaint();

        // Start the animation
        animateFloatingText(textLabel, overlayPanel, 60);
    }

    /**
     * Animates the floating label by moving it upward and reducing its opacity until it disappears.
     *
     * @param textLabel   the JLabel to animate
     * @param parentPanel the panel containing the label
     * @param totalSteps  the number of animation frames (controls duration/smoothness)
     */
    private void animateFloatingText(JLabel textLabel, JPanel parentPanel, final int totalSteps) {
        // totalSteps = Animation frames
        Timer timer = new Timer(30, null); // Fires every 30 milliseconds
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            currentStep[0]++;
            float alpha = Math.max(1.0f - (currentStep[0] / (float) totalSteps), 0f);

            // Moving the label upwards
            textLabel.setLocation(textLabel.getX(), textLabel.getY() - 1);

            // Adjusting color transparency dynamically
            textLabel.setForeground(new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    (int) (alpha * 255) // Apply alpha to transparency
            ));

            // Stop the animation and clean up once done
            if (currentStep[0] >= totalSteps) {
                parentPanel.remove(textLabel);
                parentPanel.repaint();
                ((Timer) e.getSource()).stop();
            }
        });

        timer.start();
    }
}
