package view;

import controller.ImportExportController;
import controller.TimeTableController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ExportView extends JDialog {
    private ImportExportController importExportController;
    private TimeTableController timeTableController;

    private JRadioButton csvFormatRadio, htmlFormatRadio, jsonFormatRadio;
    private JCheckBox includeHeadersCheckBox, includeTotalsCheckBox;
    private JTextField filePathField;
    private JButton browseButton, exportButton, cancelButton;

    public ExportView(Frame parent, TimeTableController timeTableController) {
        super(parent, "Export Timetable", true);
        this.timeTableController = timeTableController;
        this.importExportController = new ImportExportController();

        initializeUI();

        // Set dialog properties
        setSize(500, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Export Format Section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("<html><b>Export Format</b></html>"), gbc);

        // Format Radio Buttons
        ButtonGroup formatGroup = new ButtonGroup();

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 20, 2, 5);

        csvFormatRadio = new JRadioButton("CSV (Comma Separated Values)");
        csvFormatRadio.setSelected(true);
        formatGroup.add(csvFormatRadio);
        mainPanel.add(csvFormatRadio, gbc);

        gbc.gridy++;
        htmlFormatRadio = new JRadioButton("HTML (Web Page)");
        formatGroup.add(htmlFormatRadio);
        mainPanel.add(htmlFormatRadio, gbc);

        gbc.gridy++;
        jsonFormatRadio = new JRadioButton("JSON (Data Format)");
        formatGroup.add(jsonFormatRadio);
        mainPanel.add(jsonFormatRadio, gbc);

        // Options Section
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(new JLabel("<html><b>Export Options</b></html>"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 20, 2, 5);

        includeHeadersCheckBox = new JCheckBox("Include Headers");
        includeHeadersCheckBox.setSelected(true);
        mainPanel.add(includeHeadersCheckBox, gbc);

        gbc.gridy++;
        includeTotalsCheckBox = new JCheckBox("Include Summary Totals");
        mainPanel.add(includeTotalsCheckBox, gbc);

        // File Path Section
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(new JLabel("<html><b>Export Location</b></html>"), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2, 20, 2, 5);
        mainPanel.add(new JLabel("File Path:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        filePathField = new JTextField(20);
        mainPanel.add(filePathField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        browseButton = new JButton("Browse...");
        mainPanel.add(browseButton, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        exportButton = new JButton("Export");
        cancelButton = new JButton("Cancel");

        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        browseButton.addActionListener(e -> browseForFile());
        exportButton.addActionListener(e -> exportFile());
        cancelButton.addActionListener(e -> dispose());

        // Radio button listeners to enable/disable options based on format
        csvFormatRadio.addActionListener(e -> updateUIForFormat());
        htmlFormatRadio.addActionListener(e -> updateUIForFormat());
        jsonFormatRadio.addActionListener(e -> updateUIForFormat());
    }

    private void updateUIForFormat() {
        includeHeadersCheckBox.setEnabled(csvFormatRadio.isSelected());
        includeTotalsCheckBox.setEnabled(csvFormatRadio.isSelected() || htmlFormatRadio.isSelected());

        // Update file extension in path
        if (!filePathField.getText().isEmpty()) {
            String path = filePathField.getText();
            path = path.substring(0, path.lastIndexOf('.') > 0 ? path.lastIndexOf('.') : path.length());

            if (csvFormatRadio.isSelected()) {
                filePathField.setText(path + ".csv");
            } else if (htmlFormatRadio.isSelected()) {
                filePathField.setText(path + ".html");
            } else if (jsonFormatRadio.isSelected()) {
                filePathField.setText(path + ".json");
            }
        }
    }

    private void browseForFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Export Location");

        // Set file filter based on selected format
        if (csvFormatRadio.isSelected()) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        } else if (htmlFormatRadio.isSelected()) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("HTML Files (*.html)", "html"));
        } else if (jsonFormatRadio.isSelected()) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files (*.json)", "json"));
        }

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();

            // Add appropriate extension if not already present
            if (csvFormatRadio.isSelected() && !path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
            } else if (htmlFormatRadio.isSelected() && !path.toLowerCase().endsWith(".html")) {
                path += ".html";
            } else if (jsonFormatRadio.isSelected() && !path.toLowerCase().endsWith(".json")) {
                path += ".json";
            }

            filePathField.setText(path);
        }
    }

    private void exportFile() {
        String filePath = filePathField.getText().trim();

        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file path for export.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = false;

        if (csvFormatRadio.isSelected()) {
            success = importExportController.exportTimetableToCSV(timeTableController.getCurrentTimetable(), filePath);
        } else if (htmlFormatRadio.isSelected()) {
            success = importExportController.exportTimetableToHTML(timeTableController.getCurrentTimetable(), filePath);
        } else if (jsonFormatRadio.isSelected()) {
            success = importExportController.exportDataToJSON(
                    timeTableController.getCourses(),
                    timeTableController.getInstructors(),
                    timeTableController.getClassrooms(),
                    filePath);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Export completed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to export file. Please check the file path and try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
