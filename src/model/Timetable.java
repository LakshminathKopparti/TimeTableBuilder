package model;

import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;
import java.io.Serializable;

public class Timetable implements Serializable {
    private String name;
    private List<CourseSchedule> schedules;

    // Constructors
    public Timetable() {
        this.schedules = new ArrayList<>();
    }

    public Timetable(String name) {
        this.name = name;
        this.schedules = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CourseSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<CourseSchedule> schedules) {
        this.schedules = schedules;
    }

    // Methods to manipulate schedules
    public boolean addSchedule(CourseSchedule schedule) {
        // Check for conflicts before adding
        for (CourseSchedule existing : schedules) {
            if (existing.conflictsWith(schedule)) {
                return false; // Conflict detected, can't add
            }
        }

        // No conflicts, add the schedule
        return schedules.add(schedule);
    }

    public boolean removeSchedule(CourseSchedule schedule) {
        return schedules.remove(schedule);
    }

    public List<CourseSchedule> getSchedulesForCourse(Course course) {
        List<CourseSchedule> result = new ArrayList<>();
        for (CourseSchedule schedule : schedules) {
            if (schedule.getCourse().equals(course)) {
                result.add(schedule);
            }
        }
        return result;
    }

    public List<CourseSchedule> getSchedulesForInstructor(Instructor instructor) {
        List<CourseSchedule> result = new ArrayList<>();
        for (CourseSchedule schedule : schedules) {
            if (schedule.getInstructor().equals(instructor)) {
                result.add(schedule);
            }
        }
        return result;
    }

    public List<CourseSchedule> getSchedulesForClassroom(Classroom classroom) {
        List<CourseSchedule> result = new ArrayList<>();
        for (CourseSchedule schedule : schedules) {
            if (schedule.getClassroom().equals(classroom)) {
                result.add(schedule);
            }
        }
        return result;
    }

    // Validate timetable according to BITS policies
    public boolean validateBITSPolicies() {
        // Check for lectures/labs with a day in gap
        for (Course course : getAllCourses()) {
            List<CourseSchedule> courseSchedules = getSchedulesForCourse(course);

            // Check for each type (lecture or lab)
            List<CourseSchedule> lectures = new ArrayList<>();
            List<CourseSchedule> labs = new ArrayList<>();

            for (CourseSchedule schedule : courseSchedules) {
                if (schedule.isLab()) {
                    labs.add(schedule);
                } else {
                    lectures.add(schedule);
                }
            }

            // Check lectures have at least one day gap
            if (!checkDayGap(lectures)) {
                return false;
            }

            // Check labs have at least one day gap
            if (!checkDayGap(labs)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkDayGap(List<CourseSchedule> schedules) {
        // Check if schedules have at least one day gap between them
        if (schedules.size() <= 1) {
            return true; // No gap to check
        }

        // Check each pair of schedules
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                TimeSlot slot1 = schedules.get(i).getTimeSlot();
                TimeSlot slot2 = schedules.get(j).getTimeSlot();

                // If days are adjacent or same, policy is violated
                int dayDiff = Math.abs(slot1.getDay().getValue() - slot2.getDay().getValue());
                if (dayDiff < 2 && dayDiff != 0) {
                    return false;
                }

                // Special case for Monday and Friday (considered adjacent for BITS policy)
                if ((slot1.getDay() == DayOfWeek.MONDAY && slot2.getDay() == DayOfWeek.FRIDAY) ||
                        (slot1.getDay() == DayOfWeek.FRIDAY && slot2.getDay() == DayOfWeek.MONDAY)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Helper method to get all courses in the timetable
    private List<Course> getAllCourses() {
        List<Course> result = new ArrayList<>();
        for (CourseSchedule schedule : schedules) {
            if (!result.contains(schedule.getCourse())) {
                result.add(schedule.getCourse());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + schedules.size() + " schedules)";
    }
}
