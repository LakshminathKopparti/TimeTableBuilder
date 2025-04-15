package model;

import java.util.Objects;
import java.io.Serializable;

public class Course implements Serializable {
    private String courseCode;
    private String name;
    private int credits;
    private int lectureHours;
    private int labHours;
    private boolean hasPreferredTimeSlots;
    private TimeSlot[] preferredTimeSlots;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String name, int credits) {
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
    }

    public Course(String courseCode, String name, int credits, int lectureHours, int labHours) {
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
        this.lectureHours = lectureHours;
        this.labHours = labHours;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getLectureHours() {
        return lectureHours;
    }

    public void setLectureHours(int lectureHours) {
        this.lectureHours = lectureHours;
    }

    public int getLabHours() {
        return labHours;
    }

    public void setLabHours(int labHours) {
        this.labHours = labHours;
    }

    public boolean hasPreferredTimeSlots() {
        return hasPreferredTimeSlots;
    }

    public void setHasPreferredTimeSlots(boolean hasPreferredTimeSlots) {
        this.hasPreferredTimeSlots = hasPreferredTimeSlots;
    }

    public TimeSlot[] getPreferredTimeSlots() {
        return preferredTimeSlots;
    }

    public void setPreferredTimeSlots(TimeSlot[] preferredTimeSlots) {
        this.preferredTimeSlots = preferredTimeSlots;
        this.hasPreferredTimeSlots = preferredTimeSlots != null && preferredTimeSlots.length > 0;
    }

    @Override
    public String toString() {
        return courseCode + ": " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode);
    }
}
