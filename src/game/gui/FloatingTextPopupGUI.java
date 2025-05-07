package game.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;

public class FloatingTextPopupGUI extends JComponent {

    private final String text;
    private final Color color;
    private final int fontSize;

    public FloatingTextPopupGUI(String text, Color color, int fontSize) {
        this.text = text;
        this.color = color;
        this.fontSize = fontSize;
        setPreferredSize(new Dimension(200, 50));
        setLayout(null); // Use absolute positioning
    }

    public void showFloatingTextPopup(JPanel parentPanel) {
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        textLabel.setForeground(color);
        textLabel.setSize(parentPanel.getWidth(), parentPanel.getHeight());
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        textLabel.setOpaque(false);

        parentPanel.setLayout(null);
        parentPanel.add(textLabel, 0);
        textLabel.setLocation(parentPanel.getWidth() / 4, parentPanel.getHeight() / 4);

        Timer timer = new Timer(30, null);
        final int totalSteps = 20;
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            currentStep[0]++;
            float alpha = Math.max(1.0f - (currentStep[0] / (float) totalSteps), 0f);

            // Move the label upwards
            textLabel.setLocation(textLabel.getX(), textLabel.getY() - 1);

            // Adjust color transparency dynamically
            textLabel.setForeground(new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    (int) (alpha * 255) // Apply alpha to transparency
            ));

            if (currentStep[0] >= totalSteps) {
                parentPanel.remove(textLabel);
                parentPanel.repaint();
                ((Timer) e.getSource()).stop();
            }
        });

        timer.start();
    }
}
