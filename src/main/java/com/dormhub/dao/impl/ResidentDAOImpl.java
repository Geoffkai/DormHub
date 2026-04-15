package com.dormhub.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dormhub.dao.ResidentDAO;
import com.dormhub.model.Resident;
import com.dormhub.util.DBUtil;

public class ResidentDAOImpl implements ResidentDAO {
    @Override
    public void insert(Resident resident) {
        String sql = "INSERT INTO resident (resident_id, last_name, first_name, contact_no, year_level, program, move_in_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resident.getResidentId());
            ps.setString(2, resident.getLastName());
            ps.setString(3, resident.getFirstName());
            ps.setString(4, resident.getContactNo());
            ps.setInt(5, resident.getYearLevel());
            ps.setString(6, resident.getProgram());
            ps.setDate(7, resident.getMoveInDate());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Resident added successfully.");
            } else {
                System.out.println("Failed to add resident.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding resident: " + e.getMessage());
        }
    }

    @Override
    public void update(Resident resident) {
        String sql = "UPDATE resident SET last_name = ?, first_name = ?, contact_no = ?, year_level = ?, program = ?, move_in_date = ? WHERE resident_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resident.getLastName());
            ps.setString(2, resident.getFirstName());
            ps.setString(3, resident.getContactNo());
            ps.setInt(4, resident.getYearLevel());
            ps.setString(5, resident.getProgram());
            ps.setDate(6, resident.getMoveInDate());
            ps.setInt(7, resident.getResidentId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Resident updated successfully.");
            } else {
                System.out.println("No resident found to update.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating resident: " + e.getMessage());
        }
    }

    @Override
    public void delete(int residentId) {
        String sql = "DELETE FROM resident WHERE resident_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, residentId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Resident deleted successfully.");
            } else {
                System.out.println("No resident found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting resident: " + e.getMessage());
        }
    }

    @Override
    public Resident findById(int residentId) {
        String sql = "SELECT * FROM resident WHERE resident_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, residentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Resident resident = new Resident();
                    resident.setResidentId(rs.getInt("resident_id"));
                    resident.setLastName(rs.getString("last_name"));
                    resident.setFirstName(rs.getString("first_name"));
                    resident.setContactNo(rs.getString("contact_no"));
                    resident.setYearLevel(rs.getInt("year_level"));
                    resident.setProgram(rs.getString("program"));
                    resident.setMoveInDate(rs.getDate("move_in_date"));
                    return resident;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding resident by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Resident> findByLastName(String lastName) {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM resident WHERE last_name = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lastName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resident resident = new Resident();
                    resident.setResidentId(rs.getInt("resident_id"));
                    resident.setLastName(rs.getString("last_name"));
                    resident.setFirstName(rs.getString("first_name"));
                    resident.setContactNo(rs.getString("contact_no"));
                    resident.setYearLevel(rs.getInt("year_level"));
                    resident.setProgram(rs.getString("program"));
                    resident.setMoveInDate(rs.getDate("move_in_date"));
                    residents.add(resident);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding residents by last name: " + e.getMessage());
        }

        return residents;
    }

    @Override
    public List<Resident> findByProgram(String program) {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM resident WHERE program = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, program);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resident resident = new Resident();
                    resident.setResidentId(rs.getInt("resident_id"));
                    resident.setLastName(rs.getString("last_name"));
                    resident.setFirstName(rs.getString("first_name"));
                    resident.setContactNo(rs.getString("contact_no"));
                    resident.setYearLevel(rs.getInt("year_level"));
                    resident.setProgram(rs.getString("program"));
                    resident.setMoveInDate(rs.getDate("move_in_date"));
                    residents.add(resident);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding residents by program: " + e.getMessage());
        }

        return residents;
    }

    @Override
    public List<Resident> findByYearLevel(int yearLevel) {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM resident WHERE year_level = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, yearLevel);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resident resident = new Resident();
                    resident.setResidentId(rs.getInt("resident_id"));
                    resident.setLastName(rs.getString("last_name"));
                    resident.setFirstName(rs.getString("first_name"));
                    resident.setContactNo(rs.getString("contact_no"));
                    resident.setYearLevel(rs.getInt("year_level"));
                    resident.setProgram(rs.getString("program"));
                    residents.add(resident);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding residents by year level: " + e.getMessage());
        }

        return residents;
    }

    @Override
    public List<Resident> findAllResidents() {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM resident";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Resident resident = new Resident();
                resident.setResidentId(rs.getInt("resident_id"));
                resident.setLastName(rs.getString("last_name"));
                resident.setFirstName(rs.getString("first_name"));
                resident.setContactNo(rs.getString("contact_no"));
                resident.setYearLevel(rs.getInt("year_level"));
                resident.setProgram(rs.getString("program"));
                resident.setMoveInDate(rs.getDate("move_in_date"));
                residents.add(resident);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving residents: " + e.getMessage());
        }

        return residents;
    }
}
