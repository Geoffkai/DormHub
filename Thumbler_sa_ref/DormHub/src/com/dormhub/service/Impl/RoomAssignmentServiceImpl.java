package com.dormhub.service.Impl;

import java.sql.Date;
import java.util.List;

import com.dormhub.dao.ResidentDAO;
import com.dormhub.dao.RoomAssignmentDAO;
import com.dormhub.dao.RoomDAO;
import com.dormhub.dao.impl.ResidentDAOImpl;
import com.dormhub.dao.impl.RoomAssignmentDAOImpl;
import com.dormhub.dao.impl.RoomDAOImpl;
import com.dormhub.model.RoomAssignment;
import com.dormhub.service.RoomAssignmentService;

public class RoomAssignmentServiceImpl implements RoomAssignmentService {
    private final RoomAssignmentDAO roomAssignmentDAO;
    private final ResidentDAO residentDAO;
    private final RoomDAO roomDAO;

    public RoomAssignmentServiceImpl() {
        this(new RoomAssignmentDAOImpl(), new ResidentDAOImpl(), new RoomDAOImpl());
    }

    public RoomAssignmentServiceImpl(RoomAssignmentDAO roomAssignmentDAO) {
        this(roomAssignmentDAO, new ResidentDAOImpl(), new RoomDAOImpl());
    }

    public RoomAssignmentServiceImpl(RoomAssignmentDAO roomAssignmentDAO, ResidentDAO residentDAO, RoomDAO roomDAO) {
        this.roomAssignmentDAO = roomAssignmentDAO;
        this.residentDAO = residentDAO;
        this.roomDAO = roomDAO;
    }

    @Override
    public void addRoomAssignment(int residentId, int roomId, Date dateAssigned, Date dateVacated) {
        if (residentId <= 0) throw new IllegalArgumentException("Resident ID must be positive");
        if (roomId <= 0)     throw new IllegalArgumentException("Room ID must be positive");
        if (dateAssigned == null) throw new IllegalArgumentException("Date assigned cannot be null");
        if (dateVacated != null && !dateVacated.after(dateAssigned)) {
            throw new IllegalArgumentException("Date vacated must be after date assigned.");
        }

        if (residentDAO.findById(residentId) == null) {
            throw new IllegalArgumentException("Resident not found: " + residentId);
        }

        com.dormhub.model.Room room = roomDAO.findByRoomNo(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }

        if (dateVacated == null) {
            RoomAssignment existing = roomAssignmentDAO.findActiveByResidentId(residentId);
            if (existing != null) {
                throw new IllegalArgumentException(
                        "Resident " + residentId + " is already assigned to room " + existing.getRoomId()
                                + ". A student can only be in one room at a time.");
            }
        }

        if (dateVacated == null && room.getCurrentOccupancy() >= room.getCapacity()) {
            throw new IllegalArgumentException(
                    "Room " + roomId + " is already full (" + room.getCapacity() + "/" + room.getCapacity() + ").");
        }

        RoomAssignment assignment = buildRoomAssignment(0, residentId, roomId, dateAssigned, dateVacated);
        roomAssignmentDAO.insert(assignment);

        if (dateVacated == null) {
            room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
            roomDAO.update(room);
        }
    }

    @Override
    public void updateRoomAssignment(int assignmentId, int residentId, int roomId, Date dateAssigned,
            Date dateVacated) {
        validateRoomAssignmentFields(assignmentId, residentId, roomId, dateAssigned);
        if (dateVacated != null && !dateVacated.after(dateAssigned)) {
            throw new IllegalArgumentException("Date vacated must be after date assigned.");
        }

        RoomAssignment oldAssignment = roomAssignmentDAO.findById(assignmentId);
        if (oldAssignment == null) {
            throw new IllegalArgumentException("Assignment not found: " + assignmentId);
        }

        if (residentDAO.findById(residentId) == null) {
            throw new IllegalArgumentException("Resident not found: " + residentId);
        }

        com.dormhub.model.Room newRoom = roomDAO.findByRoomNo(roomId);
        if (newRoom == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }

        boolean wasActive = oldAssignment.getDateVacated() == null;
        boolean willBeActive = dateVacated == null;
        boolean roomChanged = oldAssignment.getRoomId() != roomId;

        if (willBeActive && (!wasActive || roomChanged)) {
            RoomAssignment existingActive = roomAssignmentDAO.findActiveByResidentId(residentId);
            if (existingActive != null && existingActive.getAssignmentId() != assignmentId) {
                throw new IllegalArgumentException(
                        "Resident " + residentId + " is already assigned to room " + existingActive.getRoomId()
                                + ". A student can only be in one room at a time.");
            }
        }

        if (willBeActive && (!wasActive || roomChanged) && newRoom.getCurrentOccupancy() >= newRoom.getCapacity()) {
            throw new IllegalArgumentException(
                    "Room " + roomId + " is already full (" + newRoom.getCapacity() + "/" + newRoom.getCapacity() + ").");
        }

        roomAssignmentDAO.update(buildRoomAssignment(assignmentId, residentId, roomId, dateAssigned, dateVacated));

        if (roomChanged) {
            if (wasActive) {
                com.dormhub.model.Room oldRoom = roomDAO.findByRoomNo(oldAssignment.getRoomId());
                if (oldRoom != null && oldRoom.getCurrentOccupancy() > 0) {
                    oldRoom.setCurrentOccupancy(oldRoom.getCurrentOccupancy() - 1);
                    roomDAO.update(oldRoom);
                }
            }
            if (willBeActive) {
                newRoom.setCurrentOccupancy(newRoom.getCurrentOccupancy() + 1);
                roomDAO.update(newRoom);
            }
        } else {
            if (wasActive && !willBeActive && newRoom.getCurrentOccupancy() > 0) {
                newRoom.setCurrentOccupancy(newRoom.getCurrentOccupancy() - 1);
                roomDAO.update(newRoom);
            } else if (!wasActive && willBeActive) {
                newRoom.setCurrentOccupancy(newRoom.getCurrentOccupancy() + 1);
                roomDAO.update(newRoom);
            }
        }
    }

    @Override
    public void deleteRoomAssignment(int assignmentId) {
        if (assignmentId <= 0) {
            throw new IllegalArgumentException("Assignment ID must be positive");
        }

        RoomAssignment assignment = roomAssignmentDAO.findById(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found: " + assignmentId);
        }

        roomAssignmentDAO.delete(assignmentId);

        if (assignment.getDateVacated() == null) {
            com.dormhub.model.Room room = roomDAO.findByRoomNo(assignment.getRoomId());
            if (room != null && room.getCurrentOccupancy() > 0) {
                room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
                roomDAO.update(room);
            }
        }
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
