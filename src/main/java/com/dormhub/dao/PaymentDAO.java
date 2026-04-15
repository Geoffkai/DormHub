package com.dormhub.dao;

import java.util.List;

import com.dormhub.model.Payment;

public interface PaymentDAO {
    void insert(Payment payment);

    void update(Payment payment);

    void delete(int paymentId);

    Payment findById(int paymentId);

    List<Payment> findByResidentId(int residentId);

    List<Payment> findByMethod(String method);

    List<Payment> findByStatus(String status);

    List<Payment> findAllPayments();
}
