package controller;

import model.Course;
import model.CourseDAO;
import model.TimeSlot;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CourseController {
    private CourseDAO courseDAO;
    private List<Course> courses;

    public CourseController() {
        courseDAO = new CourseDAO();
        courses = new ArrayList<>();
        loadCourses();
    }

    // Load courses from storage
    private void loadCourses() {
        courses = courseDAO.loadCourses();
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    // Add a new course
    public boolean addCourse(Course course) {
        // Check if course with same course code already exists
        if (getCourseByCourseCode(course.getCourseCode()) != null) {
            return false;
        }

        courses.add(course);
        return saveCourses();
    }

    // Update an existing course
    public boolean updateCourse(Course updatedCourse) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(updatedCourse.getCourseCode())) {
                courses.set(i, updatedCourse);
                return saveCourses();
            }
        }
        return false;
    }

    // Delete a course
    public boolean deleteCourse(String courseCode) {
        boolean removed = courses.removeIf(c -> c.getCourseCode().equals(courseCode));
        if (removed) {
            return saveCourses();
        }
        return false;
    }

    // Get a course by course code
    public Course getCourseByCourseCode(String courseCode) {
        return courses.stream()
                .filter(c -> c.getCourseCode().equals(courseCode))
                .findFirst()
                .orElse(null);
    }

    // Filter courses by credits
    public List<Course> getCoursesByCredits(int credits) {
        return courses.stream()
                .filter(c -> c.getCredits() == credits)
                .collect(Collectors.toList());
    }

    // Filter courses by presence of lab component
    public List<Course> getCoursesWithLab() {
        return courses.stream()
                .filter(c -> c.getLabHours() > 0)
                .collect(Collectors.toList());
    }

    // Save all courses to storage
    public boolean saveCourses() {
        return courseDAO.saveCourses(courses);
    }

    // Update preferred time slots for a course
    public boolean setPreferredTimeSlots(String courseCode, TimeSlot[] timeSlots) {
        Course course = getCourseByCourseCode(courseCode);
        if (course != null) {
            course.setPreferredTimeSlots(timeSlots);
            return saveCourses();
        }
        return false;
    }

    // Check if a course exists
    public boolean courseExists(String courseCode) {
        return getCourseByCourseCode(courseCode) != null;
    }

    // Get total number of courses
    public int getCourseCount() {
        return courses.size();
    }

    // Refresh data from storage
    public void refreshCourses() {
        loadCourses();
    }
}
