package com.dormhub.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Font;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ResidentFormDialog extends JDialog {
    private static final int SHADOW_MARGIN = 12;
    private static final int CONTENT_WIDTH = 720;
    private static final int CONTENT_HEIGHT = 582;

    private final JTextField residentIdField = createTextField();
    private final JTextField lastNameField = createTextField();
    private final JTextField firstNameField = createTextField();
    private final JTextField contactNoField = createTextField();
    private final JTextField yearLevelField = createTextField();
    private final JTextField programField = createTextField();
    private final JTextField moveInDateField = createTextField();
    private final JLabel titleLabel = new JLabel();

    private ResidentFormData formData;

    public ResidentFormDialog(Window owner, String title) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        BackgroundImagePanel contentPanel = new BackgroundImagePanel("/img/AUResident.png");
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 15, 5, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(formPanel, gbc, 0, "Resident ID:", residentIdField);
        addField(formPanel, gbc, 1, "Last Name:", lastNameField);
        addField(formPanel, gbc, 2, "First Name:", firstNameField);
        addField(formPanel, gbc, 3, "Contact No:", contactNoField);
        addField(formPanel, gbc, 4, "Year Level:", yearLevelField);
        addField(formPanel, gbc, 5, "Program:", programField);
        addField(formPanel, gbc, 6, "Move-in Date:", moveInDateField);

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
        titleLabel.setBounds(SHADOW_MARGIN + 82, SHADOW_MARGIN + 22, 240, 40);

        formPanel.setBounds(SHADOW_MARGIN + 72, SHADOW_MARGIN + 110, 580, 388);
        actionsPanel.setBounds(SHADOW_MARGIN + 154, SHADOW_MARGIN + 520, 416, 44);

        contentPanel.add(titleLabel);
        contentPanel.add(formPanel);
        contentPanel.add(actionsPanel);

        setSize(CONTENT_WIDTH + (SHADOW_MARGIN * 2), CONTENT_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocation(758 - SHADOW_MARGIN, 390 - SHADOW_MARGIN);
    }

    public static ResidentFormData showAddDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        ResidentFormDialog dialog = new ResidentFormDialog(owner, "Add Resident");
        dialog.residentIdField.setEditable(true);
        dialog.residentIdField.setEnabled(true);
        dialog.setVisible(true);
        return dialog.formData;
    }

    public static ResidentFormData showUpdateDialog(Component parent, ResidentFormData initialData) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        ResidentFormDialog dialog = new ResidentFormDialog(owner, "Update Resident");
        dialog.populateFields(initialData);
        dialog.residentIdField.setEditable(false);
        dialog.residentIdField.setEnabled(false);
        dialog.setVisible(true);
        return dialog.formData;
    }

    private void populateFields(ResidentFormData initialData) {
        residentIdField.setText(initialData.getResidentId());
        lastNameField.setText(initialData.getLastName());
        firstNameField.setText(initialData.getFirstName());
        contactNoField.setText(initialData.getContactNo());
        yearLevelField.setText(initialData.getYearLevel());
        programField.setText(initialData.getProgram());
        moveInDateField.setText(initialData.getMoveInDate());
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
                BorderFactory.createLineBorder(new Color(255, 255, 255, 207), 1),
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
        if (residentIdField.getText().isBlank() || lastNameField.getText().isBlank()
                || firstNameField.getText().isBlank() || contactNoField.getText().isBlank()
                || yearLevelField.getText().isBlank() || programField.getText().isBlank()
                || moveInDateField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Fill in all resident fields.");
            return;
        }

        formData = new ResidentFormData(
                residentIdField.getText().trim(),
                lastNameField.getText().trim(),
                firstNameField.getText().trim(),
                contactNoField.getText().trim(),
                yearLevelField.getText().trim(),
                programField.getText().trim(),
                moveInDateField.getText().trim());
        dispose();
    }

    public static class ResidentFormData {
        private final String residentId;
        private final String lastName;
        private final String firstName;
        private final String contactNo;
        private final String yearLevel;
        private final String program;
        private final String moveInDate;

        public ResidentFormData(String residentId, String lastName, String firstName, String contactNo,
                String yearLevel, String program, String moveInDate) {
            this.residentId = residentId;
            this.lastName = lastName;
            this.firstName = firstName;
            this.contactNo = contactNo;
            this.yearLevel = yearLevel;
            this.program = program;
            this.moveInDate = moveInDate;
        }

        public String getResidentId() {
            return residentId;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getContactNo() {
            return contactNo;
        }

        public String getYearLevel() {
            return yearLevel;
        }

        public String getProgram() {
            return program;
        }

        public String getMoveInDate() {
            return moveInDate;
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final Image backgroundImage;

        BackgroundImagePanel(String path) {
            backgroundImage = new ImageIcon(ResidentFormDialog.class.getResource(path)).getImage();
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
