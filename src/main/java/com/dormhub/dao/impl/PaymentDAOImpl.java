package com.dormhub.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dormhub.dao.PaymentDAO;
import com.dormhub.model.Payment;
import com.dormhub.util.DBUtil;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void insert(Payment payment) {
        String sql = "INSERT INTO payment (payment_id, resident_id, amount, payment_date, method, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payment.getPaymentId());
            ps.setInt(2, payment.getResidentId());
            ps.setDouble(3, payment.getAmount());
            ps.setDate(4, payment.getPaymentDate());
            ps.setString(5, payment.getMethod());
            ps.setString(6, payment.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Payment added successfully.");
            } else {
                System.out.println("Failed to add payment.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding payment: " + e.getMessage());
        }
    }

    @Override
    public void update(Payment payment) {
        String sql = "UPDATE payment SET resident_id = ?, amount = ?, payment_date = ?, method = ?, status = ? WHERE payment_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payment.getResidentId());
            ps.setDouble(2, payment.getAmount());
            ps.setDate(3, payment.getPaymentDate());
            ps.setString(4, payment.getMethod());
            ps.setString(5, payment.getStatus());
            ps.setInt(6, payment.getPaymentId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Payment updated successfully.");
            } else {
                System.out.println("No payment found to update.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating payment: " + e.getMessage());
        }
    }

    @Override
    public void delete(int paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, paymentId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Payment deleted successfully.");
            } else {
                System.out.println("No payment found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting payment: " + e.getMessage());
        }
    }

    @Override
    public Payment findById(int paymentId) {
        String sql = "SELECT * FROM payment WHERE payment_id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, paymentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setResidentId(rs.getInt("resident_id"));
                    payment.setAmount(rs.getInt("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    return payment;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding payment by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Payment> findByResidentId(int residentId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE resident_id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, residentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setResidentId(rs.getInt("resident_id"));
                    payment.setAmount(rs.getInt("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payments.add(payment);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding payments by resident ID: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public List<Payment> findByMethod(String method) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE method = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, method);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setResidentId(rs.getInt("resident_id"));
                    payment.setAmount(rs.getInt("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payments.add(payment);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding payments by method: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public List<Payment> findByStatus(String status) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE status = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setResidentId(rs.getInt("resident_id"));
                    payment.setAmount(rs.getInt("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setMethod(rs.getString("method"));
                    payment.setStatus(rs.getString("status"));
                    payments.add(payment);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding payments by status: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public List<Payment> findAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setResidentId(rs.getInt("resident_id"));
                payment.setAmount(rs.getInt("amount"));
                payment.setPaymentDate(rs.getDate("payment_date"));
                payment.setMethod(rs.getString("method"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving payments: " + e.getMessage());
        }

        return payments;
    }
}
