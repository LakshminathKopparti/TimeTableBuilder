package view;

import controller.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainView extends JFrame {
    private TimeTableController timeTableController;
    private ClassroomController classroomController;
    private CourseController courseController;
    private InstructorController instructorController;
    private ImportExportController importExportController;

    private JTabbedPane tabbedPane;
    private ClassroomView classroomView;
    private CourseView courseView;
    private InstructorView instructorView;
    private TimetableView timetableView;
    private AutoScheduleView autoScheduleView;

    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, viewMenu, helpMenu;

    public MainView() {
        // Initialize controllers
        timeTableController = new TimeTableController();
        classroomController = new ClassroomController();
        courseController = new CourseController();
        instructorController = new InstructorController();
        importExportController = new ImportExportController();

        // Set up the frame
        setTitle("TimeTable Builder");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Initialize views
        classroomView = new ClassroomView(classroomController);
        courseView = new CourseView(courseController);
        instructorView = new InstructorView(instructorController);
        timetableView = new TimetableView(timeTableController, classroomController, courseController, instructorController);
        autoScheduleView = new AutoScheduleView(timeTableController, classroomController, courseController, instructorController);

        // Add views to tabbed pane
        tabbedPane.addTab("Classrooms", new ImageIcon(), classroomView, "Manage classrooms");
        tabbedPane.addTab("Courses", new ImageIcon(), courseView, "Manage courses");
        tabbedPane.addTab("Instructors", new ImageIcon(), instructorView, "Manage instructors");
        tabbedPane.addTab("Timetable", new ImageIcon(), timetableView, "View and edit timetable");
        tabbedPane.addTab("Auto Schedule", new ImageIcon(), autoScheduleView, "Generate timetable automatically");

        // Add tabbed pane to frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Add status bar
        JPanel statusBar = new JPanel();
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

        JLabel statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel);
        statusBar.add(Box.createHorizontalGlue());

        JLabel itemCountLabel = new JLabel("Classrooms: " + classroomController.getClassroomCount() +
                " | Courses: " + courseController.getCourseCount() +
                " | Instructors: " + instructorController.getInstructorCount());
        statusBar.add(itemCountLabel);

        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newMenuItem = new JMenuItem("New Timetable", KeyEvent.VK_N);
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.addActionListener(e -> createNewTimetable());

        JMenuItem openMenuItem = new JMenuItem("Open Timetable", KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.addActionListener(e -> openTimetable());

        JMenuItem saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.addActionListener(e -> saveTimetable());

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.addActionListener(e -> saveTimetableAs());

        JMenuItem exportMenuItem = new JMenuItem("Export to CSV...");
        exportMenuItem.addActionListener(e -> exportTimetable());

        JMenuItem importMenuItem = new JMenuItem("Import from CSV...");
        importMenuItem.addActionListener(e -> importTimetable());

        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exportMenuItem);
        fileMenu.add(importMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Edit Menu
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem prefMenuItem = new JMenuItem("Preferences...");
        prefMenuItem.addActionListener(e -> showPreferences());

        editMenu.add(prefMenuItem);

        // View Menu
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem refreshMenuItem = new JMenuItem("Refresh", KeyEvent.VK_R);
        refreshMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        refreshMenuItem.addActionListener(e -> refreshViews());

        viewMenu.add(refreshMenuItem);

        // Help Menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.addActionListener(e -> showAbout());

        helpMenu.add(aboutMenuItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        // Set menu bar to frame
        setJMenuBar(menuBar);
    }

    private void createNewTimetable() {
        String name = JOptionPane.showInputDialog(this, "Enter timetable name:", "New Timetable", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            timeTableController.createNewTimetable(name.trim());
            timetableView.refreshTimetable();
            JOptionPane.showMessageDialog(this, "New timetable created: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openTimetable() {
        List<String> timetableNames = timeTableController.getAllTimetableNames();
        if (timetableNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No saved timetables found.", "Open Timetable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selectedName = (String) JOptionPane.showInputDialog(this,
                "Select a timetable to open:", "Open Timetable",
                JOptionPane.QUESTION_MESSAGE, null,
                timetableNames.toArray(), timetableNames.get(0));

        if (selectedName != null) {
            if (timeTableController.loadTimetable(selectedName)) {
                timetableView.refreshTimetable();
                JOptionPane.showMessageDialog(this, "Timetable loaded: " + selectedName, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load timetable: " + selectedName, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveTimetable() {
        if (timeTableController.saveTimetable()) {
            JOptionPane.showMessageDialog(this, "Timetable saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save timetable.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTimetableAs() {
        String name = JOptionPane.showInputDialog(this, "Enter timetable name:", "Save Timetable As", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            if (timeTableController.saveTimetableAs(name.trim())) {
                JOptionPane.showMessageDialog(this, "Timetable saved as: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save timetable.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportTimetable() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Timetable to CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            if (timeTableController.exportToCSV(filePath)) {
                JOptionPane.showMessageDialog(this, "Timetable exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export timetable.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importTimetable() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Timetable from CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            String name = JOptionPane.showInputDialog(this, "Enter timetable name:", "Import Timetable", JOptionPane.QUESTION_MESSAGE);

            if (name != null && !name.trim().isEmpty()) {
                if (timeTableController.importFromCSV(name.trim(), filePath,
                        courseController.getAllCourses(),
                        instructorController.getAllInstructors(),
                        classroomController.getAllClassrooms())) {
                    timetableView.refreshTimetable();
                    JOptionPane.showMessageDialog(this, "Timetable imported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to import timetable.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showPreferences() {
        PreferenceView preferenceView = new PreferenceView(this);
        preferenceView.setVisible(true);
    }

    private void refreshViews() {
        classroomController.refreshClassrooms();
        courseController.refreshCourses();
        instructorController.refreshInstructors();

        classroomView.refreshData();
        courseView.refreshData();
        instructorView.refreshData();
        timetableView.refreshTimetable();

        JOptionPane.showMessageDialog(this, "Views refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "TimeTable Builder\nVersion 1.0\n\n" +
                        "A tool for creating and managing academic timetables.\n" +
                        "Assignment for OOPS class.\n\n" +
                        "© 2025",
                "About TimeTable Builder",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}
