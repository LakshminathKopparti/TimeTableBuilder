package controller;

import model.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.io.File;

public class TimeTableController {
    private TimetableDAO timetableDAO;
    private Timetable currentTimetable;
    private List<Timetable> generatedTimetables; // For storing auto-generated suggestions
    private int currentSuggestionIndex;

    public TimeTableController() {
        timetableDAO = new TimetableDAO();
        currentTimetable = new Timetable("Default");
        generatedTimetables = new ArrayList<>();
        currentSuggestionIndex = -1;
    }
    /**
     * Get the current suggestion index
     * @return The current index in the generated timetable suggestions list
     */
    public int getCurrentSuggestionIndex() {
        return currentSuggestionIndex;
    }
    /**
     * Get all courses in the current timetable
     * @return List of all courses
     */
    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        for (CourseSchedule schedule : currentTimetable.getSchedules()) {
            Course course = schedule.getCourse();
            if (!courses.contains(course)) {
                courses.add(course);
            }
        }
        return courses;
    }

    /**
     * Get all instructors in the current timetable
     * @return List of all instructors
     */
    public List<Instructor> getInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        for (CourseSchedule schedule : currentTimetable.getSchedules()) {
            Instructor instructor = schedule.getInstructor();
            if (!instructors.contains(instructor)) {
                instructors.add(instructor);
            }
        }
        return instructors;
    }

    /**
     * Get all classrooms in the current timetable
     * @return List of all classrooms
     */
    public List<Classroom> getClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        for (CourseSchedule schedule : currentTimetable.getSchedules()) {
            Classroom classroom = schedule.getClassroom();
            if (!classrooms.contains(classroom)) {
                classrooms.add(classroom);
            }
        }
        return classrooms;
    }
    /**
     * Get all courses in the current timetable
     * @return List of all courses
     */


    /**
     * Get all instructors in the current timetable
     * @return List of all instructors
     */


    /**
     * Get all classrooms in the current timetable
     * @return List of all classrooms
     */
    // Timetable management methods
    public Timetable getCurrentTimetable() {
        return currentTimetable;
    }

    public void createNewTimetable(String name) {
        currentTimetable = new Timetable(name);
    }

    public boolean saveTimetable() {
        return timetableDAO.saveTimetable(currentTimetable);
    }

    public boolean saveTimetableAs(String name) {
        currentTimetable.setName(name);
        return timetableDAO.saveTimetable(currentTimetable);
    }

    public boolean loadTimetable(String name) {
        Timetable loaded = timetableDAO.loadTimetable(name);
        if (loaded != null) {
            currentTimetable = loaded;
            return true;
        }
        return false;
    }

    public List<String> getAllTimetableNames() {
        return timetableDAO.getAllTimetableNames();
    }

    public boolean deleteTimetable(String name) {
        return timetableDAO.deleteTimetable(name);
    }

    // Schedule management methods
    public boolean addSchedule(CourseSchedule schedule) {
        return currentTimetable.addSchedule(schedule);
    }

    public boolean addSchedule(Course course, Instructor instructor, Classroom classroom, TimeSlot timeSlot, boolean isLab) {
        CourseSchedule schedule = new CourseSchedule(course, instructor, classroom, timeSlot, isLab);
        return currentTimetable.addSchedule(schedule);
    }

    public boolean removeSchedule(CourseSchedule schedule) {
        return currentTimetable.removeSchedule(schedule);
    }

    public List<CourseSchedule> getSchedulesForCourse(Course course) {
        return currentTimetable.getSchedulesForCourse(course);
    }

    public List<CourseSchedule> getSchedulesForInstructor(Instructor instructor) {
        return currentTimetable.getSchedulesForInstructor(instructor);
    }

    public List<CourseSchedule> getSchedulesForClassroom(Classroom classroom) {
        return currentTimetable.getSchedulesForClassroom(classroom);
    }

    public List<CourseSchedule> getAllSchedules() {
        return currentTimetable.getSchedules();
    }

    // Timetable suggestion methods
    public void generateTimetableSuggestions(List<Course> courses, List<Instructor> instructors,
                                             List<Classroom> classrooms, List<TimeSlot> availableTimeSlots) {
        // Clear previous suggestions
        generatedTimetables.clear();
        currentSuggestionIndex = -1;

        // Use AutoScheduler to generate suggestions
        AutoScheduler scheduler = new AutoScheduler();
        generatedTimetables = scheduler.generateSuggestions(
                courses, instructors, classrooms, availableTimeSlots, 5); // Generate 5 suggestions

        if (!generatedTimetables.isEmpty()) {
            currentSuggestionIndex = 0;
            currentTimetable = generatedTimetables.get(0);
        }
    }

    public boolean hasNextSuggestion() {
        return currentSuggestionIndex < generatedTimetables.size() - 1;
    }

    public boolean hasPreviousSuggestion() {
        return currentSuggestionIndex > 0;
    }

    public Timetable nextSuggestion() {
        if (hasNextSuggestion()) {
            currentSuggestionIndex++;
            currentTimetable = generatedTimetables.get(currentSuggestionIndex);
            return currentTimetable;
        }
        return null;
    }

    public Timetable previousSuggestion() {
        if (hasPreviousSuggestion()) {
            currentSuggestionIndex--;
            currentTimetable = generatedTimetables.get(currentSuggestionIndex);
            return currentTimetable;
        }
        return null;
    }

    // Import/Export methods
    public boolean exportToCSV(String filePath) {
        return timetableDAO.exportToCSV(currentTimetable, filePath);
    }

    public boolean importFromCSV(String name, String filePath,
                                 List<Course> courses, List<Instructor> instructors, List<Classroom> classrooms) {
        Timetable imported = timetableDAO.importFromCSV(name, filePath, courses, instructors, classrooms);
        if (imported != null) {
            currentTimetable = imported;
            return true;
        }
        return false;
    }

    // Validation methods
    public boolean validateTimetable() {
        return currentTimetable.validateBITSPolicies();
    }

    public List<String> getPolicyViolations() {
        ConflictChecker checker = new ConflictChecker();
        return checker.checkBITSPolicyCompliance(currentTimetable);
    }
}
