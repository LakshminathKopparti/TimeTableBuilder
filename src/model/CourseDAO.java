package model;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final String CSV_SEPARATOR = ",";
    private static final String FILE_PATH = "data/courses.csv";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Save all courses to CSV
    public boolean saveCourses(List<Course> courses) {
        // Create the data directory if it doesn't exist
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new File(FILE_PATH))) {
            // Write header
            writer.println("CourseCode,Name,Credits,LectureHours,LabHours,PreferredTimeSlots");
            
            // Write data
            for (Course course : courses) {
                StringBuilder sb = new StringBuilder();
                sb.append(course.getCourseCode()).append(CSV_SEPARATOR);
                sb.append(course.getName()).append(CSV_SEPARATOR);
                sb.append(course.getCredits()).append(CSV_SEPARATOR);
                sb.append(course.getLectureHours()).append(CSV_SEPARATOR);
                sb.append(course.getLabHours()).append(CSV_SEPARATOR);
                
                // Handle preferred time slots
                if (course.hasPreferredTimeSlots() && course.getPreferredTimeSlots() != null && 
                    course.getPreferredTimeSlots().length > 0) {
                    
                    TimeSlot[] timeSlots = course.getPreferredTimeSlots();
                    StringBuilder slotsBuilder = new StringBuilder();
                    
                    for (int i = 0; i < timeSlots.length; i++) {
                        TimeSlot slot = timeSlots[i];
                        // Format: DAY:HH:MM-HH:MM
                        slotsBuilder.append(slot.getDay().toString())
                                   .append(":")
                                   .append(slot.getStartTime().format(TIME_FORMATTER))
                                   .append("-")
                                   .append(slot.getEndTime().format(TIME_FORMATTER));
                        
                        if (i < timeSlots.length - 1) {
                            slotsBuilder.append("|"); // Separator for multiple time slots
                        }
                    }
                    
                    sb.append(slotsBuilder.toString());
                }
                
                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Load all courses from CSV
    public List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("Courses file not found: " + FILE_PATH);
            return courses; // Return empty list if file doesn't exist
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                String[] fields = line.split(CSV_SEPARATOR, -1); // -1 to keep trailing empty fields
                
                if (fields.length >= 5) {
                    Course course = new Course();
                    course.setCourseCode(fields[0]);
                    course.setName(fields[1]);
                    
                    try {
                        course.setCredits(Integer.parseInt(fields[2]));
                        course.setLectureHours(Integer.parseInt(fields[3]));
                        course.setLabHours(Integer.parseInt(fields[4]));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing numeric fields for course " + fields[0] + ": " + e.getMessage());
                        continue; // Skip this course on number format error
                    }
                    
                    // Parse preferred time slots if present
                    if (fields.length >= 6 && !fields[5].trim().isEmpty()) {
                        try {
                            List<TimeSlot> slots = new ArrayList<>();
                            String[] slotStrings = fields[5].split("\\|"); // Split multiple slots
                            
                            for (String slotString : slotStrings) {
                                // Parse slot in format DAY:HH:MM-HH:MM
                                String[] parts = slotString.split(":");
                                if (parts.length >= 2) {
                                    DayOfWeek day = DayOfWeek.valueOf(parts[0]);
                                    
                                    // Second part contains time range (HH:MM-HH:MM)
                                    String[] timeParts = parts[1].split("-");
                                    if (timeParts.length == 2) {
                                        LocalTime startTime = LocalTime.parse(timeParts[0], TIME_FORMATTER);
                                        LocalTime endTime = LocalTime.parse(timeParts[1], TIME_FORMATTER);
                                        
                                        TimeSlot slot = new TimeSlot(day, startTime, endTime);
                                        slots.add(slot);
                                    }
                                }
                            }
                            
                            if (!slots.isEmpty()) {
                                course.setPreferredTimeSlots(slots.toArray(new TimeSlot[0]));
                            }
                        } catch (IllegalArgumentException | DateTimeParseException e) {
                            System.err.println("Error parsing time slots for course " + fields[0] + ": " + e.getMessage());
                            // Continue without preferred time slots
                        }
                    }
                    
                    courses.add(course);
                } else {
                    System.err.println("Skipping invalid course data: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return courses;
    }
    
    // Get a course by course code
    public Course getCourseByCourseCode(String courseCode) {
        List<Course> courses = loadCourses();
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return course;
            }
        }
        return null;
    }
    
    // Delete a course
    public boolean deleteCourse(String courseCode) {
        List<Course> courses = loadCourses();
        boolean removed = courses.removeIf(c -> c.getCourseCode().equals(courseCode));
        
        if (removed) {
            return saveCourses(courses);
        }
        return false;
    }
    
    // Update a course
    public boolean updateCourse(Course updatedCourse) {
        List<Course> courses = loadCourses();
        
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(updatedCourse.getCourseCode())) {
                courses.set(i, updatedCourse);
                return saveCourses(courses);
            }
        }
        
        return false; // Course not found
    }
    
    // Add a course
    public boolean addCourse(Course course) {
        List<Course> courses = loadCourses();
        
        // Check if course with same code already exists
        for (Course existingCourse : courses) {
            if (existingCourse.getCourseCode().equals(course.getCourseCode())) {
                return false; // Course with this code already exists
            }
        }
        
        courses.add(course);
        return saveCourses(courses);
    }
}
