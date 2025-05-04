package game.gui;

import javax.swing.*;
import java.awt.*;

public class HealthBarPanelGUI extends JPanel {
    private final JProgressBar healthBar;

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

    private void updateHealthColor() {
        int health = healthBar.getValue();
        int max = healthBar.getMaximum();
        float percent = (float) health / max;

        if (percent <= 0.25f) {
            healthBar.setForeground(Color.RED);
        } else if (percent <= 0.49f) {
            healthBar.setForeground(Color.YELLOW);
        } else {
            healthBar.setForeground(new Color(0, 128, 0)); // Green
        }
    }

    public void updateHealth(int currentHealth) {
        healthBar.setValue(currentHealth);
        healthBar.setString(currentHealth + "/" + healthBar.getMaximum());
        updateHealthColor();
    }
}

