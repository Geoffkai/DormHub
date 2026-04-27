package com.dormhub.service.Impl;

import java.sql.Date;
import java.util.List;

import com.dormhub.dao.PaymentDAO;
import com.dormhub.dao.impl.PaymentDAOImpl;
import com.dormhub.model.Payment;
import com.dormhub.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
        private final PaymentDAO paymentDAO;

        public PaymentServiceImpl() {
                this(new PaymentDAOImpl());
        }

        public PaymentServiceImpl(PaymentDAO paymentDAO) {
                this.paymentDAO = paymentDAO;
        }

        @Override
        public void addPayment(int paymentId, int residentId, double amount, Date paymentDate, String status) {
                validatePaymentFields(paymentId, residentId, amount, paymentDate, status);

                if (paymentDAO.findById(paymentId) != null) {
                        throw new IllegalArgumentException("Payment ID already exists: " + paymentId);
                }

                Payment payment = buildPayment(paymentId, residentId, amount, paymentDate, status);
                paymentDAO.insert(payment);
        }

        @Override
        public void updatePayment(int paymentId, int residentId, double amount, Date paymentDate, String status) {
                validatePaymentFields(paymentId, residentId, amount, paymentDate, status);

                Payment payment = paymentDAO.findById(paymentId);
                if (payment == null) {
                        throw new IllegalArgumentException("Payment not found: " + paymentId);
                }

                payment = buildPayment(paymentId, residentId, amount, paymentDate, status);
                paymentDAO.update(payment);
        }

        @Override
        public void deletePayment(int paymentId) {
                if (paymentId <= 0) {
                        throw new IllegalArgumentException("Payment ID must be positive");
                }
                paymentDAO.delete(paymentId);
        }

        @Override
        public Payment findById(int paymentId) {
                if (paymentId <= 0) {
                        throw new IllegalArgumentException("Payment ID must be positive");
                }
                return paymentDAO.findById(paymentId);
        }

        @Override
        public List<Payment> findByResidentId(int residentId) {
                if (residentId <= 0) {
                        throw new IllegalArgumentException("Resident ID must be positive");
                }
                return paymentDAO.findByResidentId(residentId);
        }

        @Override
        public List<Payment> findByStatus(String status) {
                if (status == null || status.trim().isEmpty()) {
                        throw new IllegalArgumentException("Payment status cannot be null or empty");
                }
                return paymentDAO.findByStatus(status);
        }

        @Override
        public List<Payment> findAllPayments() {
                return paymentDAO.findAllPayments();
        }

        private Payment buildPayment(int paymentId, int residentId, double amount, Date paymentDate,
                        String status) {
                Payment payment = new Payment();
                payment.setPaymentId(paymentId);
                payment.setResidentId(residentId);
                payment.setAmount(amount);
                payment.setPaymentDate(paymentDate);
                payment.setStatus(status);

                return payment;
        }

        private void validatePaymentFields(int paymentId, int residentId, double amount, Date paymentDate,
                        String status) {
                if (paymentId <= 0) {
                        throw new IllegalArgumentException("Payment ID must be positive");
                }
                if (residentId <= 0) {
                        throw new IllegalArgumentException("Resident ID must be positive");
                }
                if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                }
                if (paymentDate == null) {
                        throw new IllegalArgumentException("Payment date cannot be null");
                }
                if (status == null || status.trim().isEmpty()) {
                        throw new IllegalArgumentException("Payment status cannot be null or empty");
                }
        }
}