package com.dormhub.service.Impl;

import java.sql.Date;
import java.util.List;

import com.dormhub.dao.RoomAssignmentDAO;
import com.dormhub.dao.impl.RoomAssignmentDAOImpl;
import com.dormhub.model.RoomAssignment;
import com.dormhub.service.RoomAssignmentService;

public class RoomAssignmentServiceImpl implements RoomAssignmentService {
    private final RoomAssignmentDAO roomAssignmentDAO;

    public RoomAssignmentServiceImpl() {
        this(new RoomAssignmentDAOImpl());
    }

    public RoomAssignmentServiceImpl(RoomAssignmentDAO roomAssignmentDAO) {
        this.roomAssignmentDAO = roomAssignmentDAO;
    }

    @Override
    public void addRoomAssignment(int assignmentId, int residentId, int roomId, Date dateAssigned,
            Date dateVacated) {
        validateRoomAssignmentFields(assignmentId, residentId, roomId, dateAssigned);

        if (roomAssignmentDAO.findById(assignmentId) != null) {
            throw new IllegalArgumentException("Assignment ID already exists: " + assignmentId);
        }

        RoomAssignment assignment = buildRoomAssignment(assignmentId, residentId, roomId, dateAssigned,
                dateVacated);
        roomAssignmentDAO.insert(assignment);
    }

    @Override
    public void updateRoomAssignment(int assignmentId, int residentId, int roomId, Date dateAssigned,
            Date dateVacated) {
        validateRoomAssignmentFields(assignmentId, residentId, roomId, dateAssigned);

        RoomAssignment assignment = roomAssignmentDAO.findById(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found: " + assignmentId);
        }

        assignment = buildRoomAssignment(assignmentId, residentId, roomId, dateAssigned, dateVacated);
        roomAssignmentDAO.update(assignment);
    }

    @Override
    public void deleteRoomAssignment(int assignmentId) {
        if (assignmentId <= 0) {
            throw new IllegalArgumentException("Assignment ID must be positive");
        }
        roomAssignmentDAO.delete(assignmentId);
    }

    @Override
    public RoomAssignment findById(int assignmentId) {
        if (assignmentId <= 0) {
            throw new IllegalArgumentException("Assignment ID must be positive");
        }
        return roomAssignmentDAO.findById(assignmentId);
    }

    @Override
    public List<RoomAssignment> findByResidentId(int residentId) {
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive");
        }
        return roomAssignmentDAO.findByResidentId(residentId);
    }

    @Override
    public List<RoomAssignment> findByRoomId(int roomId) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Room ID must be positive");
        }
        return roomAssignmentDAO.findByRoomId(roomId);
    }

    @Override
    public List<RoomAssignment> findAllAssignments() {
        return roomAssignmentDAO.findAllAssignments();
    }

    private RoomAssignment buildRoomAssignment(int assignmentId, int residentId, int roomId, Date dateAssigned,
            Date dateVacated) {
        RoomAssignment assignment = new RoomAssignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setResidentId(residentId);
        assignment.setRoomId(roomId);
        assignment.setDateAssigned(dateAssigned);
        assignment.setDateVacated(dateVacated);

        return assignment;
    }

    private void validateRoomAssignmentFields(int assignmentId, int residentId, int roomId, Date dateAssigned) {
        if (assignmentId <= 0) {
            throw new IllegalArgumentException("Assignment ID must be positive");
        }
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive");
        }
        if (roomId <= 0) {
            throw new IllegalArgumentException("Room ID must be positive");
        }
        if (dateAssigned == null) {
            throw new IllegalArgumentException("Date assigned cannot be null");
        }
    }
}
