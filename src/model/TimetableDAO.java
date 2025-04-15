package model;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableDAO {
    private static final String FILE_EXTENSION = ".ttb";
    private static final String FILE_DIRECTORY = "data/timetables/";

    // Save a timetable to a file
    public boolean saveTimetable(Timetable timetable) {
        String filePath = FILE_DIRECTORY + timetable.getName() + FILE_EXTENSION;

        // Create directory if it doesn't exist
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // We'll save the entire timetable object
            oos.writeObject(timetable);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load a timetable from a file
    public Timetable loadTimetable(String name) {
        String filePath = FILE_DIRECTORY + name + FILE_EXTENSION;
        File file = new File(filePath);

        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Timetable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get a list of all saved timetables
    public List<String> getAllTimetableNames() {
        List<String> timetableNames = new ArrayList<>();
        File directory = new File(FILE_DIRECTORY);

        if (!directory.exists() || !directory.isDirectory()) {
            return timetableNames;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                // Remove extension
                name = name.substring(0, name.length() - FILE_EXTENSION.length());
                timetableNames.add(name);
            }
        }

        return timetableNames;
    }

    // Delete a timetable
    public boolean deleteTimetable(String name) {
        String filePath = FILE_DIRECTORY + name + FILE_EXTENSION;
        File file = new File(filePath);

        if (!file.exists()) {
            return false;
        }

        return file.delete();
    }

    // Export timetable to CSV
    public boolean exportToCSV(Timetable timetable, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            // Write header
            writer.println("Day,StartTime,EndTime,Course,Instructor,Classroom,IsLab");

            // Write data
            for (CourseSchedule schedule : timetable.getSchedules()) {
                StringBuilder sb = new StringBuilder();
                sb.append(schedule.getTimeSlot().getDay()).append(",");
                sb.append(schedule.getTimeSlot().getStartTime()).append(",");
                sb.append(schedule.getTimeSlot().getEndTime()).append(",");
                sb.append(schedule.getCourse().getCourseCode()).append(",");
                sb.append(schedule.getInstructor().getName()).append(",");
                sb.append(schedule.getClassroom().getRoomNumber()).append(",");
                sb.append(schedule.isLab());

                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Import timetable from CSV
    public Timetable importFromCSV(String name, String filePath,
                                   List<Course> courses, List<Instructor> instructors, List<Classroom> classrooms) {
        // Create maps for quick lookup
        Map<String, Course> courseMap = new HashMap<>();
        Map<String, Instructor> instructorMap = new HashMap<>();
        Map<String, Classroom> classroomMap = new HashMap<>();

        for (Course course : courses) {
            courseMap.put(course.getCourseCode(), course);
        }

        for (Instructor instructor : instructors) {
            instructorMap.put(instructor.getName(), instructor);
        }

        for (Classroom classroom : classrooms) {
            classroomMap.put(classroom.getRoomNumber(), classroom);
        }

        Timetable timetable = new Timetable(name);

        File file = new File(filePath);
        if (!file.exists()) {
            return timetable;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length >= 7) {
                    DayOfWeek day = DayOfWeek.valueOf(fields[0]);
                    LocalTime startTime = LocalTime.parse(fields[1]);
                    LocalTime endTime = LocalTime.parse(fields[2]);
                    TimeSlot timeSlot = new TimeSlot(day, startTime, endTime);

                    Course course = courseMap.get(fields[3]);
                    Instructor instructor = instructorMap.get(fields[4]);
                    Classroom classroom = classroomMap.get(fields[5]);
                    boolean isLab = Boolean.parseBoolean(fields[6]);

                    if (course != null && instructor != null && classroom != null) {
                        CourseSchedule schedule = new CourseSchedule(course, instructor, classroom, timeSlot, isLab);
                        timetable.addSchedule(schedule);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return timetable;
    }
}
