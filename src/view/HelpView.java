package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class HelpView extends JDialog {
    private JTree helpTopicsTree;
    private JTextArea contentArea;

    public HelpView(Frame parent) {
        super(parent, "TimeTable Builder Help", false);
        initializeUI();

        // Set dialog properties
        setSize(700, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Create the help topics tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Help Topics");

        // Getting Started
        DefaultMutableTreeNode gettingStarted = new DefaultMutableTreeNode("Getting Started");
        root.add(gettingStarted);
        gettingStarted.add(new DefaultMutableTreeNode("Introduction"));
        gettingStarted.add(new DefaultMutableTreeNode("Main Interface"));
        gettingStarted.add(new DefaultMutableTreeNode("Quick Start Guide"));

        // Data Management
        DefaultMutableTreeNode dataManagement = new DefaultMutableTreeNode("Data Management");
        root.add(dataManagement);
        dataManagement.add(new DefaultMutableTreeNode("Managing Classrooms"));
        dataManagement.add(new DefaultMutableTreeNode("Managing Courses"));
        dataManagement.add(new DefaultMutableTreeNode("Managing Instructors"));

        // Timetable
        DefaultMutableTreeNode timetable = new DefaultMutableTreeNode("Timetable");
        root.add(timetable);
        timetable.add(new DefaultMutableTreeNode("Manual Scheduling"));
        timetable.add(new DefaultMutableTreeNode("Auto Scheduling"));
        timetable.add(new DefaultMutableTreeNode("Conflict Resolution"));
        timetable.add(new DefaultMutableTreeNode("BITS Policies"));

        // Import/Export
        DefaultMutableTreeNode importExport = new DefaultMutableTreeNode("Import/Export");
        root.add(importExport);
        importExport.add(new DefaultMutableTreeNode("Importing Data"));
        importExport.add(new DefaultMutableTreeNode("Exporting Timetables"));
        importExport.add(new DefaultMutableTreeNode("Supported Formats"));

        // Create the tree and make it look nice
        helpTopicsTree = new JTree(root);
        helpTopicsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        helpTopicsTree.setRootVisible(false);
        helpTopicsTree.setShowsRootHandles(true);

        // Create a scroll pane for the tree
        JScrollPane treeScrollPane = new JScrollPane(helpTopicsTree);
        treeScrollPane.setPreferredSize(new Dimension(200, 500));

        // Create the content area
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contentArea.setText("Select a topic from the tree to view help content.");

        // Create a scroll pane for the content
        JScrollPane contentScrollPane = new JScrollPane(contentArea);

        // Create split pane to hold tree and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, contentScrollPane);
        splitPane.setDividerLocation(200);

        // Add the split pane to the dialog
        add(splitPane, BorderLayout.CENTER);

        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add tree selection listener
        helpTopicsTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        helpTopicsTree.getLastSelectedPathComponent();

                if (node == null) return;

                Object nodeInfo = node.getUserObject();
                displayHelpContent(nodeInfo.toString());
            }
        });
    }

    private void displayHelpContent(String topic) {
        // In a real application, this would load content from a help file or database
        // For this example, we'll just display some placeholder text

        switch(topic) {
            case "Introduction":
                contentArea.setText("TimeTable Builder is an application designed to create and manage academic timetables. " +
                        "It provides tools for managing classroom data, course information, and instructor assignments, " +
                        "as well as both manual and automatic timetable creation.\n\n" +
                        "This help guide will walk you through all the features of the application.");
                break;

            case "Main Interface":
                contentArea.setText("The main interface of TimeTable Builder consists of several tabs:\n\n" +
                        "- Classrooms: Manage classroom data (room numbers, capacity, facilities)\n" +
                        "- Courses: Manage course data (course codes, names, credits, hours)\n" +
                        "- Instructors: Manage instructor information and course assignments\n" +
                        "- Timetable: Create and edit timetables manually\n" +
                        "- Auto Schedule: Generate timetable suggestions automatically\n\n" +
                        "The menu bar provides access to file operations, preferences, and help.");
                break;

            case "Managing Classrooms":
                contentArea.setText("The Classrooms tab allows you to add, edit, and delete classroom information.\n\n" +
                        "For each classroom, you can specify:\n" +
                        "- Room Number: A unique identifier for the classroom\n" +
                        "- Capacity: The number of students the room can accommodate\n" +
                        "- Facilities: Whether the room has a projector, AC, or other facilities\n\n" +
                        "To add a classroom, fill in the details and click 'Add'.\n" +
                        "To edit a classroom, select it from the table, modify the details, and click 'Update'.\n" +
                        "To delete a classroom, select it and click 'Delete'.");
                break;

            case "Auto Scheduling":
                contentArea.setText("The Auto Schedule feature allows you to generate timetable suggestions automatically.\n\n" +
                        "To use this feature:\n" +
                        "1. Select the courses you want to include in the timetable\n" +
                        "2. Click 'Generate Suggestions'\n" +
                        "3. The system will create multiple possible timetables\n" +
                        "4. Use the 'Next' and 'Previous' buttons to navigate through suggestions\n" +
                        "5. Click 'Save Current' to save a timetable you like\n\n" +
                        "The auto-scheduler takes into account BITS policies, classroom capacities, instructor availability, " +
                        "and tries to optimize the schedule to minimize conflicts and maximize resource utilization.");
                break;

            case "BITS Policies":
                contentArea.setText("TimeTable Builder enforces several BITS Pilani timetable policies:\n\n" +
                        "1. Lectures/labs must have at least one day gap between them\n" +
                        "2. Time preferences for specific courses are respected when possible\n" +
                        "3. Instructor teaching loads are balanced\n" +
                        "4. Classroom utilization is optimized\n\n" +
                        "When you validate a timetable, these policies are checked. You can enable strict enforcement " +
                        "in the Preferences dialog, which will prevent actions that would violate these policies.");
                break;

            default:
                contentArea.setText("Select a topic from the tree to view help content.");
        }
    }
}
