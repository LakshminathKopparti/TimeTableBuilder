package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDAO {
    private static final String CSV_SEPARATOR = ",";
    private static final String FILE_PATH = "data/classrooms.csv";

    // Save all classrooms to CSV
    public boolean saveClassrooms(List<Classroom> classrooms) {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs(); // Create all necessary parent directories
        }
        try (PrintWriter writer = new PrintWriter(new File(FILE_PATH))) {
            // Write header
            writer.println("RoomNumber,Capacity,HasProjector,HasAC,AdditionalFacilities");

            // Write data
            for (Classroom classroom : classrooms) {
                StringBuilder sb = new StringBuilder();
                sb.append(classroom.getRoomNumber()).append(CSV_SEPARATOR);
                sb.append(classroom.getCapacity()).append(CSV_SEPARATOR);
                sb.append(classroom.hasProjector()).append(CSV_SEPARATOR);
                sb.append(classroom.hasAC()).append(CSV_SEPARATOR);

                // Handle additional facilities
                if (classroom.getAdditionalFacilities() != null && classroom.getAdditionalFacilities().length > 0) {
                    sb.append(String.join("|", classroom.getAdditionalFacilities()));
                }

                writer.println(sb.toString());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load all classrooms from CSV
    public List<Classroom> loadClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return classrooms; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(CSV_SEPARATOR);

                if (fields.length >= 4) {
                    Classroom classroom = new Classroom();
                    classroom.setRoomNumber(fields[0]);
                    classroom.setCapacity(Integer.parseInt(fields[1]));
                    classroom.setHasProjector(Boolean.parseBoolean(fields[2]));
                    classroom.setHasAC(Boolean.parseBoolean(fields[3]));

                    if (fields.length >= 5 && !fields[4].isEmpty()) {
                        classroom.setAdditionalFacilities(fields[4].split("\\|"));
                    }

                    classrooms.add(classroom);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classrooms;
    }
}
