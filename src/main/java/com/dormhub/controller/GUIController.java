package com.dormhub.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JFileChooser;
import javax.swing.JDialog;
import javax.swing.filechooser.FileNameExtensionFilter;

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
        contentPanel.setViewAction(e -> {
            contentPanel.clearSearch();
            loadResidents();
        });
        contentPanel.setExportAction(e -> exportResidents());

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
        contentPanel.setViewAction(e -> {
            contentPanel.clearSearch();
            loadRooms();
        });
        contentPanel.setExportAction(e -> exportRooms());

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
        contentPanel.setViewAction(e -> {
            contentPanel.clearSearch();
            loadAssignments();
        });
        contentPanel.setExportAction(e -> exportAssignments());

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
        contentPanel.setViewAction(e -> {
            contentPanel.clearSearch();
            loadPayments();
        });
        contentPanel.setExportAction(e -> exportPayments());

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
        contentPanel.setViewAction(e -> {
            contentPanel.clearSearch();
            loadDormPasses();
        });
        contentPanel.setExportAction(e -> exportDormPasses());

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

    private void exportResidents() {
        exportToCsv(
                "residents.csv",
                new String[] { "Resident ID", "First Name", "Last Name", "Contact no.", "Year level", "Program",
                        "Move-in-date" },
                residentService::findAllResidents,
                resident -> new Object[] {
                        resident.getResidentId(),
                        resident.getFirstName(),
                        resident.getLastName(),
                        resident.getContactNo(),
                        resident.getYearLevel(),
                        resident.getProgram(),
                        resident.getMoveInDate()
                });
    }

    private void exportRooms() {
        exportToCsv(
                "rooms.csv",
                new String[] { "Room Number", "Room Type", "Capacity", "Current Occupancy" },
                roomService::findAllRooms,
                room -> new Object[] {
                        room.getRoomNo(),
                        room.getRoomType(),
                        room.getCapacity(),
                        room.getCurrentOccupancy()
                });
    }

    private void exportAssignments() {
        exportToCsv(
                "assignments.csv",
                new String[] { "Assignment ID", "Resident ID", "Room ID", "Date Assigned", "Date Vacated" },
                roomAssignmentService::findAllAssignments,
                assignment -> new Object[] {
                        assignment.getAssignmentId(),
                        assignment.getResidentId(),
                        assignment.getRoomId(),
                        assignment.getDateAssigned(),
                        assignment.getDateVacated()
                });
    }

    private void exportPayments() {
        exportToCsv(
                "payments.csv",
                new String[] { "Payment ID", "Resident ID", "Amount", "Payment Date", "Status" },
                paymentService::findAllPayments,
                payment -> new Object[] {
                        payment.getPaymentId(),
                        payment.getResidentId(),
                        payment.getAmount(),
                        payment.getPaymentDate(),
                        payment.getStatus()
                });
    }

    private void exportDormPasses() {
        exportToCsv(
                "dorm_passes.csv",
                new String[] { "Pass ID", "Resident ID", "Type", "Reason", "Destination", "Date Applied", "Status" },
                dormPassService::findAllDormPasses,
                dormPass -> new Object[] {
                        dormPass.getPassId(),
                        dormPass.getResidentId(),
                        dormPass.getType(),
                        dormPass.getReason(),
                        dormPass.getDestination(),
                        dormPass.getDateApplied(),
                        dormPass.getStatus()
                });
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

    private <T> void exportToCsv(String defaultFileName, String[] headers,
            Supplier<List<T>> dataSupplier, Function<T, Object[]> rowMapper) {
        File selectedFile = chooseCsvSaveFile(defaultFileName);
        if (selectedFile == null) {
            return;
        }

        try {
            writeCsv(selectedFile, headers, dataSupplier.get(), rowMapper);
            contentPanel.showMessage(formatExportSuccessMessage(selectedFile));
        } catch (Exception ex) {
            contentPanel.showErrorMessage("Export Failed", "Failed to export CSV: " + ex.getMessage());
        }
    }

    private File chooseCsvSaveFile(String defaultFileName) {
        PositionedFileChooser fileChooser = new PositionedFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fileChooser.setPreferredSize(new java.awt.Dimension(760, 520));

        final boolean[] approved = { false };
        final JDialog dialog = fileChooser.buildDialog(contentPanel);
        fileChooser.addActionListener(e -> {
            approved[0] = JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand());
            dialog.dispose();
        });

        dialog.setModal(true);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocation(720, 400);
        dialog.setVisible(true);

        if (!approved[0] || fileChooser.getSelectedFile() == null) {
            return null;
        }

        return ensureCsvExtension(fileChooser.getSelectedFile());
    }

    private File ensureCsvExtension(File file) {
        if (file.getName().toLowerCase().endsWith(".csv")) {
            return file;
        }

        return new File(file.getParentFile(), file.getName() + ".csv");
    }

    private <T> void writeCsv(File file, String[] headers, List<T> data,
            Function<T, Object[]> rowMapper) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writeCsvRow(writer, headers);

            for (T item : data) {
                Object[] values = rowMapper.apply(item);
                String[] csvValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    csvValues[i] = values[i] == null ? "" : values[i].toString();
                }
                writeCsvRow(writer, csvValues);
            }
        }
    }

    private void writeCsvRow(FileWriter writer, String[] values) throws IOException {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.append(',');
            }
            writer.append(escapeCsv(values[i]));
        }
        writer.append(System.lineSeparator());
    }

    private String escapeCsv(String value) {
        String safeValue = value == null ? "" : value;
        boolean needsQuotes = safeValue.contains(",") || safeValue.contains("\"")
                || safeValue.contains("\n") || safeValue.contains("\r");

        if (!needsQuotes) {
            return safeValue;
        }

        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }

    private String formatExportSuccessMessage(File file) {
        return "CSV exported successfully!\n File Name: " + file.getName();
    }

    private static class PositionedFileChooser extends JFileChooser {
        private JDialog buildDialog(java.awt.Component parent) {
            return super.createDialog(parent);
        }
    }
}
