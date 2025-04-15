package view;

import controller.ConflictChecker;
import model.CourseSchedule;
import model.Timetable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ConflictView extends JDialog {
    private ConflictChecker conflictChecker;
    private Timetable timetable;

    private JTable conflictsTable;
    private DefaultTableModel tableModel;
    private JTextArea descriptionArea;
    private JButton resolveButton, ignoreButton, closeButton;

    public ConflictView(Frame parent, ConflictChecker conflictChecker, Timetable timetable) {
        super(parent, "Timetable Conflicts", true);
        this.conflictChecker = conflictChecker;
        this.timetable = timetable;

        initializeUI();
        detectConflicts();

        // Set dialog properties
        setSize(800, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Create table model
        String[] columnNames = {"Type", "Course 1", "Course 2", "Day", "Time", "Resource"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        conflictsTable = new JTable(tableModel);
        conflictsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conflictsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(conflictsTable);

        // Description panel
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("Conflict Description"));

        descriptionArea = new JTextArea(5, 40);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        resolveButton = new JButton("Resolve Selected");
        ignoreButton = new JButton("Ignore Selected");
        closeButton = new JButton("Close");

        buttonPanel.add(resolveButton);
        buttonPanel.add(ignoreButton);
        buttonPanel.add(closeButton);

        // Add components to dialog
        add(tableScrollPane, BorderLayout.CENTER);
        add(descriptionPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add listeners
        conflictsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDescription();
            }
        });

        resolveButton.addActionListener(e -> resolveSelectedConflict());
        ignoreButton.addActionListener(e -> ignoreSelectedConflict());
        closeButton.addActionListener(e -> dispose());
    }

    private void detectConflicts() {
        // Clear the table
        tableModel.setRowCount(0);

        // Get all schedules
        List<CourseSchedule> schedules = timetable.getSchedules();

        // Check each pair of schedules for conflicts
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                CourseSchedule schedule1 = schedules.get(i);
                CourseSchedule schedule2 = schedules.get(j);

                if (conflictChecker.conflictExists(schedule1, schedule2)) {
                    // Determine conflict type
                    String conflictType = determineConflictType(schedule1, schedule2);
                    String resource = determineConflictResource(schedule1, schedule2, conflictType);

                    Object[] rowData = {
                            conflictType,
                            schedule1.getCourse().getCourseCode(),
                            schedule2.getCourse().getCourseCode(),
                            schedule1.getTimeSlot().getDay(),
                            schedule1.getTimeSlot().getStartTime() + "-" + schedule1.getTimeSlot().getEndTime(),
                            resource
                    };

                    tableModel.addRow(rowData);
                }
            }
        }

        // Check BITS policies
        List<String> policyViolations = conflictChecker.checkBITSPolicyCompliance(timetable);
        for (String violation : policyViolations) {
            // Extract course code if present
            String courseCode = "N/A";
            if (violation.contains("has")) {
                String[] parts = violation.split(" has ");
                courseCode = parts[0];
            }

            Object[] rowData = {
                    "Policy Violation",
                    courseCode,
                    "",
                    "",
                    "",
                    violation
            };

            tableModel.addRow(rowData);
        }

        // Update buttons and description
        boolean hasConflicts = tableModel.getRowCount() > 0;
        resolveButton.setEnabled(hasConflicts);
        ignoreButton.setEnabled(hasConflicts);

        if (hasConflicts) {
            conflictsTable.setRowSelectionInterval(0, 0);
            updateDescription();
        } else {
            descriptionArea.setText("No conflicts detected in this timetable.");
        }
    }

    private String determineConflictType(CourseSchedule schedule1, CourseSchedule schedule2) {
        if (schedule1.getClassroom().equals(schedule2.getClassroom())) {
            return "Classroom Conflict";
        } else if (schedule1.getInstructor().equals(schedule2.getInstructor())) {
            return "Instructor Conflict";
        } else if (schedule1.getCourse().equals(schedule2.getCourse())) {
            return "Course Conflict";
        } else {
            return "Time Conflict";
        }
    }

    private String determineConflictResource(CourseSchedule schedule1, CourseSchedule schedule2, String conflictType) {
        switch (conflictType) {
            case "Classroom Conflict":
                return schedule1.getClassroom().getRoomNumber();
            case "Instructor Conflict":
                return schedule1.getInstructor().getName();
            case "Course Conflict":
                return schedule1.getCourse().getCourseCode();
            default:
                return "";
        }
    }

    private void updateDescription() {
        int selectedRow = conflictsTable.getSelectedRow();
        if (selectedRow == -1) {
            descriptionArea.setText("");
            return;
        }

        String conflictType = (String) tableModel.getValueAt(selectedRow, 0);
        String course1 = (String) tableModel.getValueAt(selectedRow, 1);
        String course2 = (String) tableModel.getValueAt(selectedRow, 2);
        String day = (String) tableModel.getValueAt(selectedRow, 3);
        String time = (String) tableModel.getValueAt(selectedRow, 4);
        String resource = (String) tableModel.getValueAt(selectedRow, 5);

        if (conflictType.equals("Policy Violation")) {
            descriptionArea.setText("Policy Violation: " + resource + "\n\n" +
                    "This schedule violates BITS timetable policies. To resolve this issue, you may need to " +
                    "reschedule one or more sessions to comply with the policy requirements.");
        } else {
            descriptionArea.setText("Conflict Type: " + conflictType + "\n" +
                    "Courses: " + course1 + (course2.isEmpty() ? "" : " and " + course2) + "\n" +
                    "When: " + (day.isEmpty() ? "Multiple times" : day + " at " + time) + "\n" +
                    "Shared Resource: " + resource + "\n\n" +
                    "This conflict occurs because two sessions are scheduled at the same time " +
                    "using the same resource. To resolve this, you can reschedule one of the sessions " +
                    "to a different time or allocate a different resource.");
        }
    }

    private void resolveSelectedConflict() {
        int selectedRow = conflictsTable.getSelectedRow();
        if (selectedRow == -1) return;

        // In a real implementation, this would open a dialog to help resolve the conflict
        // For this example, we'll just show a message
        JOptionPane.showMessageDialog(this,
                "This would open a conflict resolution dialog to help reschedule one of the conflicting sessions.",
                "Resolve Conflict", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ignoreSelectedConflict() {
        int selectedRow = conflictsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to ignore this conflict? It will be marked as resolved but still exists in the timetable.",
                "Confirm Ignore", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);

            if (tableModel.getRowCount() > 0) {
                conflictsTable.setRowSelectionInterval(0, 0);
                updateDescription();
            } else {
                descriptionArea.setText("No remaining conflicts.");
                resolveButton.setEnabled(false);
                ignoreButton.setEnabled(false);
            }
        }
    }
}
