package com.dormhub.dao;

import com.dormhub.model;

import java.util.List;
import model.Resident;

public interface ResidentDAO {
    void insert(Resident resident);

    void update(Resident resident);

    void delete(int residentId);

    Resident findById(int residentId);

    List<Resident> findByLastName(String lastName);

    List<Resident> findByProgram(String program);

    List<Resident> findByYearLevel(int yearLevel);

    List<Resident> viewAllResident();

}
