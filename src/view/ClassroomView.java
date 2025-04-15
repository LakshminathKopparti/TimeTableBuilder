package view;

import controller.ClassroomController;
import model.Classroom;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class ClassroomView extends JPanel {
    private ClassroomController classroomController;

    private JTable classroomTable;
    private DefaultTableModel tableModel;
    private JTextField roomNumberField, capacityField;
    private JCheckBox projectorCheckBox, acCheckBox;
    private JTextArea facilitiesArea;
    private JButton addButton, updateButton, deleteButton, clearButton;

    public ClassroomView(ClassroomController classroomController) {
        this.classroomController = classroomController;
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Classroom Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Room Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Room Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        roomNumberField = new JTextField(20);
        formPanel.add(roomNumberField, gbc);

        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Capacity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        capacityField = new JTextField(20);
        formPanel.add(capacityField, gbc);

        // Facilities checkboxes
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Facilities:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPanel facilitiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        projectorCheckBox = new JCheckBox("Projector");
        acCheckBox = new JCheckBox("AC");
        facilitiesPanel.add(projectorCheckBox);
        facilitiesPanel.add(acCheckBox);
        formPanel.add(facilitiesPanel, gbc);

        // Additional Facilities
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Additional Facilities:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 2;
        facilitiesArea = new JTextArea(3, 20);
        facilitiesArea.setLineWrap(true);
        JScrollPane facilitiesScrollPane = new JScrollPane(facilitiesArea);
        formPanel.add(facilitiesScrollPane, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columnNames = {"Room Number", "Capacity", "Projector", "AC", "Additional Facilities"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        classroomTable = new JTable(tableModel);
        classroomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classroomTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(classroomTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addClassroom());
        updateButton.addActionListener(e -> updateClassroom());
        deleteButton.addActionListener(e -> deleteClassroom());
        clearButton.addActionListener(e -> clearForm());

        classroomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = classroomTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }

    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);

        // Get all classrooms and add to table
        List<Classroom> classrooms = classroomController.getAllClassrooms();
        for (Classroom classroom : classrooms) {
            String additionalFacilities = "";
            if (classroom.getAdditionalFacilities() != null) {
                additionalFacilities = String.join(", ", classroom.getAdditionalFacilities());
            }

            Object[] rowData = {
                    classroom.getRoomNumber(),
                    classroom.getCapacity(),
                    classroom.hasProjector(),
                    classroom.hasAC(),
                    additionalFacilities
            };

            tableModel.addRow(rowData);
        }
    }

    private void populateFormFromTable(int row) {
        roomNumberField.setText(tableModel.getValueAt(row, 0).toString());
        capacityField.setText(tableModel.getValueAt(row, 1).toString());
        projectorCheckBox.setSelected((Boolean) tableModel.getValueAt(row, 2));
        acCheckBox.setSelected((Boolean) tableModel.getValueAt(row, 3));
        facilitiesArea.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        roomNumberField.setText("");
        capacityField.setText("");
        projectorCheckBox.setSelected(false);
        acCheckBox.setSelected(false);
        facilitiesArea.setText("");
        classroomTable.clearSelection();
    }

    private void addClassroom() {
        try {
            String roomNumber = roomNumberField.getText().trim();
            if (roomNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room number cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (classroomController.classroomExists(roomNumber)) {
                JOptionPane.showMessageDialog(this, "Classroom with this room number already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacity = Integer.parseInt(capacityField.getText().trim());
            boolean hasProjector = projectorCheckBox.isSelected();
            boolean hasAC = acCheckBox.isSelected();

            String[] additionalFacilities = null;
            if (!facilitiesArea.getText().trim().isEmpty()) {
                additionalFacilities = facilitiesArea.getText().trim().split(",\\s*");
            }

            Classroom classroom = new Classroom(roomNumber, capacity, hasProjector, hasAC, additionalFacilities);

            if (classroomController.addClassroom(classroom)) {
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Classroom added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add classroom", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClassroom() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String roomNumber = roomNumberField.getText().trim();
            String originalRoomNumber = tableModel.getValueAt(selectedRow, 0).toString();

            if (roomNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room number cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If room number is changed, check if the new one already exists
            if (!roomNumber.equals(originalRoomNumber) && classroomController.classroomExists(roomNumber)) {
                JOptionPane.showMessageDialog(this, "Classroom with this room number already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacity = Integer.parseInt(capacityField.getText().trim());
            boolean hasProjector = projectorCheckBox.isSelected();
            boolean hasAC = acCheckBox.isSelected();

            String[] additionalFacilities = null;
            if (!facilitiesArea.getText().trim().isEmpty()) {
                additionalFacilities = facilitiesArea.getText().trim().split(",\\s*");
            }

            Classroom classroom = new Classroom(roomNumber, capacity, hasProjector, hasAC, additionalFacilities);

            // If room number is changed, delete the old one and add the new one
            if (!roomNumber.equals(originalRoomNumber)) {
                classroomController.deleteClassroom(originalRoomNumber);
                classroomController.addClassroom(classroom);
            } else {
                classroomController.updateClassroom(classroom);
            }

            refreshData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Classroom updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteClassroom() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String roomNumber = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete classroom " + roomNumber + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (classroomController.deleteClassroom(roomNumber)) {
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Classroom deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete classroom", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
