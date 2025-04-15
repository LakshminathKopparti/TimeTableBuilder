package controller;

import model.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public class ConflictChecker {

    // Check for conflicts when adding a new schedule
    public boolean hasConflict(CourseSchedule newSchedule, List<CourseSchedule> existingSchedules) {
        for (CourseSchedule existingSchedule : existingSchedules) {
            if (conflictExists(newSchedule, existingSchedule)) {
                return true;
            }
        }
        return false;
    }

    // Check if two schedules conflict with each other
    public boolean conflictExists(CourseSchedule schedule1, CourseSchedule schedule2) {
        // Same time slot check
        if (!schedule1.getTimeSlot().overlaps(schedule2.getTimeSlot())) {
            return false; // Different time slots, no conflict
        }

        // Same classroom check
        if (schedule1.getClassroom().equals(schedule2.getClassroom())) {
            return true; // Same classroom at the same time
        }

        // Same instructor check
        if (schedule1.getInstructor().equals(schedule2.getInstructor())) {
            return true; // Same instructor at the same time
        }

        // Same course check (a course shouldn't have multiple sessions at the same time)
        if (schedule1.getCourse().equals(schedule2.getCourse())) {
            return true; // Same course at the same time
        }

        return false; // No conflict detected
    }

    // Check for BITS policy compliance
    public List<String> checkBITSPolicyCompliance(Timetable timetable) {
        List<String> violations = new ArrayList<>();

        // Check lectures/labs have a day in gap
        checkDayGapPolicy(timetable, violations);

        // Check instructor load balancing
        checkInstructorLoadBalancing(timetable, violations);

        // Check classroom utilization
        checkClassroomUtilization(timetable, violations);

        return violations;
    }

    // Check if lectures/labs have a day in gap
    private void checkDayGapPolicy(Timetable timetable, List<String> violations) {
        Map<String, List<CourseSchedule>> courseSchedules = new HashMap<>();

        // Group schedules by course
        for (CourseSchedule schedule : timetable.getSchedules()) {
            String courseCode = schedule.getCourse().getCourseCode();
            if (!courseSchedules.containsKey(courseCode)) {
                courseSchedules.put(courseCode, new ArrayList<>());
            }
            courseSchedules.get(courseCode).add(schedule);
        }

        // Check each course's schedule
        for (Map.Entry<String, List<CourseSchedule>> entry : courseSchedules.entrySet()) {
            String courseCode = entry.getKey();
            List<CourseSchedule> schedules = entry.getValue();

            // Sort schedules by day of week
            schedules.sort(Comparator.comparing(s -> s.getTimeSlot().getDay().getValue()));

            // Group by lecture vs lab
            List<CourseSchedule> lectures = schedules.stream()
                    .filter(s -> !s.isLab())
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            List<CourseSchedule> labs = schedules.stream()
                    .filter(CourseSchedule::isLab)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            // Check lectures have a day gap
            checkDayGapForSessionType(courseCode, lectures, "lecture", violations);

            // Check labs have a day gap
            checkDayGapForSessionType(courseCode, labs, "lab", violations);
        }
    }

    private void checkDayGapForSessionType(String courseCode, List<CourseSchedule> schedules,
                                           String sessionType, List<String> violations) {
        if (schedules.size() <= 1) {
            return; // No gap to check
        }

        for (int i = 0; i < schedules.size() - 1; i++) {
            DayOfWeek day1 = schedules.get(i).getTimeSlot().getDay();
            DayOfWeek day2 = schedules.get(i + 1).getTimeSlot().getDay();

            int dayValue1 = day1.getValue();
            int dayValue2 = day2.getValue();

            // Check if days are adjacent
            if (dayValue2 - dayValue1 == 1) {
                violations.add(courseCode + " has " + sessionType + " sessions on adjacent days ("
                        + day1 + " and " + day2 + ")");
            }

            // Special case for Monday (1) and Friday (5) which shouldn't be adjacent for BITS policy
            if (dayValue1 == 5 && dayValue2 == 1) {
                violations.add(courseCode + " has " + sessionType + " sessions on adjacent days (Friday and Monday)");
            }
        }
    }

    // Check instructor load balancing
    private void checkInstructorLoadBalancing(Timetable timetable, List<String> violations) {
        Map<Instructor, Integer> instructorHours = new HashMap<>();

        // Calculate total teaching hours per instructor
        for (CourseSchedule schedule : timetable.getSchedules()) {
            Instructor instructor = schedule.getInstructor();
            TimeSlot timeSlot = schedule.getTimeSlot();

            // Calculate duration in hours
            LocalTime start = timeSlot.getStartTime();
            LocalTime end = timeSlot.getEndTime();
            int duration = end.getHour() - start.getHour();
            if (end.getMinute() > 0) duration++;

            // Add to instructor's total
            instructorHours.put(instructor, instructorHours.getOrDefault(instructor, 0) + duration);
        }

        // Check for instructors with excessive loads (more than 20 hours per week)
        for (Map.Entry<Instructor, Integer> entry : instructorHours.entrySet()) {
            if (entry.getValue() > 20) {
                violations.add("Instructor " + entry.getKey().getName() +
                        " has an excessive teaching load of " + entry.getValue() + " hours");
            }
        }
    }

    // Check classroom utilization
    private void checkClassroomUtilization(Timetable timetable, List<String> violations) {
        Map<Classroom, Integer> classroomHours = new HashMap<>();
        Map<Classroom, List<DayOfWeek>> classroomDays = new HashMap<>();

        // Calculate usage hours per classroom
        for (CourseSchedule schedule : timetable.getSchedules()) {
            Classroom classroom = schedule.getClassroom();
            TimeSlot timeSlot = schedule.getTimeSlot();
            DayOfWeek day = timeSlot.getDay();

            // Calculate duration in hours
            LocalTime start = timeSlot.getStartTime();
            LocalTime end = timeSlot.getEndTime();
            int duration = end.getHour() - start.getHour();
            if (end.getMinute() > 0) duration++;

            // Add to classroom's total
            classroomHours.put(classroom, classroomHours.getOrDefault(classroom, 0) + duration);

            // Track days used
            if (!classroomDays.containsKey(classroom)) {
                classroomDays.put(classroom, new ArrayList<>());
            }
            if (!classroomDays.get(classroom).contains(day)) {
                classroomDays.get(classroom).add(day);
            }
        }

        // Check for classrooms with low utilization (less than 20 hours per week or used less than 4 days)
        for (Map.Entry<Classroom, Integer> entry : classroomHours.entrySet()) {
            Classroom classroom = entry.getKey();
            int hours = entry.getValue();
            int daysUsed = classroomDays.getOrDefault(classroom, Collections.emptyList()).size();

            if (hours < 20) {
                violations.add("Classroom " + classroom.getRoomNumber() +
                        " has low utilization of only " + hours + " hours per week");
            }

            if (daysUsed < 4) {
                violations.add("Classroom " + classroom.getRoomNumber() +
                        " is only used on " + daysUsed + " days of the week");
            }
        }
    }

    // Special method to check if a specific time slot can be used for a course
    public boolean isTimeSlotAvailable(Course course, Instructor instructor, Classroom classroom,
                                       TimeSlot timeSlot, List<CourseSchedule> existingSchedules) {
        CourseSchedule potentialSchedule = new CourseSchedule(course, instructor, classroom, timeSlot);

        // Check for conflicts with existing schedules
        for (CourseSchedule existingSchedule : existingSchedules) {
            if (conflictExists(potentialSchedule, existingSchedule)) {
                return false;
            }
        }

        return true;
    }
}
