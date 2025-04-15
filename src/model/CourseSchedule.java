package model;

import java.util.Objects;
import java.io.Serializable;

public class CourseSchedule implements Serializable {
    private Course course;
    private Instructor instructor;
    private Classroom classroom;
    private TimeSlot timeSlot;
    private boolean isLab; // To distinguish between lecture and lab sessions

    // Constructors
    public CourseSchedule() {
    }

    public CourseSchedule(Course course, Instructor instructor, Classroom classroom, TimeSlot timeSlot) {
        this.course = course;
        this.instructor = instructor;
        this.classroom = classroom;
        this.timeSlot = timeSlot;
        this.isLab = timeSlot.isLabSlot();
    }

    public CourseSchedule(Course course, Instructor instructor, Classroom classroom, TimeSlot timeSlot, boolean isLab) {
        this.course = course;
        this.instructor = instructor;
        this.classroom = classroom;
        this.timeSlot = timeSlot;
        this.isLab = isLab;
    }

    // Getters and Setters
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isLab() {
        return isLab;
    }

    public void setLab(boolean lab) {
        isLab = lab;
    }

    // Check if this schedule conflicts with another
    public boolean conflictsWith(CourseSchedule other) {
        // Check if timeslots overlap
        if (!this.timeSlot.overlaps(other.timeSlot)) {
            return false; // No time conflict
        }

        // Check instructor conflict
        if (this.instructor.equals(other.instructor)) {
            return true; // Same instructor can't teach two classes at the same time
        }

        // Check classroom conflict
        if (this.classroom.equals(other.classroom)) {
            return true; // Same classroom can't be used for two classes at the same time
        }

        // Check course conflict (same course can't have multiple sessions at the same time)
        return this.course.equals(other.course);
    }

    @Override
    public String toString() {
        return course.getCourseCode() + " - " + instructor.getName() + " - " +
                classroom.getRoomNumber() + " - " + timeSlot.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseSchedule that = (CourseSchedule) o;
        return isLab == that.isLab &&
                Objects.equals(course, that.course) &&
                Objects.equals(instructor, that.instructor) &&
                Objects.equals(classroom, that.classroom) &&
                Objects.equals(timeSlot, that.timeSlot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, instructor, classroom, timeSlot, isLab);
    }
}
