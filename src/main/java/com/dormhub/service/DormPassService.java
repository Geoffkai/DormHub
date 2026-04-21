package com.dormhub.service;

import java.sql.Date;
import java.util.List;

import com.dormhub.model.DormPass;

public interface DormPassService {
    void addDormPass(int passId, int residentId, String type, String reason, String destination, Date dateApplied,
            String status);

    void updateDormPass(int passId, int residentId, String type, String reason, String destination, Date dateApplied,
            String status);

    void deleteDormPass(int passId);

    DormPass findById(int passId);

    List<DormPass> findByResidentId(int residentId);

    List<DormPass> findByStatus(String status);

    List<DormPass> findByType(String type);

    List<DormPass> findAllDormPasses();
}
