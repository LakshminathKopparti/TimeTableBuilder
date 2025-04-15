package util;

import java.util.regex.Pattern;

/**
 * Utility class for validation operations
 */
public class ValidationUtils {

    // Regular expressions for validation
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{2,4}\\d{3,4}$");
    private static final Pattern ROOM_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{1,3}-\\d{3}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.'-]{2,50}$");

    /**
     * Validate that a string is not null or empty
     *
     * @param str the string to validate
     * @return true if valid
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validate an integer is within range
     *
     * @param value the value to check
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return true if within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validate course code format (e.g., CS101, MATH2001)
     *
     * @param courseCode the course code to validate
     * @return true if valid
     */
    public static boolean isValidCourseCode(String courseCode) {
        return isNotEmpty(courseCode) && COURSE_CODE_PATTERN.matcher(courseCode).matches();
    }

    /**
     * Validate room number format (e.g., A-101, LH-201)
     *
     * @param roomNumber the room number to validate
     * @return true if valid
     */
    public static boolean isValidRoomNumber(String roomNumber) {
        return isNotEmpty(roomNumber) && ROOM_NUMBER_PATTERN.matcher(roomNumber).matches();
    }

    /**
     * Validate email format
     *
     * @param email the email to validate
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate person name format
     *
     * @param name the name to validate
     * @return true if valid
     */
    public static boolean isValidName(String name) {
        return isNotEmpty(name) && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validate capacity is positive
     *
     * @param capacity the capacity to validate
     * @return true if valid
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0;
    }

    /**
     * Validate credits are within allowed range (typically 1-5)
     *
     * @param credits the credits to validate
     * @return true if valid
     */
    public static boolean isValidCredits(int credits) {
        return isInRange(credits, 1, 5);
    }

    /**
     * Validate hours are within allowed range (typically 0-6)
     *
     * @param hours the hours to validate
     * @return true if valid
     */
    public static boolean isValidHours(int hours) {
        return isInRange(hours, 0, 6);
    }

    /**
     * Validate an ID string
     *
     * @param id the ID to validate
     * @return true if valid
     */
    public static boolean isValidId(String id) {
        return isNotEmpty(id) && id.length() <= 20;
    }
}
