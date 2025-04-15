package model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import java.io.Serializable;

public class TimeSlot implements Serializable {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isLabSlot; // To differentiate between lecture and lab slots

    // Constructors
    public TimeSlot() {
    }

    public TimeSlot(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isLabSlot = endTime.minusHours(startTime.getHour()).getHour() >= 2; // Labs are typically 2+ hours
    }

    public TimeSlot(DayOfWeek day, LocalTime startTime, LocalTime endTime, boolean isLabSlot) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isLabSlot = isLabSlot;
    }

    // Getters and Setters
    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isLabSlot() {
        return isLabSlot;
    }

    public void setLabSlot(boolean labSlot) {
        isLabSlot = labSlot;
    }

    // Method to check if this timeslot overlaps with another
    public boolean overlaps(TimeSlot other) {
        if (this.day != other.day) {
            return false;
        }

        return (this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime)) ||
                (other.startTime.isBefore(this.endTime) && other.endTime.isAfter(this.startTime)) ||
                this.startTime.equals(other.startTime) || this.endTime.equals(other.endTime);
    }

    @Override
    public String toString() {
        return day + " " + startTime + "-" + endTime + (isLabSlot ? " (Lab)" : " (Lecture)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return day == timeSlot.day &&
                Objects.equals(startTime, timeSlot.startTime) &&
                Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startTime, endTime);
    }
}
