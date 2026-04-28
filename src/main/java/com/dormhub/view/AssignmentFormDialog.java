package com.dormhub.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.toedter.calendar.JDateChooser;

public class AssignmentFormDialog extends JDialog {
    private static final int SHADOW_MARGIN = 12;
    private static final int CONTENT_WIDTH = 720;
    private static final int CONTENT_HEIGHT = 582;

    private final JTextField assignmentIdField = createTextField();
    private final JTextField residentIdField = createTextField();
    private final JTextField roomIdField = createTextField();
    private final JDateChooser dateAssignedChooser = createDateChooser();
    private final JDateChooser dateVacatedChooser = createDateChooser();
    private final JLabel titleLabel = new JLabel();

    private AssignmentFormData formData;

    public AssignmentFormDialog(Window owner, String title) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        BackgroundImagePanel contentPanel = new BackgroundImagePanel("/img/AUAssignment.png");
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(formPanel, gbc, 0, "Assignment ID:", assignmentIdField);
        addField(formPanel, gbc, 1, "Resident ID:", residentIdField);
        addField(formPanel, gbc, 2, "Room ID:", roomIdField);
        addField(formPanel, gbc, 3, "Date Assigned:", dateAssignedChooser);
        addField(formPanel, gbc, 4, "Date Vacated:", dateVacatedChooser);

        JPanel actionsPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 28, 0));
        actionsPanel.setOpaque(false);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        actionsPanel.add(saveButton);
        actionsPanel.add(cancelButton);

        titleLabel.setOpaque(false);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setText(title);
        titleLabel.setBounds(SHADOW_MARGIN + 82, SHADOW_MARGIN + 22, 320, 40);

        formPanel.setBounds(SHADOW_MARGIN + 72, SHADOW_MARGIN + 132, 580, 305);
        actionsPanel.setBounds(SHADOW_MARGIN + 154, SHADOW_MARGIN + 482, 416, 44);

        contentPanel.add(titleLabel);
        contentPanel.add(formPanel);
        contentPanel.add(actionsPanel);

        setSize(CONTENT_WIDTH + (SHADOW_MARGIN * 2), CONTENT_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocation(720, 340);
    }

    public static AssignmentFormData showAddDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        AssignmentFormDialog dialog = new AssignmentFormDialog(owner, "Add Assignment");
        dialog.assignmentIdField.setEditable(true);
        dialog.assignmentIdField.setEnabled(true);
        dialog.setVisible(true);
        return dialog.formData;
    }

    public static AssignmentFormData showUpdateDialog(Component parent, AssignmentFormData initialData) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        AssignmentFormDialog dialog = new AssignmentFormDialog(owner, "Update Assignment");
        dialog.populateFields(initialData);
        dialog.assignmentIdField.setEditable(false);
        dialog.assignmentIdField.setEnabled(false);
        dialog.setVisible(true);
        return dialog.formData;
    }

    private void populateFields(AssignmentFormData initialData) {
        assignmentIdField.setText(initialData.getAssignmentId());
        residentIdField.setText(initialData.getResidentId());
        roomIdField.setText(initialData.getRoomId());
        setDateField(dateAssignedChooser, initialData.getDateAssigned());
        setDateField(dateVacatedChooser, initialData.getDateVacated());
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setOpaque(false);
        fieldLabel.setForeground(Color.WHITE);
        fieldLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JDateChooser createDateChooser() {
        JDateChooser chooser = new JDateChooser();
        chooser.setDateFormatString("yyyy-MM-dd");
        chooser.setFont(new Font("Arial", Font.PLAIN, 20));
        chooser.setOpaque(false);
        chooser.setPreferredSize(new java.awt.Dimension(25, 38));

        Component editorComponent = chooser.getDateEditor().getUiComponent();
        if (editorComponent instanceof JTextComponent textComponent) {
            textComponent.setEditable(false);
        }

        return chooser;
    }

    private void setDateField(JDateChooser chooser, String dateText) {
        try {
            if (dateText == null || dateText.isBlank()) {
                chooser.setDate(null);
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false);
            chooser.setDate(formatter.parse(dateText.trim()));
        } catch (ParseException e) {
            chooser.setDate(null);
        }
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(25);
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 20));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 170), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        button.setPreferredSize(new java.awt.Dimension(160, 44));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
    }

    private void onSave() {
        if (assignmentIdField.getText().isBlank() || residentIdField.getText().isBlank()
                || roomIdField.getText().isBlank() || dateAssignedChooser.getDate() == null) {
            StyledMessageDialog.showWarning(this, "Assignment", "Fill in all required assignment fields.");
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateAssigned = formatter.format(dateAssignedChooser.getDate());
        String dateVacated = dateVacatedChooser.getDate() == null ? "" : formatter.format(dateVacatedChooser.getDate());

        formData = new AssignmentFormData(
                assignmentIdField.getText().trim(),
                residentIdField.getText().trim(),
                roomIdField.getText().trim(),
                dateAssigned,
                dateVacated);
        dispose();
    }

    public static class AssignmentFormData {
        private final String assignmentId;
        private final String residentId;
        private final String roomId;
        private final String dateAssigned;
        private final String dateVacated;

        public AssignmentFormData(String assignmentId, String residentId, String roomId, String dateAssigned,
                String dateVacated) {
            this.assignmentId = assignmentId;
            this.residentId = residentId;
            this.roomId = roomId;
            this.dateAssigned = dateAssigned;
            this.dateVacated = dateVacated;
        }

        public String getAssignmentId() {
            return assignmentId;
        }

        public String getResidentId() {
            return residentId;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getDateAssigned() {
            return dateAssigned;
        }

        public String getDateVacated() {
            return dateVacated;
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final Image backgroundImage;

        BackgroundImagePanel(String path) {
            backgroundImage = new ImageIcon(AssignmentFormDialog.class.getResource(path)).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 10; i >= 1; i--) {
                int alpha = Math.max(2, 10 - (i / 2));
                g2d.setColor(new Color(0, 0, 0, alpha));
                g2d.fillRoundRect(
                        SHADOW_MARGIN - 2 + i,
                        SHADOW_MARGIN + 2 + i,
                        CONTENT_WIDTH - (i * 2),
                        CONTENT_HEIGHT - (i * 2),
                        24,
                        24);
            }

            g2d.drawImage(backgroundImage, SHADOW_MARGIN, SHADOW_MARGIN, CONTENT_WIDTH, CONTENT_HEIGHT, this);
            g2d.dispose();
        }
    }
}