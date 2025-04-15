package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for CSV operations
 */
public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    /**
     * Parse a CSV line
     *
     * @param line the line to parse
     * @return list of values
     */
    public static List<String> parseLine(String line) {
        return parseLine(line, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /**
     * Parse a CSV line with custom separator and quote character
     *
     * @param line the line to parse
     * @param separator the separator character
     * @param quote the quote character
     * @return list of values
     */
    public static List<String> parseLine(String line, char separator, char quote) {
        List<String> result = new ArrayList<>();

        if (line == null || line.isEmpty()) {
            return result;
        }

        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == quote) {
                // Handle quote character
                if (inQuotes) {
                    // Check for escaped quotes
                    if (i + 1 < line.length() && line.charAt(i + 1) == quote) {
                        field.append(quote);
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    inQuotes = true;
                }
            } else if (c == separator && !inQuotes) {
                // End of field
                result.add(field.toString());
                field.setLength(0);
            } else {
                // Regular character
                field.append(c);
            }
        }

        // Add the last field
        result.add(field.toString());

        return result;
    }

    /**
     * Format a list of values as a CSV line
     *
     * @param values the values to format
     * @return formatted CSV line
     */
    public static String formatLine(List<String> values) {
        return formatLine(values, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /**
     * Format a list of values as a CSV line with custom separator and quote character
     *
     * @param values the values to format
     * @param separator the separator character
     * @param quote the quote character
     * @return formatted CSV line
     */
    public static String formatLine(List<String> values, char separator, char quote) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);

            if (value == null) {
                value = "";
            }

            boolean needQuotes = value.contains(String.valueOf(separator)) ||
                    value.contains(String.valueOf(quote)) ||
                    value.contains("\n") ||
                    value.contains("\r");

            if (needQuotes) {
                // Escape quotes by doubling them
                line.append(quote).append(value.replace(String.valueOf(quote),
                                String.valueOf(quote) + quote))
                        .append(quote);
            } else {
                line.append(value);
            }

            if (i < values.size() - 1) {
                line.append(separator);
            }
        }

        return line.toString();
    }

    /**
     * Read a CSV file
     *
     * @param filePath the file path
     * @return list of rows (each row is a list of values)
     */
    public static List<List<String>> readCSV(String filePath) {
        List<List<String>> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(parseLine(line));
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + filePath);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Write data to a CSV file
     *
     * @param filePath the file path
     * @param data the data to write (list of rows, each row is a list of values)
     * @return true if successful
     */
    public static boolean writeCSV(String filePath, List<List<String>> data) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            for (List<String> row : data) {
                writer.println(formatLine(row));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }
}
