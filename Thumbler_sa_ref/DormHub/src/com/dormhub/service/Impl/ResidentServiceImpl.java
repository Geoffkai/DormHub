package com.dormhub.service.Impl;

import java.sql.Date;
import java.util.List;

import com.dormhub.dao.ResidentDAO;
import com.dormhub.dao.RoomAssignmentDAO;
import com.dormhub.dao.RoomDAO;
import com.dormhub.dao.impl.ResidentDAOImpl;
import com.dormhub.dao.impl.RoomAssignmentDAOImpl;
import com.dormhub.dao.impl.RoomDAOImpl;
import com.dormhub.model.Resident;
import com.dormhub.model.Room;
import com.dormhub.model.RoomAssignment;
import com.dormhub.service.ResidentService;

public class ResidentServiceImpl implements ResidentService {
    private final ResidentDAO residentDAO;
    private final RoomAssignmentDAO roomAssignmentDAO;
    private final RoomDAO roomDAO;

    public ResidentServiceImpl() {
        this(new ResidentDAOImpl(), new RoomAssignmentDAOImpl(), new RoomDAOImpl());
    }

    public ResidentServiceImpl(ResidentDAO residentDAO) {
        this(residentDAO, new RoomAssignmentDAOImpl(), new RoomDAOImpl());
    }

    public ResidentServiceImpl(ResidentDAO residentDAO, RoomAssignmentDAO roomAssignmentDAO, RoomDAO roomDAO) {
        this.residentDAO = residentDAO;
        this.roomAssignmentDAO = roomAssignmentDAO;
        this.roomDAO = roomDAO;
    }

    @Override
    public void addResident(int residentId, String lastName, String firstName, String contactNo, int yearLevel,
            String program,
            Date moveInDate) {
        validateResidentFields(residentId, lastName, firstName, contactNo, yearLevel, program);

        if (residentDAO.findById(residentId) != null) {
            throw new IllegalArgumentException("Resident ID already exists: " + residentId);
        }

        Resident resident = buildResident(residentId, lastName, firstName, contactNo, yearLevel, program, moveInDate);
        residentDAO.insert(resident);
    }

    @Override
    public void updateResident(int residentId, String lastName, String firstName, String contactNo, int yearLevel,
            String program,
            Date moveInDate) {
        validateResidentFields(residentId, lastName, firstName, contactNo, yearLevel, program);

        if (residentDAO.findById(residentId) == null) {
            throw new IllegalArgumentException("Resident not found: " + residentId);
        }

        Resident resident = buildResident(residentId, lastName, firstName, contactNo, yearLevel, program, moveInDate);
        residentDAO.update(resident);
    }

    @Override
    public void deleteResident(int residentId) {
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive.");
        }

        if (residentDAO.findById(residentId) == null) {
            throw new IllegalArgumentException("Resident not found: " + residentId);
        }

        for (RoomAssignment assignment : roomAssignmentDAO.findByResidentId(residentId)) {
            if (assignment.getDateVacated() == null) {
                Room room = roomDAO.findByRoomNo(assignment.getRoomId());
                if (room != null && room.getCurrentOccupancy() > 0) {
                    room.setCurrentOccupancy(room.getCurrentOccupancy() - 1);
                    roomDAO.update(room);
                }
            }
        }

        residentDAO.delete(residentId);
    }

    @Override
    public Resident findById(int residentId) {
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive.");
        }

        return residentDAO.findById(residentId);
    }

    @Override
    public List<Resident> findByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        return residentDAO.findByLastName(lastName.trim());
    }

    @Override
    public List<Resident> findAllResidents() {
        return residentDAO.findAllResidents();
    }

    private Resident buildResident(int residentId, String lastName, String firstName, String contactNo, int yearLevel,
            String program, Date moveInDate) {
        Resident resident = new Resident();
        resident.setResidentId(residentId);
        resident.setLastName(lastName.trim());
        resident.setFirstName(firstName.trim());
        resident.setContactNo(contactNo.trim());
        resident.setYearLevel(yearLevel);
        resident.setProgram(program.trim());
        resident.setMoveInDate(moveInDate);
        return resident;
    }

    private void validateResidentFields(int residentId, String lastName, String firstName, String contactNo,
            int yearLevel,
            String program) {
        if (residentId <= 0) {
            throw new IllegalArgumentException("Resident ID must be positive.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (contactNo == null || !contactNo.matches("\\d{11}")) {
            throw new IllegalArgumentException("Contact number must be exactly 11 digits.");
        }
        if (yearLevel < 1 || yearLevel > 9) {
            throw new IllegalArgumentException("Year level must be between 1 and 9.");
        }
        if (program == null || program.isBlank()) {
            throw new IllegalArgumentException("Program is required.");
        }
    }
}
