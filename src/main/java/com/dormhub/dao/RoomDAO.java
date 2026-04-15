package com.dormhub.dao;

import java.util.List;

import com.dormhub.model.Room;

public interface RoomDAO {
    void insert(Room room);

    void update(Room room);

    void delete(int roomNo);

    Room findById(int roomNo);

    List<Room> findByRoomType(String roomType);

    List<Room> findByCapacity(int capacity);

    List<Room> findAllRooms();
}
