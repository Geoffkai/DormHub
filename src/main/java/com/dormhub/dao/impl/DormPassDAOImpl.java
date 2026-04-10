package com.dormhub.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dormhub.dao.DormPassDAO;

public class DormPassDAOImpl implements DormPassDAO {
    @Override
    public void insert(DormPass dormPass) {
        String sql = "INSERT INTO dorm_pass (pass_id, resident_id, type, reason, destination, date_applied, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dormPass.getPassId());
            ps.setInt(2, dormPass.getResidentId());
            ps.setString(3, dormPass.getType());
            ps.setString(4, dormPass.getReason());
            ps.setString(5, dormPass.getDestination());
            ps.setDate(6, dormPass.getDateApplied());
            ps.setString(7, dormPass.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Dorm pass added successfully.");
            } else {
                System.out.println("Failed to add dorm pass.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding dorm pass: " + e.getMessage());
        }
    }

    @Override
    public void update(DormPass dormPass) {
        String sql = "UPDATE dorm_pass SET resident_id = ?, type = ?, reason = ?, destination = ?, date_applied = ?, status = ? WHERE pass_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dormPass.getResidentId());
            ps.setString(2, dormPass.getType());
            ps.setString(3, dormPass.getReason());
            ps.setString(4, dormPass.getDestination());
            ps.setDate(5, dormPass.getDateApplied());
            ps.setString(6, dormPass.getStatus());
            ps.setInt(7, dormPass.getPassId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Dorm pass updated successfully.");
            } else {
                System.out.println("No dorm pass found to update.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating dorm pass: " + e.getMessage());
        }
    }

    @Override
    public void delete(int passId) {
        String sql = "DELETE FROM dorm_pass WHERE pass_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, passId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Dorm pass deleted successfully.");
            } else {
                System.out.println("No dorm pass found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting dorm pass: " + e.getMessage());
        }
    }

    @Override
    public DormPass findById(int passId) {
        String sql = "SELECT * FROM dorm_pass WHERE pass_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, passId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DormPass dormPass = new DormPass();
                    dormPass.setPassId(rs.getInt("pass_id"));
                    dormPass.setResidentId(rs.getInt("resident_id"));
                    dormPass.setType(rs.getString("type"));
                    dormPass.setReason(rs.getString("reason"));
                    dormPass.setDestination(rs.getString("destination"));
                    dormPass.setDateApplied(rs.getDate("date_applied"));
                    dormPass.setStatus(rs.getString("status"));
                    return dormPass;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding dorm pass by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<DormPass> findByResidentId(int residentId) {
        List<DormPass> dormPasses = new ArrayList<>();
        String sql = "SELECT * FROM dorm_pass WHERE resident_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, residentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DormPass dormPass = new DormPass();
                    dormPass.setPassId(rs.getInt("pass_id"));
                    dormPass.setResidentId(rs.getInt("resident_id"));
                    dormPass.setType(rs.getString("type"));
                    dormPass.setReason(rs.getString("reason"));
                    dormPass.setDestination(rs.getString("destination"));
                    dormPass.setDateApplied(rs.getDate("date_applied"));
                    dormPass.setStatus(rs.getString("status"));
                    dormPasses.add(dormPass);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding dorm passes by resident ID: " + e.getMessage());
        }

        return dormPasses;
    }

    @Override
    public List<DormPass> findByStatus(String status) {
        List<DormPass> dormPasses = new ArrayList<>();
        String sql = "SELECT * FROM dorm_pass WHERE status = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DormPass dormPass = new DormPass();
                    dormPass.setPassId(rs.getInt("pass_id"));
                    dormPass.setResidentId(rs.getInt("resident_id"));
                    dormPass.setType(rs.getString("type"));
                    dormPass.setReason(rs.getString("reason"));
                    dormPass.setDestination(rs.getString("destination"));
                    dormPass.setDateApplied(rs.getDate("date_applied"));
                    dormPass.setStatus(rs.getString("status"));
                    dormPasses.add(dormPass);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding dorm passes by status: " + e.getMessage());
        }

        return dormPasses;
    }

    @Override
    public List<DormPass> findByType(String type) {
        List<DormPass> dormPasses = new ArrayList<>();
        String sql = "SELECT * FROM dorm_pass WHERE type = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DormPass dormPass = new DormPass();
                    dormPass.setPassId(rs.getInt("pass_id"));
                    dormPass.setResidentId(rs.getInt("resident_id"));
                    dormPass.setType(rs.getString("type"));
                    dormPass.setReason(rs.getString("reason"));
                    dormPass.setDestination(rs.getString("destination"));
                    dormPass.setDateApplied(rs.getDate("date_applied"));
                    dormPass.setStatus(rs.getString("status"));
                    dormPasses.add(dormPass);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding dorm passes by type: " + e.getMessage());
        }

        return dormPasses;
    }

    @Override
    public List<DormPass> findAllDormPasses() {
        List<DormPass> dormPasses = new ArrayList<>();
        String sql = "SELECT * FROM dorm_pass";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DormPass dormPass = new DormPass();
                dormPass.setPassId(rs.getInt("pass_id"));
                dormPass.setResidentId(rs.getInt("resident_id"));
                dormPass.setType(rs.getString("type"));
                dormPass.setReason(rs.getString("reason"));
                dormPass.setDestination(rs.getString("destination"));
                dormPass.setDateApplied(rs.getDate("date_applied"));
                dormPass.setStatus(rs.getString("status"));
                dormPasses.add(dormPass);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving dorm passes: " + e.getMessage());
        }

        return dormPasses;
    }
}
