package com.dormhub.controller;

import java.util.List;

import javax.swing.JOptionPane;

import com.dormhub.model.DormPass;
import com.dormhub.model.Payment;
import com.dormhub.model.Resident;
import com.dormhub.model.Room;
import com.dormhub.model.RoomAssignment;
import com.dormhub.service.DormPassService;
import com.dormhub.service.PaymentService;
import com.dormhub.service.ResidentService;
import com.dormhub.service.RoomAssignmentService;
import com.dormhub.service.RoomService;
import com.dormhub.view.ContentPanel;
import com.dormhub.view.PanelsHandler;

public class GUIController {
    private final ResidentService residentService;
    private final RoomService roomService;
    private final DormPassService dormPassService;
    private final PaymentService paymentService;
    private final RoomAssignmentService roomAssignmentService;

    private final PanelsHandler panelsHandler;
    private final ContentPanel contentPanel;

    public GUIController(ResidentService residentService, RoomService roomService,
            RoomAssignmentService roomAssignmentService, PaymentService paymentService,
            DormPassService dormPassService, PanelsHandler panelsHandler, ContentPanel contentPanel) {
        this.residentService = residentService;
        this.roomService = roomService;
        this.roomAssignmentService = roomAssignmentService;
        this.paymentService = paymentService;
        this.dormPassService = dormPassService;
        this.panelsHandler = panelsHandler;
        this.contentPanel = contentPanel;

        panelsHandler.setOnResidentsSelected(this::bindResidentActions);
        panelsHandler.setOnRoomsSelected(this::bindRoomActions);
        panelsHandler.setOnAssignmentsSelected(this::bindAssignmentActions);
        panelsHandler.setOnPaymentsSelected(this::bindPaymentActions);
        panelsHandler.setOnDormPassSelected(this::bindDormPassActions);
        panelsHandler.setOnDashboardSelected(this::bindDashboardActions);

        bindResidentActions();
    }

    private void bindResidentActions() {
        contentPanel.setViewAction(e -> loadResidents());

        // contentPanel.setAddAction(e -> {
        // try {
        // // residentService.addResident(...);
        // } catch (Exception ex) {
        // contentPanel.showMessage("Add failed: " + ex.getMessage());
        // }
        // });

        contentPanel.setDeleteAction(e -> {
            try {
                String input = JOptionPane.showInputDialog("Enter resident ID to delete:");
                if (input == null || input.isBlank()) {
                    return;
                }

                int residentId = Integer.parseInt(input);
                residentService.deleteResident(residentId);

                loadResidents();
                contentPanel.showMessage("Resident deleted successfully.");
            } catch (Exception ex) {
                contentPanel.showMessage("Delete failed: " + ex.getMessage());
            }
        });

        loadResidents();
    }

    public void bindRoomActions() {
        contentPanel.setViewAction(e -> loadRooms());
        // contentPanel.setAddAction(e -> roomService.addRoom(roomNo, roomType,
        // capacity, currentOccupancy));
        // contentPanel.setUpdateAction(e -> roomService.updateRoom(roomNo, roomType,
        // capacity, currentOccupancy));
        // contentPanel.setDeleteAction(e -> roomService.deleteRoom(roomNo));
        // contentPanel.setSearchAction(e -> roomService.findByRoomNo(roomNo));

        loadRooms();
    }

    public void bindAssignmentActions() {
        contentPanel.setViewAction(e -> loadAssignments());
        // contentPanel.setAddAction(e ->
        // roomAssignmentService.addRoomAssignment(assignmentId,
        // residentId, roomId, dateAssigned, dateVacated));
        // contentPanel.setUpdateAction(e ->
        // roomAssignmentService.updateRoomAssignment(assignmentId,
        // residentId, roomId, dateAssigned, dateVacated));
        // contentPanel.setDeleteAction(e ->
        // roomAssignmentService.deleteRoomAssignment(assignmentId));
        // contentPanel.setSearchAction(e ->
        // roomAssignmentService.findById(assignmentId));

        loadAssignments();
    }

    public void bindPaymentActions() {
        contentPanel.setViewAction(e -> loadPayments());
        // contentPanel.setAddAction(e -> paymentService.addPayment(paymentId,
        // residentId, amount, paymentDate, method, status));
        // contentPanel.setUpdateAction(e -> paymentService.updatePayment(paymentId,
        // residentId, amount, paymentDate, method, status));
        // contentPanel.setDeleteAction(e -> paymentService.deletePayment(paymentId));
        // contentPanel.setSearchAction(e -> paymentService.findById(paymentId));

        loadPayments();
    }

    public void bindDormPassActions() {
        contentPanel.setViewAction(e -> loadDormPasses());
        // contentPanel.setAddAction(e -> dormPassService.addDormPass(passId,
        // residentId, type, reason, destination, dateApplied, status));
        // contentPanel.setUpdateAction(e -> dormPassService.updateDormPass(passId,
        // residentId, type, reason, destination, dateApplied, status));
        // contentPanel.setDeleteAction(e -> dormPassService.deleteDormPass(passId));
        // contentPanel.setSearchAction(e -> dormPassService.findById(passId));

        loadDormPasses();
    }

    public void bindDashboardActions() {
        // Dashboard currently has no active CRUD bindings.
    }

    private void loadResidents() {
        try {
            List<Resident> residents = residentService.findAllResidents();
            contentPanel.showResidentsTable(residents);
        } catch (Exception ex) {
            contentPanel.showMessage("Failed to load residents: " + ex.getMessage());
        }
    }

    private void loadRooms() {
        try {
            List<Room> rooms = roomService.findAllRooms();
            contentPanel.showRoomsTable(rooms);
        } catch (Exception ex) {
            contentPanel.showMessage("Failed to load rooms: " + ex.getMessage());
        }
    }

    private void loadAssignments() {
        try {
            List<RoomAssignment> assignments = roomAssignmentService.findAllAssignments();
            contentPanel.showAssignmentsTable(assignments);
        } catch (Exception ex) {
            contentPanel.showMessage("Failed to load assignments: " + ex.getMessage());
        }
    }

    private void loadPayments() {
        try {
            List<Payment> payments = paymentService.findAllPayments();
            contentPanel.showPaymentsTable(payments);
        } catch (Exception ex) {
            contentPanel.showMessage("Failed to load payments: " + ex.getMessage());
        }
    }

    private void loadDormPasses() {
        try {
            List<DormPass> dormPasses = dormPassService.findAllDormPasses();
            contentPanel.showDormPassesTable(dormPasses);
        } catch (Exception ex) {
            contentPanel.showMessage("Failed to load dorm passes: " + ex.getMessage());
        }
    }
}
