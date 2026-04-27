package com.dormhub.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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
import com.dormhub.view.AssignmentFormDialog;
import com.dormhub.view.AssignmentFormDialog.AssignmentFormData;
import com.dormhub.view.ContentPanel;
import com.dormhub.view.DormPassFormDialog;
import com.dormhub.view.DormPassFormDialog.DormPassFormData;
import com.dormhub.view.PanelsHandler;
import com.dormhub.view.PaymentFormDialog;
import com.dormhub.view.PaymentFormDialog.PaymentFormData;
import com.dormhub.view.ResidentFormDialog;
import com.dormhub.view.ResidentFormDialog.ResidentFormData;
import com.dormhub.view.RoomFormDialog;
import com.dormhub.view.RoomFormDialog.RoomFormData;
import com.dormhub.view.StyledConfirmDialog;

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

        contentPanel.setAddAction(e -> {
            try {
                ResidentFormData formData = ResidentFormDialog.showAddDialog(contentPanel);
                if (formData == null) {
                    return;
                }

                saveResident(formData, false);
                loadResidents();
                contentPanel.showSuccessMessage("Resident Added", "Resident added successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Add Failed", "Add failed: " + ex.getMessage());
            }
        });

        contentPanel.setUpdateAction(e -> {
            Resident selectedResident = contentPanel.getSelectedResident();
            if (selectedResident == null) {
                contentPanel.showWarningMessage("Update Resident", "Select a resident to update.");
                return;
            }

            try {
                ResidentFormData formData = ResidentFormDialog.showUpdateDialog(
                        contentPanel,
                        new ResidentFormData(
                                String.valueOf(selectedResident.getResidentId()),
                                selectedResident.getLastName(),
                                selectedResident.getFirstName(),
                                selectedResident.getContactNo(),
                                String.valueOf(selectedResident.getYearLevel()),
                                selectedResident.getProgram(),
                                selectedResident.getMoveInDate().toString()));
                if (formData == null) {
                    return;
                }

                saveResident(formData, true);
                loadResidents();
                contentPanel.showSuccessMessage("Resident Updated", "Resident updated successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Update Failed", "Update failed: " + ex.getMessage());
            }
        });

        contentPanel.setDeleteAction(e -> {
            Resident selectedResident = contentPanel.getSelectedResident();
            if (selectedResident == null) {
                contentPanel.showWarningMessage("Delete Resident", "Select a resident to delete.");
                return;
            }

            boolean confirmed = StyledConfirmDialog.showConfirm(
                    contentPanel,
                    "Delete Resident",
                    "Delete resident " + selectedResident.getResidentId() + " (" + selectedResident.getFirstName()
                            + " " + selectedResident.getLastName() + ")?");
            if (!confirmed) {
                return;
            }

            try {
                residentService.deleteResident(selectedResident.getResidentId());

                loadResidents();
                contentPanel.showSuccessMessage("Resident Deleted", "Resident deleted successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Delete Failed", "Delete failed: " + ex.getMessage());
            }
        });

        loadResidents();
    }

    private void saveResident(ResidentFormData formData, boolean update) {
        int residentId = Integer.parseInt(formData.getResidentId());
        int yearLevel = Integer.parseInt(formData.getYearLevel());
        Date moveInDate = Date.valueOf(LocalDate.parse(formData.getMoveInDate()));

        if (update) {
            residentService.updateResident(
                    residentId,
                    formData.getLastName(),
                    formData.getFirstName(),
                    formData.getContactNo(),
                    yearLevel,
                    formData.getProgram(),
                    moveInDate);
            return;
        }

        residentService.addResident(
                residentId,
                formData.getLastName(),
                formData.getFirstName(),
                formData.getContactNo(),
                yearLevel,
                formData.getProgram(),
                moveInDate);
    }

    public void bindRoomActions() {
        contentPanel.setViewAction(e -> loadRooms());

        contentPanel.setAddAction(e -> {
            try {
                RoomFormData formData = RoomFormDialog.showAddDialog(contentPanel);
                if (formData == null) {
                    return;
                }

                saveRoom(formData, false);
                loadRooms();
                contentPanel.showSuccessMessage("Room Added", "Room added successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Add Failed", "Add failed: " + ex.getMessage());
            }
        });

        contentPanel.setUpdateAction(e -> {
            Room selectedRoom = contentPanel.getSelectedRoom();
            if (selectedRoom == null) {
                contentPanel.showWarningMessage("Update Room", "Select a room to update.");
                return;
            }

            try {
                RoomFormData formData = RoomFormDialog.showUpdateDialog(
                        contentPanel,
                        new RoomFormData(
                                String.valueOf(selectedRoom.getRoomNo()),
                                selectedRoom.getRoomType(),
                                String.valueOf(selectedRoom.getCapacity()),
                                String.valueOf(selectedRoom.getCurrentOccupancy())));
                if (formData == null) {
                    return;
                }

                saveRoom(formData, true);
                loadRooms();
                contentPanel.showSuccessMessage("Room Updated", "Room updated successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Update Failed", "Update failed: " + ex.getMessage());
            }
        });

        contentPanel.setDeleteAction(e -> {
            Room selectedRoom = contentPanel.getSelectedRoom();
            if (selectedRoom == null) {
                contentPanel.showWarningMessage("Delete Room", "Select a room to delete.");
                return;
            }

            boolean confirmed = StyledConfirmDialog.showConfirm(
                    contentPanel,
                    "Delete Room",
                    "Delete room " + selectedRoom.getRoomNo() + " (" + selectedRoom.getRoomType() + ")?");
            if (!confirmed) {
                return;
            }

            try {
                roomService.deleteRoom(selectedRoom.getRoomNo());
                loadRooms();
                contentPanel.showSuccessMessage("Room Deleted", "Room deleted successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Delete Failed", "Delete failed: " + ex.getMessage());
            }
        });

        loadRooms();
    }

    private void saveRoom(RoomFormData formData, boolean update) {
        int roomNo = Integer.parseInt(formData.getRoomNo());
        int capacity = Integer.parseInt(formData.getCapacity());
        int currentOccupancy = Integer.parseInt(formData.getCurrentOccupancy());

        if (update) {
            roomService.updateRoom(roomNo, formData.getRoomType(), capacity, currentOccupancy);
            return;
        }

        roomService.addRoom(roomNo, formData.getRoomType(), capacity, currentOccupancy);
    }

    public void bindAssignmentActions() {
        contentPanel.setViewAction(e -> loadAssignments());

        contentPanel.setAddAction(e -> {
            try {
                AssignmentFormData formData = AssignmentFormDialog.showAddDialog(contentPanel);
                if (formData == null) {
                    return;
                }

                saveAssignment(formData, false);
                loadAssignments();
                contentPanel.showSuccessMessage("Assignment Added", "Assignment added successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Add Failed", "Add failed: " + ex.getMessage());
            }
        });

        contentPanel.setUpdateAction(e -> {
            RoomAssignment selectedAssignment = contentPanel.getSelectedAssignment();
            if (selectedAssignment == null) {
                contentPanel.showWarningMessage("Update Assignment", "Select an assignment to update.");
                return;
            }

            try {
                AssignmentFormData formData = AssignmentFormDialog.showUpdateDialog(
                        contentPanel,
                        new AssignmentFormData(
                                String.valueOf(selectedAssignment.getAssignmentId()),
                                String.valueOf(selectedAssignment.getResidentId()),
                                String.valueOf(selectedAssignment.getRoomId()),
                                selectedAssignment.getDateAssigned().toString(),
                                selectedAssignment.getDateVacated() == null ? ""
                                        : selectedAssignment.getDateVacated().toString()));
                if (formData == null) {
                    return;
                }

                saveAssignment(formData, true);
                loadAssignments();
                contentPanel.showSuccessMessage("Assignment Updated", "Assignment updated successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Update Failed", "Update failed: " + ex.getMessage());
            }
        });

        contentPanel.setDeleteAction(e -> {
            RoomAssignment selectedAssignment = contentPanel.getSelectedAssignment();
            if (selectedAssignment == null) {
                contentPanel.showWarningMessage("Delete Assignment", "Select an assignment to delete.");
                return;
            }

            boolean confirmed = StyledConfirmDialog.showConfirm(
                    contentPanel,
                    "Delete Assignment",
                    "Delete assignment " + selectedAssignment.getAssignmentId() + "?");
            if (!confirmed) {
                return;
            }

            try {
                roomAssignmentService.deleteRoomAssignment(selectedAssignment.getAssignmentId());
                loadAssignments();
                contentPanel.showSuccessMessage("Assignment Deleted", "Assignment deleted successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Delete Failed", "Delete failed: " + ex.getMessage());
            }
        });

        loadAssignments();
    }

    private void saveAssignment(AssignmentFormData formData, boolean update) {
        int assignmentId = Integer.parseInt(formData.getAssignmentId());
        int residentId = Integer.parseInt(formData.getResidentId());
        int roomId = Integer.parseInt(formData.getRoomId());
        Date dateAssigned = Date.valueOf(LocalDate.parse(formData.getDateAssigned()));
        Date dateVacated = null;

        if (!formData.getDateVacated().isBlank()) {
            dateVacated = Date.valueOf(LocalDate.parse(formData.getDateVacated()));
        }

        if (update) {
            roomAssignmentService.updateRoomAssignment(assignmentId, residentId, roomId, dateAssigned, dateVacated);
            return;
        }

        roomAssignmentService.addRoomAssignment(assignmentId, residentId, roomId, dateAssigned, dateVacated);
    }

    public void bindPaymentActions() {
        contentPanel.setViewAction(e -> loadPayments());

        contentPanel.setAddAction(e -> {
            try {
                PaymentFormData formData = PaymentFormDialog.showAddDialog(contentPanel);
                if (formData == null) {
                    return;
                }

                savePayment(formData, false);
                loadPayments();
                contentPanel.showSuccessMessage("Payment Added", "Payment added successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Add Failed", "Add failed: " + ex.getMessage());
            }
        });

        contentPanel.setUpdateAction(e -> {
            Payment selectedPayment = contentPanel.getSelectedPayment();
            if (selectedPayment == null) {
                contentPanel.showWarningMessage("Update Payment", "Select a payment to update.");
                return;
            }

            try {
                PaymentFormData formData = PaymentFormDialog.showUpdateDialog(
                        contentPanel,
                        new PaymentFormData(
                                String.valueOf(selectedPayment.getPaymentId()),
                                String.valueOf(selectedPayment.getResidentId()),
                                String.valueOf(selectedPayment.getAmount()),
                                selectedPayment.getPaymentDate().toString(),
                                selectedPayment.getStatus()));
                if (formData == null) {
                    return;
                }

                savePayment(formData, true);
                loadPayments();
                contentPanel.showSuccessMessage("Payment Updated", "Payment updated successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Update Failed", "Update failed: " + ex.getMessage());
            }
        });

        contentPanel.setDeleteAction(e -> {
            Payment selectedPayment = contentPanel.getSelectedPayment();
            if (selectedPayment == null) {
                contentPanel.showWarningMessage("Delete Payment", "Select a payment to delete.");
                return;
            }

            boolean confirmed = StyledConfirmDialog.showConfirm(
                    contentPanel,
                    "Delete Payment",
                    "Delete payment " + selectedPayment.getPaymentId() + "?");
            if (!confirmed) {
                return;
            }

            try {
                paymentService.deletePayment(selectedPayment.getPaymentId());
                loadPayments();
                contentPanel.showSuccessMessage("Payment Deleted", "Payment deleted successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Delete Failed", "Delete failed: " + ex.getMessage());
            }
        });

        loadPayments();
    }

    private void savePayment(PaymentFormData formData, boolean update) {
        int paymentId = Integer.parseInt(formData.getPaymentId());
        int residentId = Integer.parseInt(formData.getResidentId());
        double amount = Double.parseDouble(formData.getAmount());
        Date paymentDate = Date.valueOf(LocalDate.parse(formData.getPaymentDate()));

        if (update) {
            paymentService.updatePayment(
                    paymentId,
                    residentId,
                    amount,
                    paymentDate,
                    formData.getStatus());
            return;
        }

        paymentService.addPayment(
                paymentId,
                residentId,
                amount,
                paymentDate,
                formData.getStatus());
    }

    public void bindDormPassActions() {
        contentPanel.setViewAction(e -> loadDormPasses());

        contentPanel.setAddAction(e -> {
            try {
                DormPassFormData formData = DormPassFormDialog.showAddDialog(contentPanel);
                if (formData == null) {
                    return;
                }

                saveDormPass(formData, false);
                loadDormPasses();
                contentPanel.showSuccessMessage("Dorm Pass Added", "Dorm pass added successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Add Failed", "Add failed: " + ex.getMessage());
            }
        });

        contentPanel.setUpdateAction(e -> {
            DormPass selectedDormPass = contentPanel.getSelectedDormPass();
            if (selectedDormPass == null) {
                contentPanel.showWarningMessage("Update Dorm Pass", "Select a dorm pass to update.");
                return;
            }

            try {
                DormPassFormData formData = DormPassFormDialog.showUpdateDialog(
                        contentPanel,
                        new DormPassFormData(
                                String.valueOf(selectedDormPass.getPassId()),
                                String.valueOf(selectedDormPass.getResidentId()),
                                selectedDormPass.getType(),
                                selectedDormPass.getReason(),
                                selectedDormPass.getDestination(),
                                selectedDormPass.getDateApplied().toString(),
                                selectedDormPass.getStatus()));
                if (formData == null) {
                    return;
                }

                saveDormPass(formData, true);
                loadDormPasses();
                contentPanel.showSuccessMessage("Dorm Pass Updated", "Dorm pass updated successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Update Failed", "Update failed: " + ex.getMessage());
            }
        });

        contentPanel.setDeleteAction(e -> {
            DormPass selectedDormPass = contentPanel.getSelectedDormPass();
            if (selectedDormPass == null) {
                contentPanel.showWarningMessage("Delete Dorm Pass", "Select a dorm pass to delete.");
                return;
            }

            boolean confirmed = StyledConfirmDialog.showConfirm(
                    contentPanel,
                    "Delete Dorm Pass",
                    "Delete dorm pass " + selectedDormPass.getPassId() + "?");
            if (!confirmed) {
                return;
            }

            try {
                dormPassService.deleteDormPass(selectedDormPass.getPassId());
                loadDormPasses();
                contentPanel.showSuccessMessage("Dorm Pass Deleted", "Dorm pass deleted successfully.");
            } catch (Exception ex) {
                contentPanel.showErrorMessage("Delete Failed", "Delete failed: " + ex.getMessage());
            }
        });

        loadDormPasses();
    }

    private void saveDormPass(DormPassFormData formData, boolean update) {
        int passId = Integer.parseInt(formData.getPassId());
        int residentId = Integer.parseInt(formData.getResidentId());
        Date dateApplied = Date.valueOf(LocalDate.parse(formData.getDateApplied()));

        if (update) {
            dormPassService.updateDormPass(
                    passId,
                    residentId,
                    formData.getType(),
                    formData.getReason(),
                    formData.getDestination(),
                    dateApplied,
                    formData.getStatus());
            return;
        }

        dormPassService.addDormPass(
                passId,
                residentId,
                formData.getType(),
                formData.getReason(),
                formData.getDestination(),
                dateApplied,
                formData.getStatus());
    }

    public void bindDashboardActions() {
        // Dashboard currently has no active CRUD bindings.
    }

    private void loadResidents() {
        try {
            List<Resident> residents = residentService.findAllResidents();
            contentPanel.showResidentsTable(residents);
        } catch (Exception ex) {
            contentPanel.showErrorMessage("Residents", "Failed to load residents: " + ex.getMessage());
        }
    }

    private void loadRooms() {
        try {
            List<Room> rooms = roomService.findAllRooms();
            contentPanel.showRoomsTable(rooms);
        } catch (Exception ex) {
            contentPanel.showErrorMessage("Rooms", "Failed to load rooms: " + ex.getMessage());
        }
    }

    private void loadAssignments() {
        try {
            List<RoomAssignment> assignments = roomAssignmentService.findAllAssignments();
            contentPanel.showAssignmentsTable(assignments);
        } catch (Exception ex) {
            contentPanel.showErrorMessage("Assignments", "Failed to load assignments: " + ex.getMessage());
        }
    }

    private void loadPayments() {
        try {
            List<Payment> payments = paymentService.findAllPayments();
            contentPanel.showPaymentsTable(payments);
        } catch (Exception ex) {
            contentPanel.showErrorMessage("Payments", "Failed to load payments: " + ex.getMessage());
        }
    }

    private void loadDormPasses() {
        try {
            List<DormPass> dormPasses = dormPassService.findAllDormPasses();
            contentPanel.showDormPassesTable(dormPasses);
        } catch (Exception ex) {
            contentPanel.showErrorMessage("Dorm Passes", "Failed to load dorm passes: " + ex.getMessage());
        }
    }
}
