package util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations
 */
public class FileUtils {

    /**
     * Check if a directory exists, create it if it doesn't
     *
     * @param directoryPath the path to check/create
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureDirectoryExists(String directoryPath) {
        try {
            Files.createDirectories(Paths.get(directoryPath));
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + directoryPath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Read all lines from a file
     *
     * @param filePath the path to the file
     * @return a list of strings containing all lines
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();

        try {
            if (Files.exists(Paths.get(filePath))) {
                lines = Files.readAllLines(Paths.get(filePath));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Write lines to a file
     *
     * @param filePath the path to the file
     * @param lines the lines to write
     * @return true if successful
     */
    public static boolean writeLines(String filePath, List<String> lines) {
        try {
            ensureDirectoryExists(Paths.get(filePath).getParent().toString());
            Files.write(Paths.get(filePath), lines);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a file
     *
     * @param filePath the path to the file
     * @return true if successful or file doesn't exist
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error deleting file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all files in a directory with a specific extension
     *
     * @param directoryPath the directory path
     * @param extension the file extension (without dot)
     * @return a list of file names
     */
    public static List<String> getFilesWithExtension(String directoryPath, String extension) {
        List<String> fileNames = new ArrayList<>();

        try {
            if (Files.exists(Paths.get(directoryPath))) {
                Files.list(Paths.get(directoryPath))
                        .filter(path -> path.toString().endsWith("." + extension))
                        .forEach(path -> fileNames.add(path.getFileName().toString()));
            }
        } catch (IOException e) {
            System.err.println("Error listing files in directory: " + directoryPath);
            e.printStackTrace();
        }

        return fileNames;
    }

    /**
     * Create backup of a file
     *
     * @param filePath the path to the file
     * @return true if successful
     */
    public static boolean createBackup(String filePath) {
        try {
            Path source = Paths.get(filePath);
            if (Files.exists(source)) {
                String backupPath = filePath + ".bak";
                Files.copy(source, Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Error creating backup for file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a file exists
     *
     * @param filePath the path to check
     * @return true if the file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
