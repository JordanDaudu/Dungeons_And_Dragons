package game.gui;

import game.combat.MagicElement;
import game.core.StatBalanceValidator;

import javax.swing.*;
import java.awt.*;

public class PlayerCustomizationGUI extends JDialog {
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


    // Constructor now takes class as argument
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

    private JSpinner createIntSpinner(int initial, int min, int max, int step) {
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, step);
        return new JSpinner(model);
    }

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
            dispose();  // Close dialog and unblock caller
        } else {
            JOptionPane.showMessageDialog(this,
                    "The total stat modifications must sum to zero.\nPlease adjust your stats to balance them.",
                    "Invalid Stats", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getHealthMod() {
        return healthModResult;
    }

    public int getPowerMod() {
        return powerModResult;
    }

    public int getDefenseMod() {
        return defenseModResult;
    }

    public double getAccuracyMod() {
        return accuracyModResult;
    }

    public MagicElement getSelectedElement() {
        return selectedElementResult;
    }
}
