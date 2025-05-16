package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HealthBarPanelGUI extends JPanel {

    private final JProgressBar healthBar;
    private int currentHealth;
    private final Timer animationTimer;
    private int animationTarget;

    // Pulse effect
    private Timer pulseTimer;
    private float pulsePhase = 0f;
    private boolean pulsing = false;

    // Shake effect
    private int shakeOffset = 0;
    private Timer shakeTimer;

    // Healing glow sweep
    private float glowX = -1f;
    private Timer glowTimer;

    public HealthBarPanelGUI(int currentHealth, int maxHealth) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(false);

        this.currentHealth = currentHealth;

        healthBar = new JProgressBar(0, maxHealth) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int arc = 20;

                // Apply shake offset
                g2.translate(shakeOffset, 0);

                // === Background Layer ===
                g2.setColor(new Color(60, 60, 60));
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                // === Fill Layer ===
                int fillWidth = (int) (width * getPercentComplete());
                g2.setColor(getForeground());
                g2.fillRoundRect(0, 0, fillWidth, height, arc, arc);

                // === Healing Glow Sweep ===
                if (glowX >= 0) {
                    GradientPaint glow = new GradientPaint(
                            glowX - 30, 0, new Color(255, 255, 255, 0),
                            glowX, 0, new Color(255, 255, 255, 120),
                            false
                    );
                    g2.setPaint(glow);
                    g2.fillRoundRect(0, 0, width, height, arc, arc);
                }

                // === Inner Glow Border (subtle highlight) ===
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, width - 3, height - 3, arc, arc);

                // Beveled Borders for 3D effect
                g2.setColor(new Color(100, 100, 100, 100));
                g2.drawRoundRect(1, 1, width - 3, height - 3, arc, arc);
                g2.setColor(new Color(20, 20, 20, 180));
                g2.drawRoundRect(2, 2, width - 5, height - 5, arc, arc);

                // Gradient outer border
                GradientPaint borderGradient = new GradientPaint(
                        0, 0, new Color(90, 90, 90),
                        0, height, new Color(30, 30, 30)
                );
                g2.setPaint(borderGradient);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

                // Outer glow halo
                g2.setColor(new Color(255, 0, 0, 20));
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(-2, -2, width + 3, height + 3, arc + 4, arc + 4);

                // Text
                String text = getString();
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);

                g2.dispose();
            }
        };

        healthBar.setValue(currentHealth);
        healthBar.setString(currentHealth + "/" + maxHealth);
        healthBar.setStringPainted(true);
        healthBar.setBorderPainted(false);
        healthBar.setPreferredSize(new Dimension(250, 25));
        updateHealthColor();

        add(healthBar, BorderLayout.CENTER);

        animationTimer = new Timer(15, e -> animateHealthStep());
    }

    private void updateHealthColor() {
        int max = healthBar.getMaximum();
        float percent = (float) currentHealth / max;

        if (percent < 0.3f) {
            startPulse();
        } else {
            stopPulse();
            if (percent < 0.7f) {
                healthBar.setForeground(new Color(255, 215, 0));
            } else {
                healthBar.setForeground(new Color(50, 205, 50));
            }
        }
    }

    private void animateHealthStep() {
        int displayed = healthBar.getValue();
        if (displayed == animationTarget) {
            animationTimer.stop();
            return;
        }

        int step = (animationTarget > displayed) ? 1 : -1;
        healthBar.setValue(displayed + step);
        healthBar.setString((displayed + step) + "/" + healthBar.getMaximum());
        currentHealth = displayed + step;
        updateHealthColor();
        repaint();
    }

    public void updateHealth(int newHealth, boolean animate) {
        newHealth = Math.max(0, Math.min(newHealth, healthBar.getMaximum()));

        if (!animate) {
            // Instantly set current health and repaint, skip all animations
            currentHealth = newHealth;
            animationTarget = newHealth;
            healthBar.setValue(newHealth);
            healthBar.setString(newHealth + "/" + healthBar.getMaximum());
            updateHealthColor();
            repaint();
            return;
        }

        // Animate only if there's a change
        if (newHealth < currentHealth) {
            triggerShake();
        } else if (newHealth > currentHealth) {
            triggerGlow();
        }

        animationTarget = newHealth;
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void updateHealth(int newHealth) {
        updateHealth(newHealth, true); // Default behavior: animate
    }

    private void startPulse() {
        if (pulsing) return;
        pulsing = true;

        pulseTimer = new Timer(40, e -> {
            pulsePhase += 0.1f;
            float pulse = 0.5f + 0.5f * (float) Math.sin(pulsePhase);

            int r = (int) (120 + 125 * pulse);  // R: 120–245
            int g = (int) (30 + 10 * pulse);   // G: 30–40
            int b = (int) (30 + 10 * pulse);   // B: 30–40

            healthBar.setForeground(new Color(r, g, b));
            healthBar.repaint();
        });
        pulseTimer.start();
    }

    private void stopPulse() {
        pulsing = false;
        if (pulseTimer != null) {
            pulseTimer.stop();
            pulseTimer = null;
        }
    }

    private void triggerShake() {
        if (shakeTimer != null && shakeTimer.isRunning()) return;

        shakeOffset = 0;
        shakeTimer = new Timer(20, new AbstractAction() {
            int ticks = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                shakeOffset = (ticks % 2 == 0) ? 4 : -4;
                healthBar.repaint();
                ticks++;
                if (ticks >= 6) {
                    shakeTimer.stop();
                    shakeOffset = 0;
                    healthBar.repaint();
                }
            }
        });
        shakeTimer.start();
    }

    private void triggerGlow() {
        glowX = -50f;
        if (glowTimer != null && glowTimer.isRunning()) glowTimer.stop();

        glowTimer = new Timer(15, e -> {
            glowX += 8;
            if (glowX > healthBar.getWidth() + 50) {
                glowX = -1f;
                glowTimer.stop();
            }
            healthBar.repaint();
        });
        glowTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Health Bar Test");
            HealthBarPanelGUI panel = new HealthBarPanelGUI(80, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);

            JPanel buttons = new JPanel();
            JButton damage = new JButton("Take 10 Damage");
            JButton heal = new JButton("Heal 10");

            damage.addActionListener(e -> panel.updateHealth(panel.currentHealth - 10));
            heal.addActionListener(e -> panel.updateHealth(panel.currentHealth + 10));

            buttons.add(damage);
            buttons.add(heal);

            frame.add(buttons, BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}