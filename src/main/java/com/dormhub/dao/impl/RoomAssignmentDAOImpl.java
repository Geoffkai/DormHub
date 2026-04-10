package com.dormhub.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomAssignmentDAOImpl {
    @Override
    public void insert(RoomAssignment roomAssignment) {
        String sql = "INSERT INTO room_assignment (assignment_id, resident_id, room_id, date_assigned, date_vacated) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomAssignment.getAssignmentId());
            ps.setInt(2, roomAssignment.getResidentId());
            ps.setInt(3, roomAssignment.getRoomId());
            ps.setDate(4, roomAssignment.getDateAssigned());
            ps.setDate(5, roomAssignment.getDateVacated());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Room assignment added successfully.");
            } else {
                System.out.println("Failed to add room assignment.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding room assignment: " + e.getMessage());
        }
    }

    @Override
    public void update(RoomAssignment roomAssignment) {
        String sql = "UPDATE room_assignment SET resident_id = ?, room_id = ?, date_assigned = ?, date_vacated = ? WHERE assignment_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomAssignment.getResidentId());
            ps.setInt(2, roomAssignment.getRoomId());
            ps.setDate(3, roomAssignment.getDateAssigned());
            ps.setDate(4, roomAssignment.getDateVacated());
            ps.setInt(5, roomAssignment.getAssignmentId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Room assignment updated successfully.");
            } else {
                System.out.println("No room assignment found to update.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating room assignment: " + e.getMessage());
        }
    }

    @Override
    public void delete(int assignmentId) {
        String sql = "DELETE FROM room_assignment WHERE assignment_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Room assignment deleted successfully.");
            } else {
                System.out.println("No room assignment found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting room assignment: " + e.getMessage());
        }
    }

    @Override
    public RoomAssignment findById(int assignmentId) {
        String sql = "SELECT * FROM room_assignment WHERE assignment_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RoomAssignment ra = new RoomAssignment();
                    ra.setAssignmentId(rs.getInt("assignment_id"));
                    ra.setResidentId(rs.getInt("resident_id"));
                    ra.setRoomId(rs.getInt("room_id"));
                    ra.setDateAssigned(rs.getDate("date_assigned"));
                    ra.setDateVacated(rs.getDate("date_vacated"));
                    return ra;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding room assignment by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<RoomAssignment> findByResidentId(int residentId) {
        List<RoomAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM room_assignment WHERE resident_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, residentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomAssignment ra = new RoomAssignment();
                    ra.setAssignmentId(rs.getInt("assignment_id"));
                    ra.setResidentId(rs.getInt("resident_id"));
                    ra.setRoomId(rs.getInt("room_id"));
                    ra.setDateAssigned(rs.getDate("date_assigned"));
                    ra.setDateVacated(rs.getDate("date_vacated"));
                    assignments.add(ra);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding room assignments by resident ID: " + e.getMessage());
        }

        return assignments;
    }

    @Override
    public List<RoomAssignment> findByRoomId(int roomId) {
        List<RoomAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM room_assignment WHERE room_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomAssignment ra = new RoomAssignment();
                    ra.setAssignmentId(rs.getInt("assignment_id"));
                    ra.setResidentId(rs.getInt("resident_id"));
                    ra.setRoomId(rs.getInt("room_id"));
                    ra.setDateAssigned(rs.getDate("date_assigned"));
                    ra.setDateVacated(rs.getDate("date_vacated"));
                    assignments.add(ra);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding room assignments by room ID: " + e.getMessage());
        }

        return assignments;
    }

    @Override
    public List<RoomAssignment> findAllAssignments() {
        List<RoomAssignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM room_assignment";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RoomAssignment ra = new RoomAssignment();
                ra.setAssignmentId(rs.getInt("assignment_id"));
                ra.setResidentId(rs.getInt("resident_id"));
                ra.setRoomId(rs.getInt("room_id"));
                ra.setDateAssigned(rs.getDate("date_assigned"));
                ra.setDateVacated(rs.getDate("date_vacated"));
                assignments.add(ra);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving room assignments: " + e.getMessage());
        }

        return assignments;
    }
}
