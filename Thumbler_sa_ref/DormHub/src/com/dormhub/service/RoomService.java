package com.dormhub.service;

import java.util.List;

import com.dormhub.model.Room;

public interface RoomService {
    void addRoom(int roomNo, String roomType, int capacity, int currentOccupancy);

    void updateRoom(int roomNo, String roomType, int capacity, int currentOccupancy);

    void deleteRoom(int roomNo);

    Room findByRoomNo(int roomNo);

    List<Room> findByRoomType(String roomType);

    List<Room> findByCapacity(int capacity);

    List<Room> findAllRooms();
}
