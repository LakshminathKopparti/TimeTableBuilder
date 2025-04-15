package model;

import java.util.Arrays;
import java.util.Objects;
import java.io.Serializable;

public class Instructor implements Serializable {
    private String id;
    private String name;
    private String specialization;
    private TimeSlot[] preferredTimeSlots;
    private String[] courses; // Courses the instructor can teach

    // Constructors
    public Instructor() {
    }

    public Instructor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Instructor(String id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public TimeSlot[] getPreferredTimeSlots() {
        return preferredTimeSlots;
    }

    public void setPreferredTimeSlots(TimeSlot[] preferredTimeSlots) {
        this.preferredTimeSlots = preferredTimeSlots;
    }

    public String[] getCourses() {
        return courses;
    }

    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    // Method to check if an instructor can teach a course
    public boolean canTeach(String courseCode) {
        if (courses == null || courses.length == 0) {
            return false;
        }
        return Arrays.asList(courses).contains(courseCode);
    }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor instructor = (Instructor) o;
        return Objects.equals(id, instructor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
