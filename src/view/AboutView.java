package view;

import javax.swing.*;
import java.awt.*;

public class AboutView extends JDialog {

    public AboutView(Frame parent) {
        super(parent, "About TimeTable Builder", true);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Logo panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // You can replace this with your actual logo if available
        JLabel logoLabel = new JLabel("TimeTable Builder");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoPanel.add(logoLabel);

        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel versionLabel = new JLabel("Version 1.0.0");
        JLabel dateLabel = new JLabel("April 2025");
        JLabel authorLabel = new JLabel("Created for OOPS Assignment");
        JLabel copyrightLabel = new JLabel("© 2025 BITS Pilani");

        // Centrally align all labels
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(versionLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(authorLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(copyrightLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Description panel
        JTextArea descriptionArea = new JTextArea(
                "TimeTable Builder is a comprehensive application for creating and " +
                        "managing academic timetables, with features like auto-scheduling, " +
                        "conflict detection, and BITS timetable policy compliance."
        );
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(null);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        buttonPanel.add(okButton);

        // Add all panels to the main dialog
        add(logoPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(descriptionArea, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog properties
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
}
