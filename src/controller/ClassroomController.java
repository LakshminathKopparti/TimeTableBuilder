package controller;

import model.Classroom;
import model.ClassroomDAO;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClassroomController {
    private ClassroomDAO classroomDAO;
    private List<Classroom> classrooms;

    public ClassroomController() {
        classroomDAO = new ClassroomDAO();
        classrooms = new ArrayList<>();
        loadClassrooms();
    }

    // Load classrooms from storage
    private void loadClassrooms() {
        classrooms = classroomDAO.loadClassrooms();
    }

    // Get all classrooms
    public List<Classroom> getAllClassrooms() {
        return new ArrayList<>(classrooms);
    }

    // Add a new classroom
    public boolean addClassroom(Classroom classroom) {
        // Check if classroom with same room number already exists
        if (getClassroomByRoomNumber(classroom.getRoomNumber()) != null) {
            return false;
        }

        classrooms.add(classroom);
        return saveClassrooms();
    }

    // Update an existing classroom
    public boolean updateClassroom(Classroom updatedClassroom) {
        for (int i = 0; i < classrooms.size(); i++) {
            if (classrooms.get(i).getRoomNumber().equals(updatedClassroom.getRoomNumber())) {
                classrooms.set(i, updatedClassroom);
                return saveClassrooms();
            }
        }
        return false;
    }

    // Delete a classroom
    public boolean deleteClassroom(String roomNumber) {
        boolean removed = classrooms.removeIf(c -> c.getRoomNumber().equals(roomNumber));
        if (removed) {
            return saveClassrooms();
        }
        return false;
    }

    // Get a classroom by room number
    public Classroom getClassroomByRoomNumber(String roomNumber) {
        return classrooms.stream()
                .filter(c -> c.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElse(null);
    }

    // Filter classrooms by capacity
    public List<Classroom> getClassroomsByMinCapacity(int minCapacity) {
        return classrooms.stream()
                .filter(c -> c.getCapacity() >= minCapacity)
                .collect(Collectors.toList());
    }

    // Filter classrooms by facilities
    public List<Classroom> getClassroomsWithProjector() {
        return classrooms.stream()
                .filter(Classroom::hasProjector)
                .collect(Collectors.toList());
    }

    public List<Classroom> getClassroomsWithAC() {
        return classrooms.stream()
                .filter(Classroom::hasAC)
                .collect(Collectors.toList());
    }

    // Save all classrooms to storage
    public boolean saveClassrooms() {
        return classroomDAO.saveClassrooms(classrooms);
    }

    // Check if a classroom exists
    public boolean classroomExists(String roomNumber) {
        return getClassroomByRoomNumber(roomNumber) != null;
    }

    // Get total number of classrooms
    public int getClassroomCount() {
        return classrooms.size();
    }

    // Refresh data from storage
    public void refreshClassrooms() {
        loadClassrooms();
    }
}
