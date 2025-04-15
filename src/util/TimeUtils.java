package util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for time operations
 */
public class TimeUtils {

    private static final DateTimeFormatter TIME_FORMATTER_12H = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter TIME_FORMATTER_24H = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Format time in 12-hour format
     *
     * @param time the LocalTime to format
     * @return formatted time string
     */
    public static String format12Hour(LocalTime time) {
        return time.format(TIME_FORMATTER_12H);
    }

    /**
     * Format time in 24-hour format
     *
     * @param time the LocalTime to format
     * @return formatted time string
     */
    public static String format24Hour(LocalTime time) {
        return time.format(TIME_FORMATTER_24H);
    }

    /**
     * Format time based on user preference
     *
     * @param time the LocalTime to format
     * @param use24Hour whether to use 24-hour format
     * @return formatted time string
     */
    public static String formatTime(LocalTime time, boolean use24Hour) {
        return use24Hour ? format24Hour(time) : format12Hour(time);
    }

    /**
     * Parse time string to LocalTime
     *
     * @param timeString the time string
     * @param is24Hour whether the string is in 24-hour format
     * @return LocalTime object or null if parsing fails
     */
    public static LocalTime parseTime(String timeString, boolean is24Hour) {
        try {
            if (is24Hour) {
                return LocalTime.parse(timeString, TIME_FORMATTER_24H);
            } else {
                return LocalTime.parse(timeString, TIME_FORMATTER_12H);
            }
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Check if two time slots overlap
     *
     * @param day1 day of week for first slot
     * @param start1 start time for first slot
     * @param end1 end time for first slot
     * @param day2 day of week for second slot
     * @param start2 start time for second slot
     * @param end2 end time for second slot
     * @return true if the slots overlap
     */
    public static boolean timeSlotOverlap(DayOfWeek day1, LocalTime start1, LocalTime end1,
                                          DayOfWeek day2, LocalTime start2, LocalTime end2) {
        // Different days, no overlap
        if (day1 != day2) {
            return false;
        }

        // Check time overlap on the same day
        return (start1.isBefore(end2) && end1.isAfter(start2)) ||
                start1.equals(start2) || end1.equals(end2);
    }

    /**
     * Check if two days are adjacent (considering Monday-Friday week)
     *
     * @param day1 first day
     * @param day2 second day
     * @return true if the days are adjacent
     */
    public static boolean areAdjacentDays(DayOfWeek day1, DayOfWeek day2) {
        int value1 = day1.getValue();
        int value2 = day2.getValue();

        // Consider only Monday (1) to Friday (5)
        if (value1 > 5 || value2 > 5) {
            return false;
        }

        // Direct adjacent days
        if (Math.abs(value1 - value2) == 1) {
            return true;
        }

        // Special case: Friday (5) and Monday (1) are considered adjacent
        return (value1 == 1 && value2 == 5) || (value1 == 5 && value2 == 1);
    }

    /**
     * Generate a list of standard time slots for the day
     *
     * @param startHour starting hour (24-hour format)
     * @param endHour ending hour (24-hour format)
     * @param intervalMinutes interval in minutes
     * @return list of LocalTime objects
     */
    public static List<LocalTime> generateTimeSlots(int startHour, int endHour, int intervalMinutes) {
        List<LocalTime> timeSlots = new ArrayList<>();

        LocalTime currentTime = LocalTime.of(startHour, 0);
        LocalTime endTime = LocalTime.of(endHour, 0);

        while (currentTime.isBefore(endTime) || currentTime.equals(endTime)) {
            timeSlots.add(currentTime);
            currentTime = currentTime.plusMinutes(intervalMinutes);
        }

        return timeSlots;
    }

    /**
     * Calculate duration in minutes between two times
     *
     * @param start start time
     * @param end end time
     * @return duration in minutes
     */
    public static int getDurationMinutes(LocalTime start, LocalTime end) {
        return (end.getHour() - start.getHour()) * 60 + (end.getMinute() - start.getMinute());
    }

    /**
     * Get working days (Monday to Friday)
     *
     * @return list of working days
     */
    public static List<DayOfWeek> getWorkingDays() {
        List<DayOfWeek> workingDays = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            workingDays.add(DayOfWeek.of(i));
        }
        return workingDays;
    }
}
