package view;

import controller.CourseController;
import model.Course;
import model.TimeSlot;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CourseView extends JPanel {
    private CourseController courseController;

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField courseCodeField, nameField, creditsField, lectureHoursField, labHoursField;
    private JCheckBox preferredTimeSlotsCheckBox;
    private JButton addButton, updateButton, deleteButton, clearButton, selectTimeSlotsButton;

    private List<TimeSlot> selectedTimeSlots = new ArrayList<>();

    public CourseView(CourseController courseController) {
        this.courseController = courseController;
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Course Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course Code:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        courseCodeField = new JTextField(20);
        formPanel.add(courseCodeField, gbc);

        // Course Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Course Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Credits
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Credits:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        creditsField = new JTextField(20);
        formPanel.add(creditsField, gbc);

        // Lecture Hours
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Lecture Hours:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        lectureHoursField = new JTextField(20);
        formPanel.add(lectureHoursField, gbc);

        // Lab Hours
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Lab Hours:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        labHoursField = new JTextField(20);
        formPanel.add(labHoursField, gbc);

        // Preferred Time Slots
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        preferredTimeSlotsCheckBox = new JCheckBox("Has Preferred Time Slots");
        formPanel.add(preferredTimeSlotsCheckBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        selectTimeSlotsButton = new JButton("Select Time Slots");
        selectTimeSlotsButton.setEnabled(false);
        formPanel.add(selectTimeSlotsButton, gbc);

        preferredTimeSlotsCheckBox.addActionListener(e ->
                selectTimeSlotsButton.setEnabled(preferredTimeSlotsCheckBox.isSelected()));

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
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
        String[] columnNames = {"Course Code", "Name", "Credits", "Lecture Hours", "Lab Hours", "Has Preferred Slots"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(courseTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add listeners
        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        clearButton.addActionListener(e -> clearForm());
        selectTimeSlotsButton.addActionListener(e -> selectTimeSlots());

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromTable(selectedRow);
                }
            }
        });
    }

    public void refreshData() {
        // Clear the table
        tableModel.setRowCount(0);

        // Get all courses and add to table
        List<Course> courses = courseController.getAllCourses();
        for (Course course : courses) {
            Object[] rowData = {
                    course.getCourseCode(),
                    course.getName(),
                    course.getCredits(),
                    course.getLectureHours(),
                    course.getLabHours(),
                    course.hasPreferredTimeSlots()
            };

            tableModel.addRow(rowData);
        }
    }

    private void populateFormFromTable(int row) {
        courseCodeField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        creditsField.setText(tableModel.getValueAt(row, 2).toString());
        lectureHoursField.setText(tableModel.getValueAt(row, 3).toString());
        labHoursField.setText(tableModel.getValueAt(row, 4).toString());
        preferredTimeSlotsCheckBox.setSelected((Boolean) tableModel.getValueAt(row, 5));
        selectTimeSlotsButton.setEnabled(preferredTimeSlotsCheckBox.isSelected());

        // Load preferred time slots
        String courseCode = (String) tableModel.getValueAt(row, 0);
        Course course = courseController.getCourseByCourseCode(courseCode);
        if (course != null && course.getPreferredTimeSlots() != null) {
            selectedTimeSlots.clear();
            for (TimeSlot slot : course.getPreferredTimeSlots()) {
                selectedTimeSlots.add(slot);
            }
        } else {
            selectedTimeSlots.clear();
        }
    }

    private void clearForm() {
        courseCodeField.setText("");
        nameField.setText("");
        creditsField.setText("");
        lectureHoursField.setText("");
        labHoursField.setText("");
        preferredTimeSlotsCheckBox.setSelected(false);
        selectTimeSlotsButton.setEnabled(false);
        selectedTimeSlots.clear();
        courseTable.clearSelection();
    }

    private void addCourse() {
        try {
            String courseCode = courseCodeField.getText().trim();
            String name = nameField.getText().trim();

            if (courseCode.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course code and name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (courseController.courseExists(courseCode)) {
                JOptionPane.showMessageDialog(this, "Course with this code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int credits = Integer.parseInt(creditsField.getText().trim());
            int lectureHours = Integer.parseInt(lectureHoursField.getText().trim());
            int labHours = Integer.parseInt(labHoursField.getText().trim());

            Course course = new Course(courseCode, name, credits, lectureHours, labHours);

            if (preferredTimeSlotsCheckBox.isSelected() && !selectedTimeSlots.isEmpty()) {
                TimeSlot[] timeSlots = selectedTimeSlots.toArray(new TimeSlot[0]);
                course.setPreferredTimeSlots(timeSlots);
            }

            if (courseController.addCourse(course)) {
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Course added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add course", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits, lecture hours, and lab hours must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String courseCode = courseCodeField.getText().trim();
            String originalCourseCode = tableModel.getValueAt(selectedRow, 0).toString();
            String name = nameField.getText().trim();

            if (courseCode.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course code and name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If course code is changed, check if the new one already exists
            if (!courseCode.equals(originalCourseCode) && courseController.courseExists(courseCode)) {
                JOptionPane.showMessageDialog(this, "Course with this code already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int credits = Integer.parseInt(creditsField.getText().trim());
            int lectureHours = Integer.parseInt(lectureHoursField.getText().trim());
            int labHours = Integer.parseInt(labHoursField.getText().trim());

            Course course = new Course(courseCode, name, credits, lectureHours, labHours);

            if (preferredTimeSlotsCheckBox.isSelected() && !selectedTimeSlots.isEmpty()) {
                TimeSlot[] timeSlots = selectedTimeSlots.toArray(new TimeSlot[0]);
                course.setPreferredTimeSlots(timeSlots);
            }

            // If course code is changed, delete the old one and add the new one
            if (!courseCode.equals(originalCourseCode)) {
                courseController.deleteCourse(originalCourseCode);
                courseController.addCourse(course);
            } else {
                courseController.updateCourse(course);
            }

            refreshData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Course updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Credits, lecture hours, and lab hours must be numbers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String courseCode = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete course " + courseCode + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (courseController.deleteCourse(courseCode)) {
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Course deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete course", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
