package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * Utility class for logging
 */
public class LogUtils {

    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR, FATAL
    }

    private static final String LOG_FILE = "timetable_builder.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static LogLevel currentLevel = LogLevel.INFO;
    private static boolean consoleOutput = true;
    private static boolean fileOutput = true;

    /**
     * Set the current log level
     *
     * @param level the log level
     */
    public static void setLogLevel(LogLevel level) {
        currentLevel = level;
    }

    /**
     * Set whether to output logs to console
     *
     * @param enabled true to enable console output
     */
    public static void setConsoleOutput(boolean enabled) {
        consoleOutput = enabled;
    }

    /**
     * Set whether to output logs to file
     *
     * @param enabled true to enable file output
     */
    public static void setFileOutput(boolean enabled) {
        fileOutput = enabled;
    }

    /**
     * Log a debug message
     *
     * @param message the message to log
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    /**
     * Log an info message
     *
     * @param message the message to log
     */
    public static void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    /**
     * Log a warning message
     *
     * @param message the message to log
     */
    public static void warning(String message) {
        log(LogLevel.WARNING, message, null);
    }

    /**
     * Log an error message
     *
     * @param message the message to log
     * @param exception the exception (optional)
     */
    public static void error(String message, Throwable exception) {
        log(LogLevel.ERROR, message, exception);
    }

    /**
     * Log a fatal error message
     *
     * @param message the message to log
     * @param exception the exception (optional)
     */
    public static void fatal(String message, Throwable exception) {
        log(LogLevel.FATAL, message, exception);
    }

    /**
     * Log a message with a specific level
     *
     * @param level the log level
     * @param message the message to log
     * @param exception the exception (optional)
     */
    private static void log(LogLevel level, String message, Throwable exception) {
        if (level.ordinal() < currentLevel.ordinal()) {
            return;
        }

        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);

        // Console output
        if (consoleOutput) {
            if (level == LogLevel.ERROR || level == LogLevel.FATAL) {
                System.err.println(logEntry);
                if (exception != null) {
                    exception.printStackTrace(System.err);
                }
            } else {
                System.out.println(logEntry);
            }
        }

        // File output
        if (fileOutput) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(logEntry);
                if (exception != null) {
                    exception.printStackTrace(writer);
                }
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }
    }

    /**
     * Clear the log file
     */
    public static void clearLogFile() {
        if (fileOutput) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, false))) {
                writer.println("[" + LocalDateTime.now().format(DATE_FORMATTER) + "] Log file cleared");
            } catch (IOException e) {
                System.err.println("Failed to clear log file: " + e.getMessage());
            }
        }
    }
}
