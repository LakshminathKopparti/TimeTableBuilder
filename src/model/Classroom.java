package model;

import java.util.Objects;
import java.util.Arrays;
import java.io.Serializable;

public class Classroom implements Serializable {
    private String roomNumber;
    private int capacity;
    private boolean hasProjector;
    private boolean hasAC;
    private String[] additionalFacilities;

    // Constructors
    public Classroom() {
    }

    public Classroom(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    public Classroom(String roomNumber, int capacity, boolean hasProjector, boolean hasAC) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
        this.hasAC = hasAC;
    }

    public Classroom(String roomNumber, int capacity, boolean hasProjector, boolean hasAC, String[] additionalFacilities) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
        this.hasAC = hasAC;
        this.additionalFacilities = additionalFacilities;
    }

    // Getters and Setters
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean hasProjector() {
        return hasProjector;
    }

    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public boolean hasAC() {
        return hasAC;
    }

    public void setHasAC(boolean hasAC) {
        this.hasAC = hasAC;
    }

    public String[] getAdditionalFacilities() {
        return additionalFacilities;
    }

    public void setAdditionalFacilities(String[] additionalFacilities) {
        this.additionalFacilities = additionalFacilities;
    }

    @Override
    public String toString() {
        return roomNumber + " (Capacity: " + capacity + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equals(roomNumber, classroom.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }
}
