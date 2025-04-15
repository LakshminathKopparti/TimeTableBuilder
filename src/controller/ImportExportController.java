package controller;

import model.*;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ImportExportController {
    private static final String CSV_SEPARATOR = ",";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Export timetable to CSV
    public boolean exportTimetableToCSV(Timetable timetable, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            // Write header
            writer.println("Day,StartTime,EndTime,CourseCode,CourseName,Instructor,Classroom,IsLab");

            // Sort schedules by day and time for easier reading
            List<CourseSchedule> sortedSchedules = new ArrayList<>(timetable.getSchedules());
            sortedSchedules.sort((s1, s2) -> {
                int dayCompare = s1.getTimeSlot().getDay().compareTo(s2.getTimeSlot().getDay());
                if (dayCompare != 0) return dayCompare;
                return s1.getTimeSlot().getStartTime().compareTo(s2.getTimeSlot().getStartTime());
            });

            // Write schedule data
            for (CourseSchedule schedule : sortedSchedules) {
                StringBuilder sb = new StringBuilder();

                TimeSlot slot = schedule.getTimeSlot();
                Course course = schedule.getCourse();
                Instructor instructor = schedule.getInstructor();
                Classroom classroom = schedule.getClassroom();

                sb.append(slot.getDay()).append(CSV_SEPARATOR);
                sb.append(slot.getStartTime().format(TIME_FORMATTER)).append(CSV_SEPARATOR);
                sb.append(slot.getEndTime().format(TIME_FORMATTER)).append(CSV_SEPARATOR);
                sb.append(course.getCourseCode()).append(CSV_SEPARATOR);
                sb.append(course.getName()).append(CSV_SEPARATOR);
                sb.append(instructor.getName()).append(CSV_SEPARATOR);
                sb.append(classroom.getRoomNumber()).append(CSV_SEPARATOR);
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
    public Timetable importTimetableFromCSV(String name, String filePath,
                                            CourseController courseController,
                                            InstructorController instructorController,
                                            ClassroomController classroomController) {
        Timetable timetable = new Timetable(name);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header
            String line = reader.readLine();

            // Read data rows
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(CSV_SEPARATOR);

                if (fields.length < 8) continue;

                // Parse the fields
                DayOfWeek day = DayOfWeek.valueOf(fields[0]);
                LocalTime startTime = LocalTime.parse(fields[1], TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(fields[2], TIME_FORMATTER);
                String courseCode = fields[3];
                String instructorName = fields[5];
                String roomNumber = fields[6];
                boolean isLab = Boolean.parseBoolean(fields[7]);

                // Get the corresponding objects
                Course course = courseController.getCourseByCourseCode(courseCode);
                Instructor instructor = instructorController.getInstructorByName(instructorName);
                Classroom classroom = classroomController.getClassroomByRoomNumber(roomNumber);

                // Skip if any object not found
                if (course == null || instructor == null || classroom == null) {
                    continue;
                }

                // Create timeslot and schedule
                TimeSlot timeSlot = new TimeSlot(day, startTime, endTime, isLab);
                CourseSchedule schedule = new CourseSchedule(course, instructor, classroom, timeSlot, isLab);

                // Add to timetable
                timetable.addSchedule(schedule);
            }

            return timetable;
        } catch (IOException e) {
            e.printStackTrace();
            return timetable;
        }
    }

    // Export timetable to HTML for better visualization
    public boolean exportTimetableToHTML(Timetable timetable, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            // Write HTML header
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang=\"en\">");
            writer.println("<head>");
            writer.println("  <meta charset=\"UTF-8\">");
            writer.println("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            writer.println("  <title>Timetable: " + timetable.getName() + "</title>");
            writer.println("  <style>");
            writer.println("    body { font-family: Arial, sans-serif; margin: 20px; }");
            writer.println("    table { border-collapse: collapse; width: 100%; }");
            writer.println("    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            writer.println("    th { background-color: #f2f2f2; }");
            writer.println("    .lecture { background-color: #e6f7ff; }");
            writer.println("    .lab { background-color: #e6ffe6; }");
            writer.println("    h1 { color: #333; }");
            writer.println("  </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("  <h1>Timetable: " + timetable.getName() + "</h1>");

            // Create timetable grid
            writer.println("  <table>");
            writer.println("    <tr>");
            writer.println("      <th>Time/Day</th>");
            writer.println("      <th>Monday</th>");
            writer.println("      <th>Tuesday</th>");
            writer.println("      <th>Wednesday</th>");
            writer.println("      <th>Thursday</th>");
            writer.println("      <th>Friday</th>");
            writer.println("    </tr>");

            // Define time slots
            LocalTime startTime = LocalTime.of(8, 0);
            LocalTime endTime = LocalTime.of(18, 0);

            // Create a map to store schedules by day and time
            Map<DayOfWeek, Map<LocalTime, List<CourseSchedule>>> scheduleMap = new HashMap<>();
            for (DayOfWeek day : DayOfWeek.values()) {
                if (day.getValue() >= 1 && day.getValue() <= 5) { // Monday to Friday
                    scheduleMap.put(day, new HashMap<>());
                }
            }

            // Populate the map
            for (CourseSchedule schedule : timetable.getSchedules()) {
                DayOfWeek day = schedule.getTimeSlot().getDay();
                if (day.getValue() >= 1 && day.getValue() <= 5) {
                    LocalTime time = schedule.getTimeSlot().getStartTime();
                    if (!scheduleMap.get(day).containsKey(time)) {
                        scheduleMap.get(day).put(time, new ArrayList<>());
                    }
                    scheduleMap.get(day).get(time).add(schedule);
                }
            }

            // Generate rows for each hour
            while (startTime.isBefore(endTime)) {
                LocalTime slotEnd = startTime.plusHours(1);

                writer.println("    <tr>");
                writer.println("      <td>" + startTime.format(TIME_FORMATTER) + " - " +
                        slotEnd.format(TIME_FORMATTER) + "</td>");

                // For each day
                for (int i = 1; i <= 5; i++) {
                    DayOfWeek day = DayOfWeek.of(i);
                    writer.println("      <td>");

                    // Check if there are any schedules for this day and time
                    if (scheduleMap.get(day).containsKey(startTime)) {
                        for (CourseSchedule schedule : scheduleMap.get(day).get(startTime)) {
                            String cssClass = schedule.isLab() ? "lab" : "lecture";
                            writer.println("        <div class=\"" + cssClass + "\">");
                            writer.println("          " + schedule.getCourse().getCourseCode() + "<br>");
                            writer.println("          " + schedule.getInstructor().getName() + "<br>");
                            writer.println("          " + schedule.getClassroom().getRoomNumber());
                            writer.println("        </div>");
                        }
                    }

                    writer.println("      </td>");
                }

                writer.println("    </tr>");
                startTime = slotEnd;
            }

            writer.println("  </table>");

            // Add legend
            writer.println("  <div style=\"margin-top: 20px;\">");
            writer.println("    <div style=\"display: inline-block; width: 20px; height: 20px; background-color: #e6f7ff; margin-right: 5px;\"></div>");
            writer.println("    <span>Lecture</span>");
            writer.println("    <div style=\"display: inline-block; width: 20px; height: 20px; background-color: #e6ffe6; margin-right: 5px; margin-left: 15px;\"></div>");
            writer.println("    <span>Lab</span>");
            writer.println("  </div>");

            // Add generation timestamp
            writer.println("  <p style=\"margin-top: 20px; color: #777; font-size: 0.8em;\">");
            writer.println("    Generated on: " + new Date());
            writer.println("  </p>");

            writer.println("</body>");
            writer.println("</html>");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Export data to JSON format
    public boolean exportDataToJSON(List<Course> courses, List<Instructor> instructors,
                                    List<Classroom> classrooms, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            // Create simple JSON structure (could use a JSON library for more complex needs)
            writer.println("{");

            // Export courses
            writer.println("  \"courses\": [");
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                writer.println("    {");
                writer.println("      \"courseCode\": \"" + course.getCourseCode() + "\",");
                writer.println("      \"name\": \"" + course.getName() + "\",");
                writer.println("      \"credits\": " + course.getCredits() + ",");
                writer.println("      \"lectureHours\": " + course.getLectureHours() + ",");
                writer.println("      \"labHours\": " + course.getLabHours());
                writer.println("    }" + (i < courses.size() - 1 ? "," : ""));
            }
            writer.println("  ],");

            // Export instructors
            writer.println("  \"instructors\": [");
            for (int i = 0; i < instructors.size(); i++) {
                Instructor instructor = instructors.get(i);
                writer.println("    {");
                writer.println("      \"id\": \"" + instructor.getId() + "\",");
                writer.println("      \"name\": \"" + instructor.getName() + "\",");
                writer.println("      \"specialization\": \"" + instructor.getSpecialization() + "\"");
                writer.println("    }" + (i < instructors.size() - 1 ? "," : ""));
            }
            writer.println("  ],");

            // Export classrooms
            writer.println("  \"classrooms\": [");
            for (int i = 0; i < classrooms.size(); i++) {
                Classroom classroom = classrooms.get(i);
                writer.println("    {");
                writer.println("      \"roomNumber\": \"" + classroom.getRoomNumber() + "\",");
                writer.println("      \"capacity\": " + classroom.getCapacity() + ",");
                writer.println("      \"hasProjector\": " + classroom.hasProjector() + ",");
                writer.println("      \"hasAC\": " + classroom.hasAC());
                writer.println("    }" + (i < classrooms.size() - 1 ? "," : ""));
            }
            writer.println("  ]");

            writer.println("}");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
