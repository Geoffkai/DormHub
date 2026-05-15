package com.dormhub.model;

import java.sql.Date;

public class RoomAssignment {
    private int assignmentId;
    private int residentId;
    private int roomId;
    private Date dateAssigned;
    private Date dateVacated;

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public Date getDateVacated() {
        return dateVacated;
    }

    public void setDateVacated(Date dateVacated) {
        this.dateVacated = dateVacated;
    }
}
