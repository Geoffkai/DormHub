package com.dormhub.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import com.dormhub.model.DormPass;
import com.dormhub.model.Payment;
import com.dormhub.model.Resident;
import com.dormhub.model.Room;
import com.dormhub.model.RoomAssignment;

public class ContentPanel extends JPanel {

    JButton ViewBtn, AddBtn, UpdateBtn, DeleteBtn, ExportBtn;
    JButton searchBtn;

    // Dashboard panels
    JPanel TR = new JPanel();
    JPanel PDPR = new JPanel();
    JPanel RA = new JPanel();
    JPanel RO = new JPanel();

    // Search
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JTextField searchField;
    private JComboBox<String> attributeComboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    // Scroll pane
    JScrollPane scrollPane;

    public ContentPanel(Dimension screenSize) {
        setLayout(null);
        setBounds(0, 0, screenSize.width, screenSize.height);
        setOpaque(false);

        // Action buttons
        ViewBtn = makeButton("/img/View all.png", (int) 375.1, (int) 120.1, 238, 60);
        AddBtn = makeButton("/img/Add.png", (int) 688.3, (int) 120.1, 238, 60);
        UpdateBtn = makeButton("/img/Update.png", (int) 1001.3, (int) 120.1, 238, 60);
        DeleteBtn = makeButton("/img/Delete.png", (int) 1314.1, (int) 120.1, 238, 60);
        ExportBtn = makeButton("/img/Export.png", (int) 1626.8, (int) 120.1, 238, 60);

        // Search bar
        searchField = new JTextField(23);
        searchField.setPreferredSize(new Dimension(356, 31));
        searchField.setFont(new Font("Arial", Font.PLAIN, 20));

        attributeComboBox = new JComboBox<>(new String[] { "" });
        attributeComboBox.setPreferredSize(new Dimension(205, 31));
        attributeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        attributeComboBox.setBackground(Color.WHITE);

        // Search Button
        searchBtn = new JButton("Search");
        searchBtn.setPreferredSize(new Dimension(123, 35));
        searchBtn.setBackground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 18));
        searchBtn.setFocusPainted(false);

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setForeground(Color.white);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel searchByLabel = new JLabel("Search By: ");
        searchByLabel.setForeground(Color.white);
        searchByLabel.setFont(new Font("Arial", Font.BOLD, 23));

        searchPanel.add(searchByLabel);
        searchPanel.add(attributeComboBox);
        searchPanel.add(Box.createHorizontalStrut(23));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchBtn);
        searchPanel.setBackground(new Color(0, 0, 0, 0));
        searchPanel.setBounds((int) 320.5, (int) 223, (int) 1599.5, (int) 58.6);

        // Table Model
        String[] columnNames = { "Resident ID", "First Name", "Last Name", "Contact no.", "Year level", "Program",
                "Move-in-date" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Table
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setRowHeight(30);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setBackground(new Color(31, 59, 44));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(371, 325, 1497, 701);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        // Sorter
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Live search: update table as user types or changes selected attribute.
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }
        });
        attributeComboBox.addActionListener(e -> applySearchFilter());

        // Exit Button
        ImageIcon exitIcon = ImageResources.loadIcon("/img/ExitGreen.png");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(1840, (int) 26.7, 50, 50);
        exitButton.addActionListener(e -> System.exit(0));

        // Dashboard panels
        TR.setBounds((int) 444.8, (int) 217.2, 534, 200);
        TR.setBackground(Color.WHITE);
        PDPR.setBounds((int) 1218.1, (int) 217.2, 534, 200);
        PDPR.setBackground(Color.WHITE);
        RA.setBounds((int) 1126.2, (int) 652.8, 649, 341);
        RA.setBackground(Color.WHITE);
        RO.setBounds((int) 412.5, (int) 639.3, 598, 368);
        RO.setBackground(Color.WHITE);

        add(searchPanel);
        add(TR);
        add(PDPR);
        add(RA);
        add(RO);
        add(ViewBtn);
        add(AddBtn);
        add(UpdateBtn);
        add(DeleteBtn);
        add(ExportBtn);
        add(exitButton);

        showDashboard(); // initial state
    }

    private JButton makeButton(String path, int x, int y, int w, int h) {
        JButton btn = new JButton(ImageResources.loadIcon(path));
        btn.setBounds(x, y, w, h);
        return btn;
    }

    public void showDashboard() {
        TR.setVisible(true);
        PDPR.setVisible(true);
        RA.setVisible(true);
        RO.setVisible(true);
        ViewBtn.setVisible(false);
        AddBtn.setVisible(false);
        UpdateBtn.setVisible(false);
        DeleteBtn.setVisible(false);
        ExportBtn.setVisible(false);
        searchPanel.setVisible(false);
        table.setVisible(false);
        scrollPane.setVisible(false);
    }

    public void showResidents() {
        showManagerPanel(
                new String[] { "Resident ID", "First Name", "Last Name", "Contact no.", "Year level", "Program",
                        "Move-in-date" },
                "Residents");
    }

    public void showRooms() {
        showManagerPanel(
                new String[] { "Room Number", "Room Type", "Capacity", "Current Occupancy" },
                "Rooms");
    }

    public void showAssignments() {
        showManagerPanel(
                new String[] { "Assignment ID", "Resident ID", "Room ID", "Date Assigned", "Date Vacated" },
                "Assignments");
    }

    public void showPayments() {
        showManagerPanel(
                new String[] { "Payment ID", "Resident ID", "Amount", "Payment Date", "Status" },
                "Payments");
    }

    public void showDormPass() {
        showManagerPanel(
                new String[] { "Pass ID", "Resident ID", "Type", "Reason", "Destination", "Date Applied", "Status" },
                "DormPass");
    }

    private void showManagerPanel(String[] columnNames, String label) {
        TR.setVisible(false);
        PDPR.setVisible(false);
        RA.setVisible(false);
        RO.setVisible(false);
        ViewBtn.setVisible(true);
        AddBtn.setVisible(true);
        UpdateBtn.setVisible(true);
        DeleteBtn.setVisible(true);
        ExportBtn.setVisible(true);
        searchPanel.setVisible(true);
        table.setVisible(true);
        scrollPane.setVisible(true);

        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(columnNames);

        attributeComboBox.removeAllItems();
        for (String columnName : columnNames) {
            attributeComboBox.addItem(columnName);
        }

        bindPlaceholderActions(label);
    }

    // Rebind button actions every time the active panel changes.
    // Always remove old listeners first, or one click may trigger actions
    // from previously opened panels (Residents, Rooms, Payments, etc.).
    // Replace the placeholder listeners below with the real CRUD/search/export
    // logic.
    private void bindPlaceholderActions(String label) {
        // Helper for clearing previously attached ActionListeners from a button
        // before assigning the current panel's behavior.
        resetActionListeners(ViewBtn);
        resetActionListeners(AddBtn);
        resetActionListeners(UpdateBtn);
        resetActionListeners(DeleteBtn);
        resetActionListeners(ExportBtn);
        resetActionListeners(searchBtn);

        ViewBtn.addActionListener(e -> System.out.println("View all " + label + " clicked"));
        AddBtn.addActionListener(e -> System.out.println("Add " + label + " clicked"));
        UpdateBtn.addActionListener(e -> System.out.println("Update " + label + " clicked"));
        DeleteBtn.addActionListener(e -> System.out.println("Delete " + label + " clicked"));
        ExportBtn.addActionListener(e -> System.out.println("Export " + label + " clicked"));
        searchBtn.addActionListener(e -> applySearchFilter());
    }

    private void resetActionListeners(AbstractButton button) {
        for (java.awt.event.ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
    }

    public void setViewAction(ActionListener listener) {
        resetActionListeners(ViewBtn);
        ViewBtn.addActionListener(listener);
    }

    public void setAddAction(ActionListener listener) {
        resetActionListeners(AddBtn);
        AddBtn.addActionListener(listener);
    }

    public void setUpdateAction(ActionListener listener) {
        resetActionListeners(UpdateBtn);
        UpdateBtn.addActionListener(listener);
    }

    public void setDeleteAction(ActionListener listener) {
        resetActionListeners(DeleteBtn);
        DeleteBtn.addActionListener(listener);
    }

    public void setExportAction(ActionListener listener) {
        resetActionListeners(ExportBtn);
        ExportBtn.addActionListener(listener);
    }

    public void setSearchAction(ActionListener listener) {
        resetActionListeners(searchBtn);
        searchBtn.addActionListener(listener);
    }

    public void clearSearch() {
        searchField.setText("");
        attributeComboBox.setSelectedIndex(attributeComboBox.getItemCount() > 0 ? 0 : -1);
        sorter.setRowFilter(null);
    }

    public Resident getSelectedResident() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        Resident resident = new Resident();
        resident.setResidentId(Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString()));
        resident.setFirstName(tableModel.getValueAt(modelRow, 1).toString());
        resident.setLastName(tableModel.getValueAt(modelRow, 2).toString());
        resident.setContactNo(tableModel.getValueAt(modelRow, 3).toString());
        resident.setYearLevel(Integer.parseInt(tableModel.getValueAt(modelRow, 4).toString()));
        resident.setProgram(tableModel.getValueAt(modelRow, 5).toString());
        resident.setMoveInDate(Date.valueOf(tableModel.getValueAt(modelRow, 6).toString()));
        return resident;
    }

    public Room getSelectedRoom() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        Room room = new Room();
        room.setRoomNo(Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString()));
        room.setRoomType(tableModel.getValueAt(modelRow, 1).toString());
        room.setCapacity(Integer.parseInt(tableModel.getValueAt(modelRow, 2).toString()));
        room.setCurrentOccupancy(Integer.parseInt(tableModel.getValueAt(modelRow, 3).toString()));
        return room;
    }

    public RoomAssignment getSelectedAssignment() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        RoomAssignment assignment = new RoomAssignment();
        assignment.setAssignmentId(Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString()));
        assignment.setResidentId(Integer.parseInt(tableModel.getValueAt(modelRow, 1).toString()));
        assignment.setRoomId(Integer.parseInt(tableModel.getValueAt(modelRow, 2).toString()));
        assignment.setDateAssigned(Date.valueOf(tableModel.getValueAt(modelRow, 3).toString()));

        Object vacatedValue = tableModel.getValueAt(modelRow, 4);
        if (vacatedValue != null && !vacatedValue.toString().isBlank()) {
            assignment.setDateVacated(Date.valueOf(vacatedValue.toString()));
        }

        return assignment;
    }

    public Payment getSelectedPayment() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        Payment payment = new Payment();
        payment.setPaymentId(Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString()));
        payment.setResidentId(Integer.parseInt(tableModel.getValueAt(modelRow, 1).toString()));
        payment.setAmount(Double.parseDouble(tableModel.getValueAt(modelRow, 2).toString()));
        payment.setPaymentDate(Date.valueOf(tableModel.getValueAt(modelRow, 3).toString()));
        payment.setStatus(tableModel.getValueAt(modelRow, 4).toString());
        return payment;
    }

    public DormPass getSelectedDormPass() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        DormPass dormPass = new DormPass();
        dormPass.setPassId(Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString()));
        dormPass.setResidentId(Integer.parseInt(tableModel.getValueAt(modelRow, 1).toString()));
        dormPass.setType(tableModel.getValueAt(modelRow, 2).toString());
        dormPass.setReason(tableModel.getValueAt(modelRow, 3).toString());
        dormPass.setDestination(tableModel.getValueAt(modelRow, 4).toString());
        dormPass.setDateApplied(Date.valueOf(tableModel.getValueAt(modelRow, 5).toString()));
        dormPass.setStatus(tableModel.getValueAt(modelRow, 6).toString());
        return dormPass;
    }

    public void showResidentsTable(List<Resident> residents) {
        tableModel.setRowCount(0);
        for (Resident resident : residents) {
            tableModel.addRow(new Object[] {
                    resident.getResidentId(), resident.getFirstName(), resident.getLastName(), resident.getContactNo(),
                    resident.getYearLevel(), resident.getProgram(), resident.getMoveInDate()

            });
        }
    }

    public void showRoomsTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room room : rooms) {
            tableModel.addRow(new Object[] {
                    room.getRoomNo(), room.getRoomType(), room.getCapacity(), room.getCurrentOccupancy()
            });
        }
    }

    public void showAssignmentsTable(List<RoomAssignment> assignments) {
        tableModel.setRowCount(0);
        for (RoomAssignment assignment : assignments) {
            tableModel.addRow(new Object[] {
                    assignment.getAssignmentId(), assignment.getResidentId(), assignment.getRoomId(),
                    assignment.getDateAssigned(), assignment.getDateVacated()
            });
        }
    }

    public void showPaymentsTable(List<Payment> payments) {
        tableModel.setRowCount(0);
        for (Payment payment : payments) {
            tableModel.addRow(new Object[] {
                    payment.getPaymentId(), payment.getResidentId(), payment.getAmount(), payment.getPaymentDate(),
                    payment.getStatus()
            });
        }
    }

    public void showDormPassesTable(List<DormPass> dormPasses) {
        tableModel.setRowCount(0);
        for (DormPass dormPass : dormPasses) {
            tableModel.addRow(new Object[] {
                    dormPass.getPassId(), dormPass.getResidentId(), dormPass.getType(), dormPass.getReason(),
                    dormPass.getDestination(), dormPass.getDateApplied(), dormPass.getStatus()
            });
        }
    }

    public void showMessage(String message) {
        StyledMessageDialog.showInfo(this, "Message", message);
    }

    public void showSuccessMessage(String title, String message) {
        StyledMessageDialog.showSuccess(this, title, message);
    }

    public void showWarningMessage(String title, String message) {
        StyledMessageDialog.showWarning(this, title, message);
    }

    public void showErrorMessage(String title, String message) {
        StyledMessageDialog.showError(this, title, message);
    }

    private void applySearchFilter() {
        String query = searchField.getText();
        if (query == null || query.isBlank()) {
            sorter.setRowFilter(null);
            return;
        }

        String safeQuery = "(?i)" + Pattern.quote(query.trim());
        int selectedColumnIndex = attributeComboBox.getSelectedIndex();

        if (selectedColumnIndex < 0) {
            sorter.setRowFilter(RowFilter.regexFilter(safeQuery));
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter(safeQuery, selectedColumnIndex));
    }
}
