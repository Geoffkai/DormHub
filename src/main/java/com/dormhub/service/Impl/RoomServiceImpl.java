package com.dormhub.service.Impl;

import java.util.List;

import com.dormhub.dao.RoomAssignmentDAO;
import com.dormhub.dao.RoomDAO;
import com.dormhub.dao.impl.RoomAssignmentDAOImpl;
import com.dormhub.dao.impl.RoomDAOImpl;
import com.dormhub.model.Room;
import com.dormhub.service.RoomService;

public class RoomServiceImpl implements RoomService {
    private final RoomDAO roomDAO;
    private final RoomAssignmentDAO roomAssignmentDAO;

    public RoomServiceImpl() {
        this(new RoomDAOImpl(), new RoomAssignmentDAOImpl());
    }

    public RoomServiceImpl(RoomDAO roomDAO) {
        this(roomDAO, new RoomAssignmentDAOImpl());
    }

    public RoomServiceImpl(RoomDAO roomDAO, RoomAssignmentDAO roomAssignmentDAO) {
        this.roomDAO = roomDAO;
        this.roomAssignmentDAO = roomAssignmentDAO;
    }

    @Override
    public void addRoom(int roomNo, String roomType, int capacity, int currentOccupancy) {
        validateRoomFields(roomNo, roomType, capacity, currentOccupancy);

        if (roomDAO.findByRoomNo(roomNo) != null) {
            throw new IllegalArgumentException("Room no already exists: " + roomNo);
        }

        Room room = buildRoom(roomNo, roomType, capacity, currentOccupancy);
        roomDAO.insert(room);
    }

    public void updateRoom(int roomNo, String roomType, int capacity, int currentOccupancy) {
        validateRoomFields(roomNo, roomType, capacity, currentOccupancy);

        if (roomDAO.findByRoomNo(roomNo) == null) {
            throw new IllegalArgumentException("Room not found: " + roomNo);
        }

        Room room = buildRoom(roomNo, roomType, capacity, currentOccupancy);
        roomDAO.update(room);
    }

    public void deleteRoom(int roomNo) {
        if (roomNo < 0) {
            throw new IllegalArgumentException("Room must be valid.");
        }

        if (roomDAO.findByRoomNo(roomNo) == null) {
            throw new IllegalArgumentException("Room not found: " + roomNo);
        }

        if (!roomAssignmentDAO.findByRoomId(roomNo).isEmpty()) {
            throw new IllegalArgumentException(
                    "Room " + roomNo + " cannot be deleted: it still has room assignments. Remove all assignments for this room first.");
        }

        roomDAO.delete(roomNo);
    }

    @Override
    public Room findByRoomNo(int roomNo) {
        if (roomNo < 0) {
            throw new IllegalArgumentException();
        }

        return roomDAO.findByRoomNo(roomNo);
    }

    @Override
    public List<Room> findByRoomType(String roomType) {
        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException();
        }
        return roomDAO.findByRoomType(roomType.trim());
    }

    @Override
    public List<Room> findByCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }

        return roomDAO.findByCapacity(capacity);
    }

    @Override
    public List<Room> findAllRooms() {
        return roomDAO.findAllRooms();
    }

    private Room buildRoom(int roomNo, String roomType, int capacity, int currentOccupancy) {
        Room room = new Room();
        room.setRoomNo(roomNo);
        room.setRoomType(roomType.trim());
        room.setCapacity(capacity);
        room.setCurrentOccupancy(currentOccupancy);
        return room;
    }

    private void validateRoomFields(int roomNo, String roomType, int capacity, int currentOccupancy) {
        if (roomNo < 0) {
            throw new IllegalArgumentException("Room Number must be valid");
        }
        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException("Room Type must be valid");
        }
        // Validate room type is exactly "Male" or "Female" to prevent truncation
        String trimmedType = roomType.trim();
        if (!trimmedType.equals("Regular") && !trimmedType.equals("Transient")) {
            throw new IllegalArgumentException("Room Type must be either 'Regular' or 'Transient'");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (currentOccupancy < 0) {
            throw new IllegalArgumentException("Current occupancy cannot be negative");
        }
        if (currentOccupancy > capacity) {
            throw new IllegalArgumentException("Current occupancy cannot exceed capacity (" + capacity + ")");
        }
    }
}
