package com.dormhub.service;

import java.sql.Date;
import java.util.List;

import com.dormhub.model.Payment;

public interface PaymentService {
    void addPayment(int paymentId, int residentId, double amount, Date paymentDate, String status);

    void updatePayment(int paymentId, int residentId, double amount, Date paymentDate, String status);

    void deletePayment(int paymentId);

    Payment findById(int paymentId);

    List<Payment> findByResidentId(int residentId);

    List<Payment> findByStatus(String status);

    List<Payment> findAllPayments();
}
