package game.gui;

import game.combat.MagicElement;
import game.core.StatBalanceValidator;
import game.logging.GameLogger;

import javax.swing.*;
import java.awt.*;

/**
 * A dialog window that lets the player customize stats for their selected class.
 * The available options depend on whether the player is a Warrior, Archer, or Mage.
 * The stats must be balanced before the player can confirm and continue.
 */
public class PlayerCustomizationGUI extends JDialog {

    // Data Members
    private final String playerClass;  // Store class passed from outside

    private JPanel parameterPanel;

    private JSpinner healthSpinner;
    private JSpinner powerSpinner;
    private JSpinner defenseSpinner;

    private JSpinner accuracySpinner;

    private JComboBox<MagicElement> elementSelector;

    private JButton confirmButton;

    private boolean confirmed = false;

    private int healthModResult;
    private int powerModResult;
    private int defenseModResult;
    private double accuracyModResult;
    private MagicElement selectedElementResult;


    /**
     * Constructs a new PlayerCustomizationGUI dialog for the specified player class.
     *
     * @param owner       the parent window that owns this dialog
     * @param playerClass the selected class ("Warrior", "Archer", or "Mage")
     */
    public PlayerCustomizationGUI(Window owner, String playerClass) {
        super(owner);
        this.playerClass = playerClass;
        setModal(true);
        setTitle("Player Class Customization - " + playerClass);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                JOptionPane.showMessageDialog(
                        PlayerCustomizationGUI.this,
                        "You must confirm your customization to continue.",
                        "Action Required",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        initComponents();
    }

    /**
     * Initializes and lays out the GUI components for the dialog,
     * including spinners, combo boxes, labels, and the confirm button.
     */
    private void initComponents() {
        setLayout(new BorderLayout());

        // Remove topPanel with classSelector

        // Center panel: parameter customization
        parameterPanel = new JPanel(new GridBagLayout());
        add(parameterPanel, BorderLayout.CENTER);

        // Initialize spinners and controls
        healthSpinner = createIntSpinner(0, -2, 3, 1);
        powerSpinner = createIntSpinner(0, -2, 3, 1);
        defenseSpinner = createIntSpinner(0, -2, 3, 1);

        accuracySpinner = new JSpinner(new SpinnerNumberModel(0.00, -0.02, 0.03, 0.01));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(accuracySpinner, "0.00");
        accuracySpinner.setEditor(editor);

        elementSelector = new JComboBox<>(MagicElement.values());

        // Update parameters panel once based on passed class
        updateParameterPanel();

        // Confirm button at bottom
        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> onConfirm());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Making Enter key press confirmButton
        getRootPane().setDefaultButton(confirmButton);
    }

    /**
     * Creates a new integer spinner for stat modification, enforcing a fixed range and step size.
     *
     * @param initial the initial value
     * @param min     the minimum allowed value
     * @param max     the maximum allowed value
     * @param step    the increment step size
     * @return a configured {@code JSpinner} for integer input
     */
    private JSpinner createIntSpinner(int initial, int min, int max, int step) {
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, step);
        return new JSpinner(model);
    }

    /**
     * Updates the parameter panel with class-specific customization options.
     * Adds the appropriate components based on the selected player class.
     */
    private void updateParameterPanel() {
        parameterPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Health Modifier
        parameterPanel.add(new JLabel("Health Modifier (-2 to 3):"), gbc);
        gbc.gridx = 1;
        parameterPanel.add(healthSpinner, gbc);

        // Power Modifier
        gbc.gridx = 0;
        gbc.gridy++;
        parameterPanel.add(new JLabel("Power Modifier (-2 to 3):"), gbc);
        gbc.gridx = 1;
        parameterPanel.add(powerSpinner, gbc);

        switch (playerClass) {
            case "Warrior" -> {
                gbc.gridx = 0;
                gbc.gridy++;
                parameterPanel.add(new JLabel("Defense Modifier (-2 to 3):"), gbc);
                gbc.gridx = 1;
                parameterPanel.add(defenseSpinner, gbc);
            }
            case "Archer" -> {
                gbc.gridx = 0;
                gbc.gridy++;
                parameterPanel.add(new JLabel("Accuracy Modifier (-0.02 to 0.03):"), gbc);
                gbc.gridx = 1;
                parameterPanel.add(accuracySpinner, gbc);
            }
            case "Mage" -> {
                gbc.gridx = 0;
                gbc.gridy++;
                parameterPanel.add(new JLabel("Element:"), gbc);
                gbc.gridx = 1;
                parameterPanel.add(elementSelector, gbc);
            }
        }

        parameterPanel.revalidate();
        parameterPanel.repaint();
    }

    /**
     * Handles the confirm button action. Validates the selected stats against
     * class rules using {@code StatBalanceValidator}. If valid, stores the selected
     * values and closes the dialog.
     */
    private void onConfirm() {
        int healthMod = (Integer) healthSpinner.getValue();
        int powerMod = (Integer) powerSpinner.getValue();

        boolean valid = switch (playerClass) {
            case "Warrior" -> {
                int defenseMod = (Integer) defenseSpinner.getValue();
                yield StatBalanceValidator.validateWarrior(healthMod, powerMod, defenseMod);
            }
            case "Archer" -> {
                double accuracyMod = (Double) accuracySpinner.getValue();
                yield StatBalanceValidator.validateArcher(healthMod, powerMod, accuracyMod);
            }
            case "Mage" -> StatBalanceValidator.validateMage(healthMod, powerMod);
            default -> false;
        };

        if (valid) {
            // Save results
            confirmed = true;
            healthModResult = healthMod;
            powerModResult = powerMod;

            switch (playerClass) {
                case "Warrior" -> defenseModResult = (Integer) defenseSpinner.getValue();
                case "Archer" -> accuracyModResult = (Double) accuracySpinner.getValue();
                case "Mage" -> selectedElementResult = (MagicElement) elementSelector.getSelectedItem();
            }

            JOptionPane.showMessageDialog(this, "Stats are balanced! Ready to create player.");
            GameLogger.getInstance().log("Stats were successfully chosen and balanced");
            dispose();  // Close dialog and unblock caller
        } else {
            JOptionPane.showMessageDialog(this,
                    "The total stat modifications must sum to zero.\nPlease adjust your stats to balance them.",
                    "Invalid Stats", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns whether the player has confirmed their customization.
     *
     * @return {@code true} if confirmed, {@code false} otherwise
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the selected health stat modifier.
     *
     * @return the health modifier value
     */
    public int getHealthMod() {
        return healthModResult;
    }

    /**
     * Returns the selected power stat modifier.
     *
     * @return the power modifier value
     */
    public int getPowerMod() {
        return powerModResult;
    }

    /**
     * Returns the selected defense stat modifier (for Warriors).
     *
     * @return the defense modifier value
     */
    public int getDefenseMod() {
        return defenseModResult;
    }

    /**
     * Returns the selected accuracy stat modifier (for Archers).
     *
     * @return the accuracy modifier value
     */
    public double getAccuracyMod() {
        return accuracyModResult;
    }

    /**
     * Returns the selected magic element (for Mages).
     *
     * @return the selected {@code MagicElement}
     */
    public MagicElement getSelectedElement() {
        return selectedElementResult;
    }
}
