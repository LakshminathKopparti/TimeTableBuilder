package view;

import controller.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class TimetableView extends JPanel {
    private TimeTableController timeTableController;
    private ClassroomController classroomController;
    private CourseController courseController;
    private InstructorController instructorController;
    private ConflictChecker conflictChecker;

    private JPanel timetablePanel;
    private JComboBox<Classroom> classroomComboBox;
    private JComboBox<Instructor> instructorComboBox;
    private JComboBox<Course> courseComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> startTimeComboBox;
    private JComboBox<String> endTimeComboBox;
    private JCheckBox labCheckBox;
    private JButton addScheduleButton, removeScheduleButton, clearSelectionButton, validateButton;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public TimetableView(TimeTableController timeTableController,
                         ClassroomController classroomController,
                         CourseController courseController,
                         InstructorController instructorController) {
        this.timeTableController = timeTableController;
        this.classroomController = classroomController;
        this.courseController = courseController;
        this.instructorController = instructorController;
        this.conflictChecker = new ConflictChecker();

        initializeUI();
        refreshTimetable();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Control Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Add Schedule"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Course
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        courseComboBox = new JComboBox<>();
        controlPanel.add(courseComboBox, gbc);

        // Instructor
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(new JLabel("Instructor:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        instructorComboBox = new JComboBox<>();
        controlPanel.add(instructorComboBox, gbc);

        // Classroom
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(new JLabel("Classroom:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        classroomComboBox = new JComboBox<>();
        controlPanel.add(classroomComboBox, gbc);

        // Day
        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(new JLabel("Day:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        dayComboBox = new JComboBox<>(new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"});
        controlPanel.add(dayComboBox, gbc);

        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 4;
        controlPanel.add(new JLabel("Start Time:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        startTimeComboBox = new JComboBox<>();
        for (int hour = 8; hour < 18; hour++) {
            startTimeComboBox.addItem(String.format("%02d:00", hour));
        }
        controlPanel.add(startTimeComboBox, gbc);

        // End Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        controlPanel.add(new JLabel("End Time:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        endTimeComboBox = new JComboBox<>();
        for (int hour = 9; hour <= 18; hour++) {
            endTimeComboBox.addItem(String.format("%02d:00", hour));
        }
        controlPanel.add(endTimeComboBox, gbc);

        // Lab Checkbox
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        labCheckBox = new JCheckBox("Lab Session");
        controlPanel.add(labCheckBox, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel();
        addScheduleButton = new JButton("Add Schedule");
        removeScheduleButton = new JButton("Remove Schedule");
        clearSelectionButton = new JButton("Clear Selection");
        validateButton = new JButton("Validate Timetable");

        buttonPanel.add(addScheduleButton);
        buttonPanel.add(removeScheduleButton);
        buttonPanel.add(clearSelectionButton);
        buttonPanel.add(validateButton);

        controlPanel.add(buttonPanel, gbc);

        // Timetable Panel
        timetablePanel = new JPanel(new BorderLayout());
        timetablePanel.setBorder(BorderFactory.createTitledBorder("Timetable"));

        // Schedule Table
        String[] columnNames = {"Day", "Start Time", "End Time", "Course", "Instructor", "Classroom", "Lab"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(scheduleTable);
        timetablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add components to main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(timetablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Add listeners
        addScheduleButton.addActionListener(e -> addSchedule());
        removeScheduleButton.addActionListener(e -> removeSchedule());
        clearSelectionButton.addActionListener(e -> clearSelection());
        validateButton.addActionListener(e -> validateTimetable());

        startTimeComboBox.addActionListener(e -> updateEndTimeOptions());

        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = scheduleTable.getSelectedRow();
                if (selectedRow != -1) {
                    populateFormFromTable(selectedRow);
                }
            }
        });

        // Update combo boxes when course selection changes
        courseComboBox.addActionListener(e -> {
            updateInstructorOptions();
        });
    }

    public void refreshTimetable() {
        // Clear the table
        tableModel.setRowCount(0);

        // Update combo boxes
        updateCourseOptions();
        updateInstructorOptions();
        updateClassroomOptions();

        // Get all schedules and add to table
        List<CourseSchedule> schedules = timeTableController.getAllSchedules();
        for (CourseSchedule schedule : schedules) {
            Object[] rowData = {
                    schedule.getTimeSlot().getDay(),
                    schedule.getTimeSlot().getStartTime().format(timeFormatter),
                    schedule.getTimeSlot().getEndTime().format(timeFormatter),
                    schedule.getCourse().getCourseCode(),
                    schedule.getInstructor().getName(),
                    schedule.getClassroom().getRoomNumber(),
                    schedule.isLab()
            };

            tableModel.addRow(rowData);
        }
    }

    private void updateCourseOptions() {
        courseComboBox.removeAllItems();
        List<Course> courses = courseController.getAllCourses();
        for (Course course : courses) {
            courseComboBox.addItem(course);
        }
    }

    private void updateInstructorOptions() {
        instructorComboBox.removeAllItems();

        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse != null) {
            // Filter instructors who can teach this course
            List<Instructor> instructors = instructorController.getInstructorsForCourse(selectedCourse.getCourseCode());

            if (instructors.isEmpty()) {
                // If no specific instructors, show all
                instructors = instructorController.getAllInstructors();
            }

            for (Instructor instructor : instructors) {
                instructorComboBox.addItem(instructor);
            }
        } else {
            // If no course selected, show all instructors
            List<Instructor> instructors = instructorController.getAllInstructors();
            for (Instructor instructor : instructors) {
                instructorComboBox.addItem(instructor);
            }
        }
    }

    private void updateClassroomOptions() {
        classroomComboBox.removeAllItems();
        List<Classroom> classrooms = classroomController.getAllClassrooms();
        for (Classroom classroom : classrooms) {
            classroomComboBox.addItem(classroom);
        }
    }

    private void updateEndTimeOptions() {
        String startTimeStr = (String) startTimeComboBox.getSelectedItem();
        if (startTimeStr == null) {
            return;
        }

        LocalTime startTime = LocalTime.parse(startTimeStr);

        endTimeComboBox.removeAllItems();
        for (int hour = startTime.getHour() + 1; hour <= 18; hour++) {
            endTimeComboBox.addItem(String.format("%02d:00", hour));
        }
    }

    private void populateFormFromTable(int row) {
        String dayStr = tableModel.getValueAt(row, 0).toString();
        String startTimeStr = tableModel.getValueAt(row, 1).toString();
        String endTimeStr = tableModel.getValueAt(row, 2).toString();
        String courseCode = tableModel.getValueAt(row, 3).toString();
        String instructorName = tableModel.getValueAt(row, 4).toString();
        String roomNumber = tableModel.getValueAt(row, 5).toString();
        boolean isLab = (Boolean) tableModel.getValueAt(row, 6);

        // Set day
        dayComboBox.setSelectedItem(dayStr);

        // Set times
        startTimeComboBox.setSelectedItem(startTimeStr);
        endTimeComboBox.setSelectedItem(endTimeStr);

        // Set course
        for (int i = 0; i < courseComboBox.getItemCount(); i++) {
            Course course = (Course) courseComboBox.getItemAt(i);
            if (course.getCourseCode().equals(courseCode)) {
                courseComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Set instructor
        for (int i = 0; i < instructorComboBox.getItemCount(); i++) {
            Instructor instructor = (Instructor) instructorComboBox.getItemAt(i);
            if (instructor.getName().equals(instructorName)) {
                instructorComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Set classroom
        for (int i = 0; i < classroomComboBox.getItemCount(); i++) {
            Classroom classroom = (Classroom) classroomComboBox.getItemAt(i);
            if (classroom.getRoomNumber().equals(roomNumber)) {
                classroomComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Set lab checkbox
        labCheckBox.setSelected(isLab);
    }

    private void clearSelection() {
        scheduleTable.clearSelection();

        // Reset form
        if (courseComboBox.getItemCount() > 0) {
            courseComboBox.setSelectedIndex(0);
        }

        if (dayComboBox.getItemCount() > 0) {
            dayComboBox.setSelectedIndex(0);
        }

        if (startTimeComboBox.getItemCount() > 0) {
            startTimeComboBox.setSelectedIndex(0);
        }

        if (endTimeComboBox.getItemCount() > 0) {
            endTimeComboBox.setSelectedIndex(0);
        }

        labCheckBox.setSelected(false);
    }

    private void addSchedule() {
        // Get selected items
        Course course = (Course) courseComboBox.getSelectedItem();
        Instructor instructor = (Instructor) instructorComboBox.getSelectedItem();
        Classroom classroom = (Classroom) classroomComboBox.getSelectedItem();

        if (course == null || instructor == null || classroom == null) {
            JOptionPane.showMessageDialog(this, "Please select course, instructor, and classroom", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get time slot details
        String dayStr = (String) dayComboBox.getSelectedItem();
        String startTimeStr = (String) startTimeComboBox.getSelectedItem();
        String endTimeStr = (String) endTimeComboBox.getSelectedItem();
        boolean isLab = labCheckBox.isSelected();

        if (dayStr == null || startTimeStr == null || endTimeStr == null) {
            JOptionPane.showMessageDialog(this, "Please select day and time", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DayOfWeek day = DayOfWeek.valueOf(dayStr);
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        // Create time slot
        TimeSlot timeSlot = new TimeSlot(day, startTime, endTime, isLab);

        // Create schedule
        CourseSchedule schedule = new CourseSchedule(course, instructor, classroom, timeSlot, isLab);

        // Check for conflicts
        List<CourseSchedule> existingSchedules = timeTableController.getAllSchedules();
        if (conflictChecker.hasConflict(schedule, existingSchedules)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "There is a scheduling conflict. Do you still want to add this schedule?",
                    "Conflict Detected", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Add schedule
        if (timeTableController.addSchedule(schedule)) {
            refreshTimetable();
            JOptionPane.showMessageDialog(this, "Schedule added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add schedule", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get schedule details
        String dayStr = tableModel.getValueAt(selectedRow, 0).toString();
        String startTimeStr = tableModel.getValueAt(selectedRow, 1).toString();
        String endTimeStr = tableModel.getValueAt(selectedRow, 2).toString();
        String courseCode = tableModel.getValueAt(selectedRow, 3).toString();
        String instructorName = tableModel.getValueAt(selectedRow, 4).toString();
        String roomNumber = tableModel.getValueAt(selectedRow, 5).toString();
        boolean isLab = (Boolean) tableModel.getValueAt(selectedRow, 6);

        // Find matching schedule
        List<CourseSchedule> schedules = timeTableController.getAllSchedules();
        for (CourseSchedule schedule : schedules) {
            if (schedule.getTimeSlot().getDay().toString().equals(dayStr) &&
                    schedule.getTimeSlot().getStartTime().format(timeFormatter).equals(startTimeStr) &&
                    schedule.getTimeSlot().getEndTime().format(timeFormatter).equals(endTimeStr) &&
                    schedule.getCourse().getCourseCode().equals(courseCode) &&
                    schedule.getInstructor().getName().equals(instructorName) &&
                    schedule.getClassroom().getRoomNumber().equals(roomNumber) &&
                    schedule.isLab() == isLab) {

                // Remove schedule
                if (timeTableController.removeSchedule(schedule)) {
                    refreshTimetable();
                    clearSelection();
                    JOptionPane.showMessageDialog(this, "Schedule removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove schedule", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Could not find matching schedule", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void validateTimetable() {
        List<String> violations = timeTableController.getPolicyViolations();

        if (violations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Timetable is valid and follows all BITS policies.", "Validation Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Timetable has the following violations:\n\n");
            for (String violation : violations) {
                sb.append("- ").append(violation).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scrollPane, "Validation Failed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
