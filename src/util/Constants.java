package util;

import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Constants used throughout the application
 */
public class Constants {

    // Application info
    public static final String APP_NAME = "TimeTable Builder";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "BITS Pilani";

    // File paths and directories
    public static final String DATA_DIR = "data";
    public static final String CLASSROOM_FILE = DATA_DIR + "/classrooms.csv";
    public static final String COURSE_FILE = DATA_DIR + "/courses.csv";
    public static final String INSTRUCTOR_FILE = DATA_DIR + "/instructors.csv";
    public static final String TIMETABLE_DIR = DATA_DIR + "/timetables";
    public static final String TIMETABLE_EXTENSION = ".ttb";

    // UI Constants
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    public static final int DIALOG_WIDTH = 500;
    public static final int DIALOG_HEIGHT = 400;
    public static final int BORDER_PADDING = 10;
    public static final int COMPONENT_SPACING = 5;

    // Colors
    public static final Color LECTURE_COLOR = new Color(230, 242, 255);
    public static final Color LAB_COLOR = new Color(230, 255, 230);
    public static final Color CONFLICT_COLOR = new Color(255, 200, 200);
    public static final Color HEADER_COLOR = new Color(240, 240, 240);
    public static final Color SELECTION_COLOR = new Color(51, 153, 255);

    // Timetable display constants
    public static final LocalTime TIMETABLE_START_TIME = LocalTime.of(8, 0);
    public static final LocalTime TIMETABLE_END_TIME = LocalTime.of(18, 0);
    public static final int TIMETABLE_CELL_HEIGHT = 60;
    public static final int TIMETABLE_CELL_WIDTH = 150;

    // Academic constants
    public static final int MIN_CREDITS = 1;
    public static final int MAX_CREDITS = 5;
    public static final int MAX_COURSE_HOURS = 6;
    public static final int MAX_LAB_HOURS = 3;
    public static final int MIN_CLASSROOM_CAPACITY = 10;
    public static final int MAX_CLASSROOM_CAPACITY = 300;

    // Days of week for timetable (Monday to Friday)
    public static final DayOfWeek[] WEEKDAYS = {
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
    };

    // Time slots (1 hour each from 8 AM to 6 PM)
    public static final String[] TIME_SLOTS = {
            "08:00-09:00",
            "09:00-10:00",
            "10:00-11:00",
            "11:00-12:00",
            "12:00-13:00",
            "13:00-14:00",
            "14:00-15:00",
            "15:00-16:00",
            "16:00-17:00",
            "17:00-18:00"
    };

    // Default values
    public static final int DEFAULT_CAPACITY = 60;
    public static final int DEFAULT_CREDITS = 3;
    public static final int DEFAULT_LECTURE_HOURS = 3;
    public static final int DEFAULT_LAB_HOURS = 0;

    // BITS Policy Constants
    public static final boolean REQUIRE_DAY_GAP = true;
    public static final int MAX_CONSECUTIVE_HOURS = 3;
    public static final int MAX_DAILY_HOURS = 6;
    public static final int MAX_INSTRUCTOR_WEEKLY_HOURS = 20;

    // Export/Import constants
    public static final String CSV_SEPARATOR = ",";
    public static final String[] EXPORT_FORMATS = {"CSV", "HTML", "JSON"};
}
