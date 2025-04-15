package model;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {
    private static final String CSV_SEPARATOR = ",";
    private static final String FILE_PATH = "data/instructors.csv";

    // Save all instructors to CSV
    public boolean saveInstructors(List<Instructor> instructors) {
        try (PrintWriter writer = new PrintWriter(new File(FILE_PATH))) {
            // Write header
            writer.println("ID,Name,Specialization,PreferredTimeSlots,Courses");

            // Write data
            for (Instructor instructor : instructors) {
                StringBuilder sb = new StringBuilder();
                sb.append(instructor.getId()).append(CSV_SEPARATOR);
                sb.append(instructor.getName()).append(CSV_SEPARATOR);
                sb.append(instructor.getSpecialization()).append(CSV_SEPARATOR);

                // Handle preferred time slots
                if (instructor.getPreferredTimeSlots() != null) {
                    StringBuilder slots = new StringBuilder();
                    for (TimeSlot slot : instructor.getPreferredTimeSlots()) {
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
                sb.append(CSV_SEPARATOR);

                // Handle courses
                if (instructor.getCourses() != null) {
                    sb.append(String.join("|", instructor.getCourses()));
                }

                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load all instructors from CSV
    public List<Instructor> loadInstructors() {
        List<Instructor> instructors = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return instructors; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(CSV_SEPARATOR);

                if (fields.length >= 3) {
                    Instructor instructor = new Instructor();
                    instructor.setId(fields[0]);
                    instructor.setName(fields[1]);
                    instructor.setSpecialization(fields[2]);

                    if (fields.length >= 4 && !fields[3].isEmpty()) {
                        String[] slotStrings = fields[3].split("\\|");
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

                        instructor.setPreferredTimeSlots(slots);
                    }

                    if (fields.length >= 5 && !fields[4].isEmpty()) {
                        instructor.setCourses(fields[4].split("\\|"));
                    }

                    instructors.add(instructor);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instructors;
    }
}
