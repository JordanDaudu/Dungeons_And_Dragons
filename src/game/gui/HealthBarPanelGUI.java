package game.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A graphical panel that displays a health bar using a Swing {@link JProgressBar}.
 * The bar changes color based on the current health percentage:
 * <ul>
 *     <li>Red for health below 30%</li>
 *     <li>Yellow for health between 30% and 69%</li>
 *     <li>Green for health 70% and above</li>
 * </ul>
 * The health bar also displays the current and maximum health as text.
 */
public class HealthBarPanelGUI extends JPanel {

    // Data Members
    private final JProgressBar healthBar;

    // Methods
    /**
     * Constructs a HealthBarPanelGUI with the specified current and maximum health.
     *
     * @param currentHealth the player's current health
     * @param maxHealth     the player's maximum health
     */
    public HealthBarPanelGUI(int currentHealth, int maxHealth) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        healthBar = new JProgressBar(0, maxHealth);
        healthBar.setValue(currentHealth);
        healthBar.setString(currentHealth + "/" + maxHealth);
        healthBar.setStringPainted(true);
        updateHealthColor();
        add(healthBar, BorderLayout.CENTER);
    }

    /**
     * Updates the health bar's color based on the current health percentage.
     * <ul>
     *     <li>&lt; 30% <: Red</li>
     *     <li>&lt; 30%-70%: Yellow</li>
     *     <li>&ge; 70% >: Green</li>
     * </ul>
     */
    private void updateHealthColor() {
        int health = healthBar.getValue();
        int max = healthBar.getMaximum();
        float percent = (float) health / max;

        if (percent < 0.3f) {
            healthBar.setForeground(Color.RED);
        } else if (percent < 0.7f) {
            healthBar.setForeground(Color.YELLOW);
        } else {
            healthBar.setForeground(new Color(0, 128, 0));
        }
    }

    /**
     * Updates the displayed health value and the bar's color accordingly.
     *
     * @param currentHealth the new current health to display
     */
    public void updateHealth(int currentHealth) {
        healthBar.setValue(currentHealth);
        healthBar.setString(currentHealth + "/" + healthBar.getMaximum());
        updateHealthColor();
    }
}

