package com.dormhub.service.Impl;

import java.sql.Date;
import java.util.List;

import com.dormhub.dao.DormPassDAO;
import com.dormhub.dao.impl.DormPassDAOImpl;
import com.dormhub.model.DormPass;
import com.dormhub.service.DormPassService;

public class DormPassServiceImpl implements DormPassService {
    private final DormPassDAO dormPassDAO;

    public DormPassServiceImpl() {
        this(new DormPassDAOImpl());
    }

    public DormPassServiceImpl(DormPassDAO dormPassDAO) {
        this.dormPassDAO = dormPassDAO;
    }

    @Override
    public void addDormPass(int passId, int residentId, String type, String reason, String destination,
            Date dateApplied,
            String status) {
        validateDormPassFields(passId, residentId, type, reason, destination, status);

        if (dormPassDAO.findById(passId) != null) {
            throw new IllegalArgumentException("Dorm pass ID already exists: " + passId);
        }

        DormPass dormPass = buildDormPass(passId, residentId, type, reason, destination, dateApplied, status);
        dormPassDAO.insert(dormPass);
    }

    @Override
    public void updateDormPass(int passId, int residentId, String type, String reason, String destination,
            Date dateApplied, String status) {
        validateDormPassFields(passId, residentId, type, reason, destination, status);

        if (dormPassDAO.findById(passId) == null) {
            throw new IllegalArgumentException("Dorm pass not found: " + passId);
        }

        DormPass dormPass = buildDormPass(passId, residentId, type, reason, destination, dateApplied, status);
        dormPassDAO.update(dormPass);
    }

    @Override
    public void deleteDormPass(int passId) {
        if (passId <= 0) {
            throw new IllegalArgumentException("Dorm pass ID must be positive.");
        }

        if (dormPassDAO.findById(passId) == null) {
            throw new IllegalArgumentException("Dorm pass not found: " + passId);
        }

        dormPassDAO.delete(passId);
    }

    @Override
    public DormPass findById(int passId) {
        if (passId <= 0) {
            throw new IllegalArgumentException("Dorm pass ID must be positive.");
        }

        return dormPassDAO.findById(passId);
    }

    @Override
    public List<DormPass> findByResidentId(int residentId) {
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive.");
        }

        return dormPassDAO.findByResidentId(residentId);
    }

    @Override
    public List<DormPass> findByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required.");
        }

        return dormPassDAO.findByStatus(status.trim());
    }

    @Override
    public List<DormPass> findByType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type is required.");
        }

        return dormPassDAO.findByType(type.trim());
    }

    @Override
    public List<DormPass> findAllDormPasses() {
        return dormPassDAO.findAllDormPasses();
    }

    private DormPass buildDormPass(int passId, int residentId, String type, String reason, String destination,
            Date dateApplied, String status) {
        DormPass dormPass = new DormPass();
        dormPass.setPassId(passId);
        dormPass.setResidentId(residentId);
        dormPass.setType(type.trim());
        dormPass.setReason(reason.trim());
        dormPass.setDestination(destination.trim());
        dormPass.setDateApplied(dateApplied);
        dormPass.setStatus(status.trim());
        return dormPass;
    }

    private void validateDormPassFields(int passId, int residentId, String type, String reason, String destination,
            String status) {
        if (passId <= 0) {
            throw new IllegalArgumentException("Dorm pass ID must be positive.");
        }
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type is required.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination is required.");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required.");
        }
    }
}
