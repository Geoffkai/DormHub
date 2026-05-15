package com.dormhub.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dormhub.dao.RoomDAO;
import com.dormhub.model.Room;
import com.dormhub.util.DBUtil;

public class RoomDAOImpl implements RoomDAO {
    @Override
    public void insert(Room room) {
        String sql = "INSERT INTO room (room_number, room_type, capacity, current_occupancy) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, room.getRoomNo());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getCapacity());
            ps.setInt(4, room.getCurrentOccupancy());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Room added successfully.");
            } else {
                System.out.println("Failed to add room.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
        }
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE room SET room_type = ?, capacity = ?, current_occupancy = ? WHERE room_number = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomType());
            ps.setInt(2, room.getCapacity());
            ps.setInt(3, room.getCurrentOccupancy());
            ps.setInt(4, room.getRoomNo());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Room updated successfully.");
            } else {
                System.out.println("No room found to update.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating room: " + e.getMessage());
        }
    }

    @Override
    public void delete(int roomNo) {
        String sql = "DELETE FROM room WHERE room_number = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomNo);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Room deleted successfully.");
            } else {
                System.out.println("No room found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting room: " + e.getMessage());
        }
    }

    @Override
    public Room findByRoomNo(int roomNo) {
        String sql = "SELECT * FROM room WHERE room_number = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomNo(rs.getInt("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setCurrentOccupancy(rs.getInt("current_occupancy"));
                    return room;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding room by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Room> findByRoomType(String roomType) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE room_type = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomNo(rs.getInt("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setCurrentOccupancy(rs.getInt("current_occupancy"));
                    rooms.add(room);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding rooms by type: " + e.getMessage());
        }

        return rooms;
    }

    @Override
    public List<Room> findByCapacity(int capacity) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE capacity = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, capacity);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomNo(rs.getInt("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setCapacity(rs.getInt("capacity"));
                    room.setCurrentOccupancy(rs.getInt("current_occupancy"));
                    rooms.add(room);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding rooms by capacity: " + e.getMessage());
        }

        return rooms;
    }

    @Override
    public List<Room> findAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomNo(rs.getInt("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setCapacity(rs.getInt("capacity"));
                room.setCurrentOccupancy(rs.getInt("current_occupancy"));
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving rooms: " + e.getMessage());
        }

        return rooms;
    }
}
