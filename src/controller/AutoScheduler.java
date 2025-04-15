package controller;

import model.*;
import java.util.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class AutoScheduler {
    private ConflictChecker conflictChecker;
    private Random random;

    public AutoScheduler() {
        conflictChecker = new ConflictChecker();
        random = new Random();
    }

    // Generate multiple timetable suggestions
    public List<Timetable> generateSuggestions(List<Course> courses, List<Instructor> instructors,
                                               List<Classroom> classrooms, List<TimeSlot> availableTimeSlots,
                                               int numberOfSuggestions) {
        List<Timetable> suggestions = new ArrayList<>();

        for (int i = 0; i < numberOfSuggestions; i++) {
            Timetable suggestion = generateTimetable(
                    "Suggestion " + (i + 1),
                    courses,
                    instructors,
                    classrooms,
                    availableTimeSlots
            );

            if (suggestion != null && !containsSimilarTimetable(suggestions, suggestion)) {
                suggestions.add(suggestion);
            } else {
                // Try again if we couldn't generate a valid suggestion or it's too similar
                i--;
            }

            // Prevent infinite loop if we can't generate enough unique suggestions
            if (i < 0 && i < -10) {
                break;
            }
        }

        return suggestions;
    }

    // Generate a single timetable
    private Timetable generateTimetable(String name, List<Course> courses, List<Instructor> instructors,
                                        List<Classroom> classrooms, List<TimeSlot> availableTimeSlots) {
        Timetable timetable = new Timetable(name);
        int maxAttempts = 1000; // Prevent infinite loops

        // Process courses in order of complexity (most lectures + labs first)
        List<Course> sortedCourses = new ArrayList<>(courses);
        sortedCourses.sort((c1, c2) -> (c2.getLectureHours() + c2.getLabHours()) -
                (c1.getLectureHours() + c1.getLabHours()));

        for (Course course : sortedCourses) {
            // Calculate how many lecture slots we need (typically 1 hour per slot)
            int lectureSlots = course.getLectureHours();
            int labSlots = course.getLabHours() > 0 ? 1 : 0; // Usually labs are combined into one longer session

            // Find suitable instructor
            List<Instructor> suitableInstructors = new ArrayList<>();
            for (Instructor instructor : instructors) {
                if (instructor.canTeach(course.getCourseCode())) {
                    suitableInstructors.add(instructor);
                }
            }

            if (suitableInstructors.isEmpty()) {
                continue; // Skip course if no suitable instructor
            }

            // Schedule lectures
            boolean lecturesScheduled = scheduleSessionsForCourse(timetable, course, suitableInstructors,
                    classrooms, availableTimeSlots,
                    lectureSlots, false, maxAttempts);

            // Schedule lab if needed
            boolean labScheduled = true;
            if (labSlots > 0) {
                labScheduled = scheduleSessionsForCourse(timetable, course, suitableInstructors,
                        classrooms, availableTimeSlots,
                        labSlots, true, maxAttempts);
            }

            // If we couldn't schedule all sessions, the timetable is invalid
            if (!lecturesScheduled || !labScheduled) {
                return null;
            }
        }

        return timetable;
    }

    // Schedule multiple sessions for a course
    private boolean scheduleSessionsForCourse(Timetable timetable, Course course, List<Instructor> instructors,
                                              List<Classroom> classrooms, List<TimeSlot> availableTimeSlots,
                                              int sessionsNeeded, boolean isLab, int maxAttempts) {
        // Track the days we've already scheduled for this course to ensure day gaps
        Set<DayOfWeek> scheduledDays = new HashSet<>();

        for (int i = 0; i < sessionsNeeded; i++) {
            boolean slotFound = false;
            int attempts = 0;

            while (!slotFound && attempts < maxAttempts) {
                attempts++;

                // Select random instructor, classroom, and timeslot
                Instructor instructor = instructors.get(random.nextInt(instructors.size()));

                // For labs, we need larger classrooms
                List<Classroom> suitableClassrooms = isLab ?
                        filterClassroomsByCapacity(classrooms, course.getCredits() * 5) : // Rough estimate
                        classrooms;

                if (suitableClassrooms.isEmpty()) {
                    suitableClassrooms = classrooms; // Fallback to all classrooms
                }

                Classroom classroom = suitableClassrooms.get(random.nextInt(suitableClassrooms.size()));

                // Filter time slots by type (lab or lecture) and avoid days already scheduled
                List<TimeSlot> suitableTimeSlots = filterTimeSlots(availableTimeSlots, isLab, scheduledDays);

                if (suitableTimeSlots.isEmpty()) {
                    continue; // No suitable time slots, try again
                }

                TimeSlot timeSlot = suitableTimeSlots.get(random.nextInt(suitableTimeSlots.size()));

                // Check if this slot works
                if (conflictChecker.isTimeSlotAvailable(course, instructor, classroom, timeSlot, timetable.getSchedules())) {
                    CourseSchedule schedule = new CourseSchedule(course, instructor, classroom, timeSlot, isLab);

                    if (timetable.addSchedule(schedule)) {
                        slotFound = true;
                        scheduledDays.add(timeSlot.getDay());
                    }
                }
            }

            if (!slotFound) {
                return false; // Could not schedule all needed sessions
            }
        }

        return true;
    }

    // Filter time slots based on type and already scheduled days
    private List<TimeSlot> filterTimeSlots(List<TimeSlot> timeSlots, boolean isLab, Set<DayOfWeek> scheduledDays) {
        List<TimeSlot> filtered = new ArrayList<>();

        for (TimeSlot slot : timeSlots) {
            // Check if slot matches the type we need
            if (slot.isLabSlot() == isLab) {
                // Check day gap policy
                boolean adjacentDayFound = false;

                for (DayOfWeek scheduledDay : scheduledDays) {
                    if (isAdjacentDay(scheduledDay, slot.getDay())) {
                        adjacentDayFound = true;
                        break;
                    }
                }

                if (!adjacentDayFound) {
                    filtered.add(slot);
                }
            }
        }

        return filtered;
    }

    // Check if two days are adjacent (including Monday-Friday wrap-around)
    private boolean isAdjacentDay(DayOfWeek day1, DayOfWeek day2) {
        int value1 = day1.getValue();
        int value2 = day2.getValue();

        return Math.abs(value1 - value2) == 1 ||
                (value1 == 1 && value2 == 5) ||
                (value1 == 5 && value2 == 1);
    }

    // Filter classrooms by minimum capacity
    private List<Classroom> filterClassroomsByCapacity(List<Classroom> classrooms, int minCapacity) {
        List<Classroom> filtered = new ArrayList<>();

        for (Classroom classroom : classrooms) {
            if (classroom.getCapacity() >= minCapacity) {
                filtered.add(classroom);
            }
        }

        return filtered;
    }

    // Check if a similar timetable already exists in our suggestions
    private boolean containsSimilarTimetable(List<Timetable> timetables, Timetable newTimetable) {
        int similarityThreshold = 80; // Percentage of similar schedules to consider timetables similar

        for (Timetable existing : timetables) {
            if (calculateSimilarity(existing, newTimetable) > similarityThreshold) {
                return true;
            }
        }

        return false;
    }

    // Calculate similarity between two timetables (percentage of identical schedules)
    private double calculateSimilarity(Timetable timetable1, Timetable timetable2) {
        List<CourseSchedule> schedules1 = timetable1.getSchedules();
        List<CourseSchedule> schedules2 = timetable2.getSchedules();

        if (schedules1.isEmpty() || schedules2.isEmpty()) {
            return 0;
        }

        int matches = 0;

        for (CourseSchedule s1 : schedules1) {
            for (CourseSchedule s2 : schedules2) {
                if (s1.equals(s2)) {
                    matches++;
                    break;
                }
            }
        }

        return (double) matches * 100 / Math.min(schedules1.size(), schedules2.size());
    }
}
