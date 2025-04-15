package util;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Utility class for UI operations
 */
public class UIUtils {

    private static final Preferences PREFS = Preferences.userNodeForPackage(UIUtils.class);

    /**
     * Center a window on the screen
     *
     * @param window the window to center
     */
    public static void centerOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - window.getWidth()) / 2;
        int y = (screenSize.height - window.getHeight()) / 2;
        window.setLocation(x, y);
    }

    /**
     * Create a standard button with icon
     *
     * @param text button text
     * @param iconName icon resource name (without path/extension)
     * @return configured button
     */
    public static JButton createButton(String text, String iconName) {
        JButton button = new JButton(text);

        // Try to load icon if provided
        if (iconName != null && !iconName.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(UIUtils.class.getResource("/icons/" + iconName + ".png"));
                button.setIcon(icon);
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + iconName);
            }
        }

        return button;
    }

    /**
     * Show an error dialog
     *
     * @param parent the parent component
     * @param message the error message
     * @param title the dialog title
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an information dialog
     *
     * @param parent the parent component
     * @param message the information message
     * @param title the dialog title
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a confirmation dialog
     *
     * @param parent the parent component
     * @param message the confirmation message
     * @param title the dialog title
     * @return true if user confirmed
     */
    public static boolean showConfirmation(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Show an input dialog
     *
     * @param parent the parent component
     * @param message the prompt message
     * @param title the dialog title
     * @param initialValue the initial value
     * @return the entered string or null if canceled
     */
    public static String showInput(Component parent, String message, String title, String initialValue) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE, null, null, initialValue).toString();
    }

    /**
     * Show a selection dialog
     *
     * @param parent the parent component
     * @param message the prompt message
     * @param title the dialog title
     * @param options the available options
     * @param initialOption the initially selected option
     * @return the selected option or null if canceled
     */
    public static Object showSelection(Component parent, String message, String title, Object[] options, Object initialOption) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE, null, options, initialOption);
    }

    /**
     * Apply dark mode to the UI
     */
    public static void applyDarkMode() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("Panel.background", new Color(43, 43, 43));
            UIManager.put("OptionPane.background", new Color(43, 43, 43));
            UIManager.put("TextField.background", new Color(60, 63, 65));
            UIManager.put("TextField.foreground", new Color(187, 187, 187));
            UIManager.put("TextArea.background", new Color(60, 63, 65));
            UIManager.put("TextArea.foreground", new Color(187, 187, 187));
            UIManager.put("ComboBox.background", new Color(60, 63, 65));
            UIManager.put("ComboBox.foreground", new Color(187, 187, 187));
            UIManager.put("Table.background", new Color(60, 63, 65));
            UIManager.put("Table.foreground", new Color(187, 187, 187));
            UIManager.put("Table.selectionBackground", new Color(75, 110, 175));
            UIManager.put("Table.gridColor", new Color(80, 80, 80));
            UIManager.put("Label.foreground", new Color(187, 187, 187));
            UIManager.put("Button.background", new Color(60, 63, 65));
            UIManager.put("Button.foreground", new Color(187, 187, 187));
            UIManager.put("TabbedPane.background", new Color(43, 43, 43));
            UIManager.put("TabbedPane.foreground", new Color(187, 187, 187));

        } catch (Exception e) {
            System.err.println("Failed to apply dark mode");
            e.printStackTrace();
        }
    }

    /**
     * Save window size and position
     *
     * @param window the window
     * @param windowName a unique name for the window
     */
    public static void saveWindowState(Window window, String windowName) {
        PREFS.putInt(windowName + ".x", window.getX());
        PREFS.putInt(windowName + ".y", window.getY());
        PREFS.putInt(windowName + ".width", window.getWidth());
        PREFS.putInt(windowName + ".height", window.getHeight());
    }

    /**
     * Restore window size and position
     *
     * @param window the window
     * @param windowName a unique name for the window
     */
    public static void restoreWindowState(Window window, String windowName) {
        int x = PREFS.getInt(windowName + ".x", -1);
        int y = PREFS.getInt(windowName + ".y", -1);
        int width = PREFS.getInt(windowName + ".width", window.getWidth());
        int height = PREFS.getInt(windowName + ".height", window.getHeight());

        if (x != -1 && y != -1) {
            window.setLocation(x, y);
        } else {
            centerOnScreen(window);
        }

        window.setSize(width, height);
    }

    /**
     * Create a titled border panel
     *
     * @param title the panel title
     * @return the panel
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }
}
