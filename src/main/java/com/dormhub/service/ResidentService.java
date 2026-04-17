package com.dormhub.service;

import java.sql.Date;
import java.util.List;

import com.dormhub.model.Resident;

public interface ResidentService {
    void addResident(int residentId, String lastName, String firstName, String contactNo, int yearLevel, String program,
            Date moveInDate);

    void updateResident(int residentId, String lastName, String firstName, String contactNo, int yearLevel,
            String program,
            Date moveInDate);

    void deleteResident(int residentId);

    Resident findById(int residentId);

    List<Resident> findByLastName(String lastName);

    List<Resident> findAllResidents();
}
