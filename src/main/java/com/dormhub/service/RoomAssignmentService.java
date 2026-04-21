package com.dormhub.service;

import java.sql.Date;
import java.util.List;

import com.dormhub.model.RoomAssignment;

public interface RoomAssignmentService {
    void addRoomAssignment(int assignmentId, int residentId, int roomId, Date dateAssigned, Date dateVacated);

    void updateRoomAssignment(int assignmentId, int residentId, int roomId, Date dateAssigned, Date dateVacated);

    void deleteRoomAssignment(int assignmentId);

    RoomAssignment findById(int assignmentId);

    List<RoomAssignment> findByResidentId(int residentId);

    List<RoomAssignment> findByRoomId(int roomId);

    List<RoomAssignment> findAllAssignments();
}
