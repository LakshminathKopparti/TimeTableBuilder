package view;

import controller.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AutoScheduleView extends JPanel {
    private TimeTableController timeTableController;
    private ClassroomController classroomController;
    private CourseController courseController;
    private InstructorController instructorController;

    private JList<Course> courseList;
    private DefaultListModel<Course> courseListModel;
    private JButton addCourseButton, removeCourseButton, clearCoursesButton;

    private JPanel timetablePanel;
    private JButton generateButton, nextButton, prevButton, saveButton, regenerateButton;
    private JLabel suggestionLabel;

    private List<Course> selectedCourses;

    public AutoScheduleView(TimeTableController timeTableController,
                            ClassroomController classroomController,
                            CourseController courseController,
                            InstructorController instructorController) {
        this.timeTableController = timeTableController;
        this.classroomController = classroomController;
        this.courseController = courseController;
        this.instructorController = instructorController;

        this.selectedCourses = new ArrayList<>();

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Course Selection Panel
        JPanel courseSelectionPanel = new JPanel(new BorderLayout());
        courseSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Courses for Auto Scheduling"));

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane courseScrollPane = new JScrollPane(courseList);
        courseSelectionPanel.add(courseScrollPane, BorderLayout.CENTER);

        // Course control buttons
        JPanel courseButtonPanel = new JPanel();
        addCourseButton = new JButton("Add Courses");
        removeCourseButton = new JButton("Remove Courses");
        clearCoursesButton = new JButton("Clear All");

        courseButtonPanel.add(addCourseButton);
        courseButtonPanel.add(removeCourseButton);
        courseButtonPanel.add(clearCoursesButton);

        courseSelectionPanel.add(courseButtonPanel, BorderLayout.SOUTH);

        // Timetable Panel
        timetablePanel = new JPanel(new BorderLayout());
        timetablePanel.setBorder(BorderFactory.createTitledBorder("Auto Generated Timetable"));

        // Control buttons
        JPanel timetableControlPanel = new JPanel();
        generateButton = new JButton("Generate Suggestions");
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        saveButton = new JButton("Save Current");
        regenerateButton = new JButton("Regenerate");

        timetableControlPanel.add(generateButton);
        timetableControlPanel.add(prevButton);
        timetableControlPanel.add(nextButton);
        timetableControlPanel.add(saveButton);
        timetableControlPanel.add(regenerateButton);

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        saveButton.setEnabled(false);
        regenerateButton.setEnabled(false);

        suggestionLabel = new JLabel("No suggestions generated yet");
        suggestionLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(suggestionLabel, BorderLayout.CENTER);
        headerPanel.add(timetableControlPanel, BorderLayout.SOUTH);

        timetablePanel.add(headerPanel, BorderLayout.NORTH);

        // Auto Schedule display
        JPanel scheduleDisplayPanel = new JPanel();
        scheduleDisplayPanel.setLayout(new BorderLayout());

        // Create empty schedule display
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new GridBagLayout());
        JLabel emptyLabel = new JLabel("Generate suggestions to see timetable here");
        emptyPanel.add(emptyLabel);

        scheduleDisplayPanel.add(emptyPanel, BorderLayout.CENTER);
        timetablePanel.add(scheduleDisplayPanel, BorderLayout.CENTER);

        // Add panels to main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(courseSelectionPanel, BorderLayout.NORTH);
        mainPanel.add(timetablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Add listeners
        addCourseButton.addActionListener(e -> addCourses());
        removeCourseButton.addActionListener(e -> removeCourses());
        clearCoursesButton.addActionListener(e -> clearCourses());

        generateButton.addActionListener(e -> generateSuggestions());
        prevButton.addActionListener(e -> previousSuggestion());
        nextButton.addActionListener(e -> nextSuggestion());
        saveButton.addActionListener(e -> saveCurrentSuggestion());
        regenerateButton.addActionListener(e -> regenerateSuggestions());
    }

    private void addCourses() {
        // Get all available courses
        List<Course> availableCourses = courseController.getAllCourses();

        // Filter out already selected courses
        List<Course> filteredCourses = new ArrayList<>();
        for (Course course : availableCourses) {
            boolean alreadySelected = false;
            for (Course selectedCourse : selectedCourses) {
                if (selectedCourse.getCourseCode().equals(course.getCourseCode())) {
                    alreadySelected = true;
                    break;
                }
            }
            if (!alreadySelected) {
                filteredCourses.add(course);
            }
        }

        if (filteredCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No more courses available to add", "No Courses", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog for course selection
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Courses", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Create course list
        JList<Course> list = new JList<>(filteredCourses.toArray(new Course[0]));
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(list);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            // Add selected courses
            List<Course> newSelectedCourses = list.getSelectedValuesList();

            for (Course course : newSelectedCourses) {
                selectedCourses.add(course);
                courseListModel.addElement(course);
            }

            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void removeCourses() {
        List<Course> coursesToRemove = courseList.getSelectedValuesList();

        if (coursesToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select courses to remove", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Course course : coursesToRemove) {
            selectedCourses.remove(course);
            courseListModel.removeElement(course);
        }
    }

    private void clearCourses() {
        selectedCourses.clear();
        courseListModel.clear();
    }

    private void generateSuggestions() {
        if (selectedCourses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one course", "No Courses", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get all instructors and classrooms
        List<Instructor> instructors = instructorController.getAllInstructors();
        List<Classroom> classrooms = classroomController.getAllClassrooms();

        if (instructors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No instructors available. Please add instructors first.", "No Instructors", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (classrooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No classrooms available. Please add classrooms first.", "No Classrooms", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create time slots
        List<TimeSlot> availableTimeSlots = new ArrayList<>();

        // Add standard time slots (8 AM to 6 PM, Monday to Friday)
        for (int dayValue = 1; dayValue <= 5; dayValue++) {
            DayOfWeek day = DayOfWeek.of(dayValue);

            // Add lecture slots (1 hour each)
            for (int hour = 8; hour < 18; hour++) {
                LocalTime startTime = LocalTime.of(hour, 0);
                LocalTime endTime = LocalTime.of(hour + 1, 0);

                TimeSlot lectureSlot = new TimeSlot(day, startTime, endTime, false);
                availableTimeSlots.add(lectureSlot);
            }

            // Add lab slots (2 hours each)
            for (int hour = 8; hour < 17; hour += 2) {
                LocalTime startTime = LocalTime.of(hour, 0);
                LocalTime endTime = LocalTime.of(hour + 2, 0);

                TimeSlot labSlot = new TimeSlot(day, startTime, endTime, true);
                availableTimeSlots.add(labSlot);
            }
        }

        // Show progress dialog
        JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generating Timetable", true);
        progressDialog.setLayout(new BorderLayout());
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        JLabel progressLabel = new JLabel("Generating suggestions, please wait...");
        progressLabel.setHorizontalAlignment(JLabel.CENTER);

        progressDialog.add(progressLabel, BorderLayout.NORTH);
        progressDialog.add(progressBar, BorderLayout.CENTER);

        // Use a timer to show the dialog after a short delay
        Timer timer = new Timer(100, e -> {
            progressDialog.setVisible(true);
        });
        timer.setRepeats(false);
        timer.start();

        // Generate suggestions in a background thread
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                timeTableController.generateTimetableSuggestions(
                        selectedCourses, instructors, classrooms, availableTimeSlots);
                return null;
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                updateTimetableDisplay();

                // Enable/disable navigation buttons
                prevButton.setEnabled(false);
                nextButton.setEnabled(timeTableController.hasNextSuggestion());
                saveButton.setEnabled(true);
                regenerateButton.setEnabled(true);

                // Update suggestion label
                suggestionLabel.setText("Suggestion 1" +
                        (timeTableController.hasNextSuggestion() ? " (more available)" : ""));
            }
        };

        worker.execute();
    }

    private void previousSuggestion() {
        Timetable prevTimetable = timeTableController.previousSuggestion();
        if (prevTimetable != null) {
            updateTimetableDisplay();

            // Update button states
            nextButton.setEnabled(true);
            prevButton.setEnabled(timeTableController.hasPreviousSuggestion());

            // Update suggestion label
            int currentIndex = timeTableController.getCurrentSuggestionIndex() + 1;
            suggestionLabel.setText("Suggestion " + currentIndex +
                    (timeTableController.hasNextSuggestion() ? " (more available)" : ""));
        }
    }

    private void nextSuggestion() {
        Timetable nextTimetable = timeTableController.nextSuggestion();
        if (nextTimetable != null) {
            updateTimetableDisplay();

            // Update button states
            prevButton.setEnabled(true);
            nextButton.setEnabled(timeTableController.hasNextSuggestion());

            // Update suggestion label
            int currentIndex = timeTableController.getCurrentSuggestionIndex() + 1;
            suggestionLabel.setText("Suggestion " + currentIndex +
                    (timeTableController.hasNextSuggestion() ? " (more available)" : ""));
        }
    }

    private void saveCurrentSuggestion() {
        String name = JOptionPane.showInputDialog(this, "Enter a name for this timetable:", "Save Timetable", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            if (timeTableController.saveTimetableAs(name.trim())) {
                JOptionPane.showMessageDialog(this, "Timetable saved successfully as: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save timetable", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void regenerateSuggestions() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "This will generate a new set of suggestions. Continue?",
                "Regenerate Suggestions", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            generateSuggestions();
        }
    }

    private void updateTimetableDisplay() {
        // Get current timetable
        Timetable timetable = timeTableController.getCurrentTimetable();

        // Remove old timetable display
        Component[] components = timetablePanel.getComponents();
        if (components.length > 1) {
            timetablePanel.remove(1);
        }

        // Create timetable grid
        JPanel gridPanel = new JPanel(new GridBagLayout());

        // Create header row (days of week)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.2;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        gridPanel.add(new JLabel("Time/Day"), gbc);

        for (int day = 1; day <= 5; day++) {
            gbc.gridx = day;
            gridPanel.add(new JLabel(DayOfWeek.of(day).toString()), gbc);
        }

        // Create time rows
        for (int hour = 8; hour < 18; hour++) {
            gbc.gridx = 0;
            gbc.gridy = hour - 7;

            gridPanel.add(new JLabel(String.format("%02d:00", hour)), gbc);

            // Initialize empty cells for all days
            for (int day = 1; day <= 5; day++) {
                gbc.gridx = day;
                gbc.weightx = 1.0;

                JPanel cellPanel = new JPanel();
                cellPanel.setLayout(new BoxLayout(cellPanel, BoxLayout.Y_AXIS));
                cellPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                // Tag the panel with its day and hour for easy lookup
                cellPanel.putClientProperty("day", day);
                cellPanel.putClientProperty("hour", hour);

                gridPanel.add(cellPanel, gbc);
            }
        }

        // Add schedules to cells
        for (CourseSchedule schedule : timetable.getSchedules()) {
            DayOfWeek day = schedule.getTimeSlot().getDay();
            LocalTime startTime = schedule.getTimeSlot().getStartTime();
            LocalTime endTime = schedule.getTimeSlot().getEndTime();

            int dayIndex = day.getValue();
            int startHour = startTime.getHour();
            int endHour = endTime.getHour();

            // Find panels for each hour this schedule spans
            for (int hour = startHour; hour < endHour; hour++) {
                // Find the panel for this time slot
                for (Component component : gridPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        JPanel panel = (JPanel) component;
                        Object panelDay = panel.getClientProperty("day");
                        Object panelHour = panel.getClientProperty("hour");

                        if (panelDay != null && panelHour != null &&
                                (Integer) panelDay == dayIndex && (Integer) panelHour == hour) {

                            // Add schedule info to panel
                            JPanel schedulePanel = new JPanel();
                            schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));

                            if (schedule.isLab()) {
                                schedulePanel.setBackground(new Color(220, 255, 220)); // Light green for labs
                            } else {
                                schedulePanel.setBackground(new Color(220, 240, 255)); // Light blue for lectures
                            }

                            JLabel courseLabel = new JLabel(schedule.getCourse().getCourseCode());
                            JLabel instructorLabel = new JLabel(schedule.getInstructor().getName());
                            JLabel roomLabel = new JLabel(schedule.getClassroom().getRoomNumber());
                            JLabel typeLabel = new JLabel(schedule.isLab() ? "Lab" : "Lecture");

                            schedulePanel.add(courseLabel);
                            schedulePanel.add(instructorLabel);
                            schedulePanel.add(roomLabel);
                            schedulePanel.add(typeLabel);

                            panel.removeAll();
                            panel.add(schedulePanel);

                            break;
                        }
                    }
                }
            }
        }

        // Add the grid to a scroll pane
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        timetablePanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh the panel
        timetablePanel.revalidate();
        timetablePanel.repaint();
    }
}
