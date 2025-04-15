package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class PreferenceView extends JDialog {
    private JCheckBox enableAutoSaveCheckBox;
    private JCheckBox enableConflictWarningsCheckBox;
    private JCheckBox strictPolicyEnforcementCheckBox;
    private JCheckBox darkModeCheckBox;
    private JComboBox<String> timeFormatComboBox;

    private Preferences prefs;
    private JButton saveButton, cancelButton, resetButton;

    public PreferenceView(Frame parent) {
        super(parent, "Preferences", true);

        // Get preferences
        prefs = Preferences.userNodeForPackage(MainView.class);

        initializeUI();
        loadPreferences();

        // Set dialog properties
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Main panel with GridBagLayout for form layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Application Settings Section
        mainPanel.add(createSectionLabel("Application Settings"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 20, 2, 5);

        // Auto Save
        enableAutoSaveCheckBox = new JCheckBox("Enable Auto Save");
        mainPanel.add(enableAutoSaveCheckBox, gbc);

        gbc.gridy++;

        // Time Format
        mainPanel.add(new JLabel("Time Format:"), gbc);

        gbc.gridx = 1;
        timeFormatComboBox = new JComboBox<>(new String[] {"12-hour (hh:mm a)", "24-hour (HH:mm)"});
        mainPanel.add(timeFormatComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        // Dark Mode
        darkModeCheckBox = new JCheckBox("Dark Mode (requires restart)");
        mainPanel.add(darkModeCheckBox, gbc);

        // Timetable Settings Section
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);

        mainPanel.add(createSectionLabel("Timetable Settings"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 20, 2, 5);

        // Conflict Warnings
        enableConflictWarningsCheckBox = new JCheckBox("Enable Conflict Warnings");
        mainPanel.add(enableConflictWarningsCheckBox, gbc);

        gbc.gridy++;

        // Policy Enforcement
        strictPolicyEnforcementCheckBox = new JCheckBox("Strict BITS Policy Enforcement");
        mainPanel.add(strictPolicyEnforcementCheckBox, gbc);

        // Add main panel to dialog
        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        resetButton = new JButton("Reset to Defaults");

        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        saveButton.addActionListener(e -> savePreferences());
        cancelButton.addActionListener(e -> dispose());
        resetButton.addActionListener(e -> resetToDefaults());
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    private void loadPreferences() {
        enableAutoSaveCheckBox.setSelected(prefs.getBoolean("enableAutoSave", true));
        enableConflictWarningsCheckBox.setSelected(prefs.getBoolean("enableConflictWarnings", true));
        strictPolicyEnforcementCheckBox.setSelected(prefs.getBoolean("strictPolicyEnforcement", false));
        darkModeCheckBox.setSelected(prefs.getBoolean("darkMode", false));
        timeFormatComboBox.setSelectedIndex(prefs.getInt("timeFormat", 1)); // Default to 24-hour
    }

    private void savePreferences() {
        prefs.putBoolean("enableAutoSave", enableAutoSaveCheckBox.isSelected());
        prefs.putBoolean("enableConflictWarnings", enableConflictWarningsCheckBox.isSelected());
        prefs.putBoolean("strictPolicyEnforcement", strictPolicyEnforcementCheckBox.isSelected());
        prefs.putBoolean("darkMode", darkModeCheckBox.isSelected());
        prefs.putInt("timeFormat", timeFormatComboBox.getSelectedIndex());

        JOptionPane.showMessageDialog(this, "Preferences saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void resetToDefaults() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all preferences to default values?",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            enableAutoSaveCheckBox.setSelected(true);
            enableConflictWarningsCheckBox.setSelected(true);
            strictPolicyEnforcementCheckBox.setSelected(false);
            darkModeCheckBox.setSelected(false);
            timeFormatComboBox.setSelectedIndex(1); // 24-hour format
        }
    }
}
