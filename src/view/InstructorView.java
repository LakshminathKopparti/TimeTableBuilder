package view;

import controller.CourseController;
import controller.InstructorController;
import model.Course;
import model.Instructor;
import model.TimeSlot;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstructorView extends JPanel {
    private InstructorController instructorController;
    private CourseController courseController;

    private JTable instructorTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, specializationField;
    private JList<String> coursesList;
    private DefaultListModel<String> coursesListModel;
    private JCheckBox preferredTimeSlotsCheckBox;
    private JButton addButton, updateButton, deleteButton, clearButton, selectCoursesButton, selectTimeSlotsButton;

    private List<String> selectedCourses = new ArrayList<>();
    private List<TimeSlot> selectedTimeSlots = new ArrayList<>();

    public InstructorView(InstructorController instructorController) {
        this.instructorController = instructorController;
        this.courseController = new CourseController(); // Create a course controller to get all courses
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Instructor Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        idField = new JTextField(20);
        formPanel.add(idField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Specialization
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Specialization:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        specializationField = new JTextField(20);
        formPanel.add(specializationField, gbc);

        // Courses
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Courses:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        coursesListModel = new DefaultListModel<>();
        coursesList = new JList<>(coursesListModel);
        coursesList.setVisibleRowCount(4);
        coursesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane coursesScrollPane = new JScrollPane(coursesList);
        formPanel.add(coursesScrollPane, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        selectCoursesButton = new JButton("Select Courses");
        formPanel.add(selectCoursesButton, gbc);

        // Preferred Time Slots
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        preferredTimeSlotsCheckBox = new JCheckBox("Has Preferred Time Slots");
        formPanel.add(preferredTimeSlotsCheckBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        selectTimeSlotsButton = new JButton("Select Time Slots");
        selectTimeSlotsButton.setEnabled(false);
        formPanel.add(selectTimeSlotsButton, gbc);

        preferredTimeSlotsCheckBox.addActionListener(e ->
                selectTimeSlotsButton.setEnabled(preferredTimeSlotsCheckBox.isSelected()));

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
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
        String[] columnNames = {"ID", "Name", "Specialization", "Courses"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        instructorTable = new JTable(tableModel);
        instructorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        instructorTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(instructorTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addInstructor());
        updateButton.addActionListener(e -> updateInstructor());
        deleteButton.addActionListener(e -> deleteInstructor());
        clearButton.addActionListener(e -> clearForm());
        selectCoursesButton.addActionListener(e -> selectCourses());
        selectTimeSlotsButton.addActionListener(e -> selectTimeSlots());

        instructorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = instructorTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }

    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);

        // Get all instructors and add to table
        List<Instructor> instructors = instructorController.getAllInstructors();
        for (Instructor instructor : instructors) {
            String courses = "";
            if (instructor.getCourses() != null) {
                courses = String.join(", ", instructor.getCourses());
            }

            Object[] rowData = {
                    instructor.getId(),
                    instructor.getName(),
                    instructor.getSpecialization(),
                    courses
            };

            tableModel.addRow(rowData);
        }
    }

    private void populateFormFromTable(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        specializationField.setText(tableModel.getValueAt(row, 2).toString());

        // Update courses list
        coursesListModel.clear();
        selectedCourses.clear();
        String coursesStr = tableModel.getValueAt(row, 3).toString();
        if (!coursesStr.isEmpty()) {
            String[] courses = coursesStr.split(", ");
            for (String course : courses) {
                coursesListModel.addElement(course);
                selectedCourses.add(course);
            }
        }

        // Check if instructor has preferred time slots
        String instructorId = tableModel.getValueAt(row, 0).toString();
        Instructor instructor = instructorController.getInstructorById(instructorId);
        if (instructor != null && instructor.getPreferredTimeSlots() != null) {
            preferredTimeSlotsCheckBox.setSelected(true);
            selectTimeSlotsButton.setEnabled(true);

            selectedTimeSlots.clear();
            for (TimeSlot slot : instructor.getPreferredTimeSlots()) {
                selectedTimeSlots.add(slot);
            }
        } else {
            preferredTimeSlotsCheckBox.setSelected(false);
            selectTimeSlotsButton.setEnabled(false);
            selectedTimeSlots.clear();
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        specializationField.setText("");
        coursesListModel.clear();
        selectedCourses.clear();
        preferredTimeSlotsCheckBox.setSelected(false);
        selectTimeSlotsButton.setEnabled(false);
        selectedTimeSlots.clear();
        instructorTable.clearSelection();
    }

    private void addInstructor() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (instructorController.instructorExists(id)) {
            JOptionPane.showMessageDialog(this, "Instructor with this ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Instructor instructor = new Instructor(id, name, specialization);

        // Set courses
        if (!selectedCourses.isEmpty()) {
            instructor.setCourses(selectedCourses.toArray(new String[0]));
        }

        // Set preferred time slots
        if (preferredTimeSlotsCheckBox.isSelected() && !selectedTimeSlots.isEmpty()) {
            instructor.setPreferredTimeSlots(selectedTimeSlots.toArray(new TimeSlot[0]));
        }

        if (instructorController.addInstructor(instructor)) {
            refreshData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Instructor added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add instructor", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = idField.getText().trim();
        String originalId = tableModel.getValueAt(selectedRow, 0).toString();
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID and name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If ID is changed, check if the new one already exists
        if (!id.equals(originalId) && instructorController.instructorExists(id)) {
            JOptionPane.showMessageDialog(this, "Instructor with this ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Instructor instructor = new Instructor(id, name, specialization);

        // Set courses
        if (!selectedCourses.isEmpty()) {
            instructor.setCourses(selectedCourses.toArray(new String[0]));
        }

        // Set preferred time slots
        if (preferredTimeSlotsCheckBox.isSelected() && !selectedTimeSlots.isEmpty()) {
            instructor.setPreferredTimeSlots(selectedTimeSlots.toArray(new TimeSlot[0]));
        }

        // If ID is changed, delete the old one and add the new one
        if (!id.equals(originalId)) {
            instructorController.deleteInstructor(originalId);
            instructorController.addInstructor(instructor);
        } else {
            instructorController.updateInstructor(instructor);
        }

        refreshData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Instructor updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete instructor " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (instructorController.deleteInstructor(id)) {
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Instructor deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete instructor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectCourses() {
        // Get all available courses
        List<Course> availableCourses = courseController.getAllCourses();
        if (availableCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available. Please add courses first.", "No Courses", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Courses", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Create course list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Course course : availableCourses) {
            listModel.addElement(course.getCourseCode() + ": " + course.getName());
        }

        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Pre-select current courses
        if (!selectedCourses.isEmpty()) {
            List<Integer> indicesToSelect = new ArrayList<>();
            for (int i = 0; i < listModel.getSize(); i++) {
                String courseItem = listModel.getElementAt(i);
                String courseCode = courseItem.split(":")[0].trim();

                if (selectedCourses.contains(courseCode)) {
                    indicesToSelect.add(i);
                }
            }

            int[] indices = new int[indicesToSelect.size()];
            for (int i = 0; i < indicesToSelect.size(); i++) {
                indices[i] = indicesToSelect.get(i);
            }

            list.setSelectedIndices(indices);
        }

        JScrollPane scrollPane = new JScrollPane(list);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            // Update selected courses
            selectedCourses.clear();
            coursesListModel.clear();

            for (int index : list.getSelectedIndices()) {
                String courseItem = listModel.getElementAt(index);
                String courseCode = courseItem.split(":")[0].trim();

                selectedCourses.add(courseCode);
                coursesListModel.addElement(courseCode);
            }

            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void selectTimeSlots() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Preferred Time Slots", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // Create time slot panel
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));

        // Create day panels
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getValue() >= 1 && day.getValue() <= 5) { // Monday to Friday
                JPanel dayPanel = new JPanel();
                dayPanel.setBorder(BorderFactory.createTitledBorder(day.toString()));
                dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));

                // Add time slots for this day
                for (int hour = 8; hour < 18; hour++) {
                    LocalTime startTime = LocalTime.of(hour, 0);
                    LocalTime endTime = LocalTime.of(hour + 1, 0);

                    TimeSlot timeSlot = new TimeSlot(day, startTime, endTime);

                    JCheckBox slotCheckBox = new JCheckBox(
                            String.format("%02d:00 - %02d:00", hour, hour + 1));

                    // Check if this slot is already selected
                    for (TimeSlot selectedSlot : selectedTimeSlots) {
                        if (selectedSlot.getDay() == day &&
                                selectedSlot.getStartTime().equals(startTime) &&
                                selectedSlot.getEndTime().equals(endTime)) {
                            slotCheckBox.setSelected(true);
                            break;
                        }
                    }

                    slotCheckBox.putClientProperty("timeSlot", timeSlot);
                    dayPanel.add(slotCheckBox);
                }

                slotPanel.add(dayPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(slotPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            selectedTimeSlots.clear();

            // Collect all selected time slots
            Component[] dayPanels = slotPanel.getComponents();
            for (Component dayPanel : dayPanels) {
                if (dayPanel instanceof JPanel) {
                    Component[] slotComponents = ((JPanel) dayPanel).getComponents();
                    for (Component slotComponent : slotComponents) {
                        if (slotComponent instanceof JCheckBox) {
                            JCheckBox checkBox = (JCheckBox) slotComponent;
                            if (checkBox.isSelected()) {
                                TimeSlot slot = (TimeSlot) checkBox.getClientProperty("timeSlot");
                                selectedTimeSlots.add(slot);
                            }
                        }
                    }
                }
            }

            preferredTimeSlotsCheckBox.setSelected(!selectedTimeSlots.isEmpty());
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
