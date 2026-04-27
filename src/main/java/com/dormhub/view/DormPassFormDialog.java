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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DormPassFormDialog extends JDialog {
    private static final int SHADOW_MARGIN = 12;
    private static final int CONTENT_WIDTH = 720;
    private static final int CONTENT_HEIGHT = 582;

    private final JTextField passIdField = createTextField();
    private final JTextField residentIdField = createTextField();
    private final JTextField typeField = createTextField();
    private final JTextField reasonField = createTextField();
    private final JTextField destinationField = createTextField();
    private final JTextField dateAppliedField = createTextField();
    private final JTextField statusField = createTextField();
    private final JLabel titleLabel = new JLabel();

    private DormPassFormData formData;

    public DormPassFormDialog(Window owner, String title) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        BackgroundImagePanel contentPanel = new BackgroundImagePanel("/img/AUDormPass.png");
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 15, 5, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(formPanel, gbc, 0, "Pass ID:", passIdField);
        addField(formPanel, gbc, 1, "Resident ID:", residentIdField);
        addField(formPanel, gbc, 2, "Type:", typeField);
        addField(formPanel, gbc, 3, "Reason:", reasonField);
        addField(formPanel, gbc, 4, "Destination:", destinationField);
        addField(formPanel, gbc, 5, "Date Applied:", dateAppliedField);
        addField(formPanel, gbc, 6, "Status:", statusField);

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
        titleLabel.setBounds(SHADOW_MARGIN + 82, SHADOW_MARGIN + 22, 300, 40);

        formPanel.setBounds(SHADOW_MARGIN + 72, SHADOW_MARGIN + 108, 580, 360);
        actionsPanel.setBounds(SHADOW_MARGIN + 154, SHADOW_MARGIN + 500, 416, 44);

        contentPanel.add(titleLabel);
        contentPanel.add(formPanel);
        contentPanel.add(actionsPanel);

        setSize(CONTENT_WIDTH + (SHADOW_MARGIN * 2), CONTENT_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocation(758 - SHADOW_MARGIN, 390 - SHADOW_MARGIN);
    }

    public static DormPassFormData showAddDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        DormPassFormDialog dialog = new DormPassFormDialog(owner, "Add Dorm Pass");
        dialog.passIdField.setEditable(true);
        dialog.passIdField.setEnabled(true);
        dialog.setVisible(true);
        return dialog.formData;
    }

    public static DormPassFormData showUpdateDialog(Component parent, DormPassFormData initialData) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        DormPassFormDialog dialog = new DormPassFormDialog(owner, "Update Dorm Pass");
        dialog.populateFields(initialData);
        dialog.passIdField.setEditable(false);
        dialog.passIdField.setEnabled(false);
        dialog.setVisible(true);
        return dialog.formData;
    }

    private void populateFields(DormPassFormData initialData) {
        passIdField.setText(initialData.getPassId());
        residentIdField.setText(initialData.getResidentId());
        typeField.setText(initialData.getType());
        reasonField.setText(initialData.getReason());
        destinationField.setText(initialData.getDestination());
        dateAppliedField.setText(initialData.getDateApplied());
        statusField.setText(initialData.getStatus());
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
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
        if (passIdField.getText().isBlank() || residentIdField.getText().isBlank()
                || typeField.getText().isBlank() || reasonField.getText().isBlank()
                || destinationField.getText().isBlank() || dateAppliedField.getText().isBlank()
                || statusField.getText().isBlank()) {
            StyledMessageDialog.showWarning(this, "Dorm Pass", "Fill in all dorm pass fields.");
            return;
        }

        formData = new DormPassFormData(
                passIdField.getText().trim(),
                residentIdField.getText().trim(),
                typeField.getText().trim(),
                reasonField.getText().trim(),
                destinationField.getText().trim(),
                dateAppliedField.getText().trim(),
                statusField.getText().trim());
        dispose();
    }

    public static class DormPassFormData {
        private final String passId;
        private final String residentId;
        private final String type;
        private final String reason;
        private final String destination;
        private final String dateApplied;
        private final String status;

        public DormPassFormData(String passId, String residentId, String type, String reason, String destination,
                String dateApplied, String status) {
            this.passId = passId;
            this.residentId = residentId;
            this.type = type;
            this.reason = reason;
            this.destination = destination;
            this.dateApplied = dateApplied;
            this.status = status;
        }

        public String getPassId() {
            return passId;
        }

        public String getResidentId() {
            return residentId;
        }

        public String getType() {
            return type;
        }

        public String getReason() {
            return reason;
        }

        public String getDestination() {
            return destination;
        }

        public String getDateApplied() {
            return dateApplied;
        }

        public String getStatus() {
            return status;
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final Image backgroundImage;

        BackgroundImagePanel(String path) {
            backgroundImage = new ImageIcon(DormPassFormDialog.class.getResource(path)).getImage();
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
