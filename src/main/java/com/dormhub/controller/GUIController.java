package com.dormhub.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    private final ContentPanel contentPanel;

    public GUIController(ResidentService residentService, RoomService roomService,
            RoomAssignmentService roomAssignmentService, PaymentService paymentService,
            DormPassService dormPassService, PanelsHandler panelsHandler, ContentPanel contentPanel) {
        this.residentService = residentService;
        this.roomService = roomService;
        this.roomAssignmentService = roomAssignmentService;
        this.paymentService = paymentService;
        this.dormPassService = dormPassService;
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

        configureAddAction(
            () -> ResidentFormDialog.showAddDialog(contentPanel),
                formData -> saveResident(formData, false),
                this::loadResidents,
                "Resident Added",
                "Resident added successfully.");

        configureUpdateAction(
                contentPanel::getSelectedResident,
                "Update Resident",
                "Select a resident to update.",
                selectedResident -> ResidentFormDialog.showUpdateDialog(
                        contentPanel,
                        new ResidentFormData(
                                String.valueOf(selectedResident.getResidentId()),
                                selectedResident.getLastName(),
                                selectedResident.getFirstName(),
                                selectedResident.getContactNo(),
                                String.valueOf(selectedResident.getYearLevel()),
                                selectedResident.getProgram(),
                                selectedResident.getMoveInDate().toString())),
                formData -> saveResident(formData, true),
                this::loadResidents,
                "Resident Updated",
                "Resident updated successfully.");

        configureDeleteAction(
                contentPanel::getSelectedResident,
                "Delete Resident",
                "Select a resident to delete.",
                "Delete Resident",
                selectedResident -> "Delete resident " + selectedResident.getResidentId() + " ("
                        + selectedResident.getFirstName() + " " + selectedResident.getLastName() + ")?",
                selectedResident -> residentService.deleteResident(selectedResident.getResidentId()),
                this::loadResidents,
                "Resident Deleted",
                "Resident deleted successfully.");

        loadResidents();
    }

    private void saveResident(ResidentFormData formData, boolean update) {
        int residentId = parseIntField(formData.getResidentId(), "Resident ID");
        int yearLevel = parseIntField(formData.getYearLevel(), "Year level");
        Date moveInDate = parseDateField(formData.getMoveInDate(), "Move-in date");

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

        configureAddAction(
            () -> RoomFormDialog.showAddDialog(contentPanel),
                formData -> saveRoom(formData, false),
                this::loadRooms,
                "Room Added",
                "Room added successfully.");

        configureUpdateAction(
                contentPanel::getSelectedRoom,
                "Update Room",
                "Select a room to update.",
                selectedRoom -> RoomFormDialog.showUpdateDialog(
                        contentPanel,
                        new RoomFormData(
                                String.valueOf(selectedRoom.getRoomNo()),
                                selectedRoom.getRoomType(),
                                String.valueOf(selectedRoom.getCapacity()),
                                String.valueOf(selectedRoom.getCurrentOccupancy()))),
                formData -> saveRoom(formData, true),
                this::loadRooms,
                "Room Updated",
                "Room updated successfully.");

        configureDeleteAction(
                contentPanel::getSelectedRoom,
                "Delete Room",
                "Select a room to delete.",
                "Delete Room",
                selectedRoom -> "Delete room " + selectedRoom.getRoomNo() + " (" + selectedRoom.getRoomType() + ")?",
                selectedRoom -> roomService.deleteRoom(selectedRoom.getRoomNo()),
                this::loadRooms,
                "Room Deleted",
                "Room deleted successfully.");

        loadRooms();
    }

    private void saveRoom(RoomFormData formData, boolean update) {
        int roomNo = parseIntField(formData.getRoomNo(), "Room number");
        int capacity = parseIntField(formData.getCapacity(), "Capacity");
        int currentOccupancy = parseIntField(formData.getCurrentOccupancy(), "Current occupancy");

        if (update) {
            roomService.updateRoom(roomNo, formData.getRoomType(), capacity, currentOccupancy);
            return;
        }

        roomService.addRoom(roomNo, formData.getRoomType(), capacity, currentOccupancy);
    }

    public void bindAssignmentActions() {
        contentPanel.setViewAction(e -> loadAssignments());

        configureAddAction(
            () -> AssignmentFormDialog.showAddDialog(contentPanel),
                formData -> saveAssignment(formData, false),
                this::loadAssignments,
                "Assignment Added",
                "Assignment added successfully.");

        configureUpdateAction(
                contentPanel::getSelectedAssignment,
                "Update Assignment",
                "Select an assignment to update.",
                selectedAssignment -> AssignmentFormDialog.showUpdateDialog(
                        contentPanel,
                        new AssignmentFormData(
                                String.valueOf(selectedAssignment.getAssignmentId()),
                                String.valueOf(selectedAssignment.getResidentId()),
                                String.valueOf(selectedAssignment.getRoomId()),
                                selectedAssignment.getDateAssigned().toString(),
                                selectedAssignment.getDateVacated() == null ? ""
                                        : selectedAssignment.getDateVacated().toString())),
                formData -> saveAssignment(formData, true),
                this::loadAssignments,
                "Assignment Updated",
                "Assignment updated successfully.");

        configureDeleteAction(
                contentPanel::getSelectedAssignment,
                "Delete Assignment",
                "Select an assignment to delete.",
                "Delete Assignment",
                selectedAssignment -> "Delete assignment " + selectedAssignment.getAssignmentId() + "?",
                selectedAssignment -> roomAssignmentService.deleteRoomAssignment(selectedAssignment.getAssignmentId()),
                this::loadAssignments,
                "Assignment Deleted",
                "Assignment deleted successfully.");

        loadAssignments();
    }

    private void saveAssignment(AssignmentFormData formData, boolean update) {
        int assignmentId = parseIntField(formData.getAssignmentId(), "Assignment ID");
        int residentId = parseIntField(formData.getResidentId(), "Resident ID");
        int roomId = parseIntField(formData.getRoomId(), "Room ID");
        Date dateAssigned = parseDateField(formData.getDateAssigned(), "Date assigned");
        Date dateVacated = null;

        if (!formData.getDateVacated().isBlank()) {
            dateVacated = parseDateField(formData.getDateVacated(), "Date vacated");
        }

        if (update) {
            roomAssignmentService.updateRoomAssignment(assignmentId, residentId, roomId, dateAssigned, dateVacated);
            return;
        }

        roomAssignmentService.addRoomAssignment(assignmentId, residentId, roomId, dateAssigned, dateVacated);
    }

    public void bindPaymentActions() {
        contentPanel.setViewAction(e -> loadPayments());

        configureAddAction(
            () -> PaymentFormDialog.showAddDialog(contentPanel),
                formData -> savePayment(formData, false),
                this::loadPayments,
                "Payment Added",
                "Payment added successfully.");

        configureUpdateAction(
                contentPanel::getSelectedPayment,
                "Update Payment",
                "Select a payment to update.",
                selectedPayment -> PaymentFormDialog.showUpdateDialog(
                        contentPanel,
                        new PaymentFormData(
                                String.valueOf(selectedPayment.getPaymentId()),
                                String.valueOf(selectedPayment.getResidentId()),
                                String.valueOf(selectedPayment.getAmount()),
                                selectedPayment.getPaymentDate().toString(),
                                selectedPayment.getStatus())),
                formData -> savePayment(formData, true),
                this::loadPayments,
                "Payment Updated",
                "Payment updated successfully.");

        configureDeleteAction(
                contentPanel::getSelectedPayment,
                "Delete Payment",
                "Select a payment to delete.",
                "Delete Payment",
                selectedPayment -> "Delete payment " + selectedPayment.getPaymentId() + "?",
                selectedPayment -> paymentService.deletePayment(selectedPayment.getPaymentId()),
                this::loadPayments,
                "Payment Deleted",
                "Payment deleted successfully.");

        loadPayments();
    }

    private void savePayment(PaymentFormData formData, boolean update) {
        int paymentId = parseIntField(formData.getPaymentId(), "Payment ID");
        int residentId = parseIntField(formData.getResidentId(), "Resident ID");
        double amount = parseDoubleField(formData.getAmount(), "Amount");
        Date paymentDate = parseDateField(formData.getPaymentDate(), "Payment date");

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

        configureAddAction(
            () -> DormPassFormDialog.showAddDialog(contentPanel),
                formData -> saveDormPass(formData, false),
                this::loadDormPasses,
                "Dorm Pass Added",
                "Dorm pass added successfully.");

        configureUpdateAction(
                contentPanel::getSelectedDormPass,
                "Update Dorm Pass",
                "Select a dorm pass to update.",
                selectedDormPass -> DormPassFormDialog.showUpdateDialog(
                        contentPanel,
                        new DormPassFormData(
                                String.valueOf(selectedDormPass.getPassId()),
                                String.valueOf(selectedDormPass.getResidentId()),
                                selectedDormPass.getType(),
                                selectedDormPass.getReason(),
                                selectedDormPass.getDestination(),
                                selectedDormPass.getDateApplied().toString(),
                                selectedDormPass.getStatus())),
                formData -> saveDormPass(formData, true),
                this::loadDormPasses,
                "Dorm Pass Updated",
                "Dorm pass updated successfully.");

        configureDeleteAction(
                contentPanel::getSelectedDormPass,
                "Delete Dorm Pass",
                "Select a dorm pass to delete.",
                "Delete Dorm Pass",
                selectedDormPass -> "Delete dorm pass " + selectedDormPass.getPassId() + "?",
                selectedDormPass -> dormPassService.deleteDormPass(selectedDormPass.getPassId()),
                this::loadDormPasses,
                "Dorm Pass Deleted",
                "Dorm pass deleted successfully.");

        loadDormPasses();
    }

    private void saveDormPass(DormPassFormData formData, boolean update) {
        int passId = parseIntField(formData.getPassId(), "Pass ID");
        int residentId = parseIntField(formData.getResidentId(), "Resident ID");
        Date dateApplied = parseDateField(formData.getDateApplied(), "Date applied");

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
        loadTableData("Residents", "Failed to load residents: ", residentService::findAllResidents,
                contentPanel::showResidentsTable);
    }

    private void loadRooms() {
        loadTableData("Rooms", "Failed to load rooms: ", roomService::findAllRooms, contentPanel::showRoomsTable);
    }

    private void loadAssignments() {
        loadTableData("Assignments", "Failed to load assignments: ", roomAssignmentService::findAllAssignments,
                contentPanel::showAssignmentsTable);
    }

    private void loadPayments() {
        loadTableData("Payments", "Failed to load payments: ", paymentService::findAllPayments,
                contentPanel::showPaymentsTable);
    }

    private void loadDormPasses() {
        loadTableData("Dorm Passes", "Failed to load dorm passes: ", dormPassService::findAllDormPasses,
                contentPanel::showDormPassesTable);
    }

    private int parseIntField(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid whole number.");
        }
    }

    private double parseDoubleField(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private Date parseDateField(String value, String fieldName) {
        try {
            return Date.valueOf(LocalDate.parse(value));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must use YYYY-MM-DD format.");
        }
    }

    private <F> void configureAddAction(
            Supplier<F> showAddDialog,
            Consumer<F> saveAction,
            Runnable reloadAction,
            String successTitle,
            String successMessage) {
        contentPanel.setAddAction(e -> runCrudAction("Add Failed", "Add failed: ", () -> {
            F formData = showAddDialog.get();
            if (formData == null) {
                return;
            }

            saveAction.accept(formData);
            reloadAction.run();
            contentPanel.showSuccessMessage(successTitle, successMessage);
        }));
    }

    private <S, F> void configureUpdateAction(
            Supplier<S> selectedSupplier,
            String warningTitle,
            String warningMessage,
            Function<S, F> showUpdateDialog,
            Consumer<F> saveAction,
            Runnable reloadAction,
            String successTitle,
            String successMessage) {
        contentPanel.setUpdateAction(e -> {
            S selectedItem = selectedSupplier.get();
            if (selectedItem == null) {
                contentPanel.showWarningMessage(warningTitle, warningMessage);
                return;
            }

            runCrudAction("Update Failed", "Update failed: ", () -> {
                F formData = showUpdateDialog.apply(selectedItem);
                if (formData == null) {
                    return;
                }

                saveAction.accept(formData);
                reloadAction.run();
                contentPanel.showSuccessMessage(successTitle, successMessage);
            });
        });
    }

    private <S> void configureDeleteAction(
            Supplier<S> selectedSupplier,
            String warningTitle,
            String warningMessage,
            String confirmTitle,
            Function<S, String> confirmMessageSupplier,
            Consumer<S> deleteAction,
            Runnable reloadAction,
            String successTitle,
            String successMessage) {
        contentPanel.setDeleteAction(e -> {
            S selectedItem = selectedSupplier.get();
            if (selectedItem == null) {
                contentPanel.showWarningMessage(warningTitle, warningMessage);
                return;
            }

            boolean confirmed = StyledConfirmDialog.showConfirm(
                    contentPanel,
                    confirmTitle,
                    confirmMessageSupplier.apply(selectedItem));
            if (!confirmed) {
                return;
            }

            runCrudAction("Delete Failed", "Delete failed: ", () -> {
                deleteAction.accept(selectedItem);
                reloadAction.run();
                contentPanel.showSuccessMessage(successTitle, successMessage);
            });
        });
    }

    private void runCrudAction(String errorTitle, String errorPrefix, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            contentPanel.showErrorMessage(errorTitle, errorPrefix + ex.getMessage());
        }
    }

    private <T> void loadTableData(String errorTitle, String errorMessagePrefix,
            Supplier<List<T>> dataSupplier, Consumer<List<T>> tableRenderer) {
        try {
            tableRenderer.accept(dataSupplier.get());
        } catch (Exception ex) {
            contentPanel.showErrorMessage(errorTitle, errorMessagePrefix + ex.getMessage());
        }
    }
}
