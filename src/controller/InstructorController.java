package controller;

import model.Instructor;
import model.InstructorDAO;
import model.TimeSlot;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InstructorController {
    private InstructorDAO instructorDAO;
    private List<Instructor> instructors;

    public InstructorController() {
        instructorDAO = new InstructorDAO();
        instructors = new ArrayList<>();
        loadInstructors();
    }

    // Load instructors from storage
    private void loadInstructors() {
        instructors = instructorDAO.loadInstructors();
    }

    // Get all instructors
    public List<Instructor> getAllInstructors() {
        return new ArrayList<>(instructors);
    }

    // Add a new instructor
    public boolean addInstructor(Instructor instructor) {
        // Check if instructor with same ID already exists
        if (getInstructorById(instructor.getId()) != null) {
            return false;
        }

        instructors.add(instructor);
        return saveInstructors();
    }

    // Update an existing instructor
    public boolean updateInstructor(Instructor updatedInstructor) {
        for (int i = 0; i < instructors.size(); i++) {
            if (instructors.get(i).getId().equals(updatedInstructor.getId())) {
                instructors.set(i, updatedInstructor);
                return saveInstructors();
            }
        }
        return false;
    }

    // Delete an instructor
    public boolean deleteInstructor(String id) {
        boolean removed = instructors.removeIf(i -> i.getId().equals(id));
        if (removed) {
            return saveInstructors();
        }
        return false;
    }

    // Get an instructor by ID
    public Instructor getInstructorById(String id) {
        return instructors.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Get an instructor by name
    public Instructor getInstructorByName(String name) {
        return instructors.stream()
                .filter(i -> i.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // Filter instructors by specialization
    public List<Instructor> getInstructorsBySpecialization(String specialization) {
        return instructors.stream()
                .filter(i -> i.getSpecialization().equals(specialization))
                .collect(Collectors.toList());
    }

    // Filter instructors who can teach a specific course
    public List<Instructor> getInstructorsForCourse(String courseCode) {
        return instructors.stream()
                .filter(i -> i.canTeach(courseCode))
                .collect(Collectors.toList());
    }

    // Save all instructors to storage
    public boolean saveInstructors() {
        return instructorDAO.saveInstructors(instructors);
    }

    // Update the courses an instructor can teach
    public boolean setCourses(String instructorId, String[] courses) {
        Instructor instructor = getInstructorById(instructorId);
        if (instructor != null) {
            instructor.setCourses(courses);
            return saveInstructors();
        }
        return false;
    }

    // Add a course to an instructor's teaching list
    public boolean addCourseToInstructor(String instructorId, String courseCode) {
        Instructor instructor = getInstructorById(instructorId);
        if (instructor != null) {
            String[] currentCourses = instructor.getCourses();
            if (currentCourses == null) {
                instructor.setCourses(new String[]{courseCode});
            } else {
                // Check if course already exists
                if (Arrays.asList(currentCourses).contains(courseCode)) {
                    return true; // Course already assigned
                }

                String[] newCourses = new String[currentCourses.length + 1];
                System.arraycopy(currentCourses, 0, newCourses, 0, currentCourses.length);
                newCourses[currentCourses.length] = courseCode;
                instructor.setCourses(newCourses);
            }
            return saveInstructors();
        }
        return false;
    }

    // Update preferred time slots for an instructor
    public boolean setPreferredTimeSlots(String instructorId, TimeSlot[] timeSlots) {
        Instructor instructor = getInstructorById(instructorId);
        if (instructor != null) {
            instructor.setPreferredTimeSlots(timeSlots);
            return saveInstructors();
        }
        return false;
    }

    // Check if an instructor exists
    public boolean instructorExists(String id) {
        return getInstructorById(id) != null;
    }

    // Get total number of instructors
    public int getInstructorCount() {
        return instructors.size();
    }

    // Refresh data from storage
    public void refreshInstructors() {
        loadInstructors();
    }
}
