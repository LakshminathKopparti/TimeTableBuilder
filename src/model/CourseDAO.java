package model;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final String CSV_SEPARATOR = ",";
    private static final String FILE_PATH = "data/courses.csv";

    // Save all courses to CSV
    public boolean saveCourses(List<Course> courses) {
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
                if (course.hasPreferredTimeSlots() && course.getPreferredTimeSlots() != null) {
                    StringBuilder slots = new StringBuilder();
                    for (TimeSlot slot : course.getPreferredTimeSlots()) {
                        slots.append(slot.getDay()).append(":")
                                .append(slot.getStartTime()).append("-")
                                .append(slot.getEndTime()).append("|");
                    }
                    // Remove trailing pipe if exists
                    if (slots.length() > 0) {
                        slots.setLength(slots.length() - 1);
                    }
                    sb.append(slots);
                }

                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load all courses from CSV
    public List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return courses; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(CSV_SEPARATOR);

                if (fields.length >= 5) {
                    Course course = new Course();
                    course.setCourseCode(fields[0]);
                    course.setName(fields[1]);
                    course.setCredits(Integer.parseInt(fields[2]));
                    course.setLectureHours(Integer.parseInt(fields[3]));
                    course.setLabHours(Integer.parseInt(fields[4]));

                    if (fields.length >= 6 && !fields[5].isEmpty()) {
                        String[] slotStrings = fields[5].split("\\|");
                        TimeSlot[] slots = new TimeSlot[slotStrings.length];

                        for (int i = 0; i < slotStrings.length; i++) {
                            String[] parts = slotStrings[i].split(":");
                            String day = parts[0];
                            String[] times = parts[1].split("-");

                            slots[i] = new TimeSlot(
                                    DayOfWeek.valueOf(day),
                                    LocalTime.parse(times[0]),
                                    LocalTime.parse(times[1])
                            );
                        }

                        course.setPreferredTimeSlots(slots);
                    }

                    courses.add(course);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return courses;
    }
}
