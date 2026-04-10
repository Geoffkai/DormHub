package com.dormhub.dao;

public interface RoomAssignmentDAO {
    void insert(RoomAssignment roomAssignment);

    void update(RoomAssignment roomAssignment);

    void delete(int assignmentId);

    RoomAssignment findById(int assignmentId);

    List<RoomAssignment> findByResidentId(int residentId);

    List<RoomAssignment> findByRoomId(int roomId);

    List<RoomAssignment> findAllAssignment();
}
