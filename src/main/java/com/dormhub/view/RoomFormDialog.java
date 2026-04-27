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

public class RoomFormDialog extends JDialog {
    private static final int SHADOW_MARGIN = 12;
    private static final int CONTENT_WIDTH = 720;
    private static final int CONTENT_HEIGHT = 582;

    private final JTextField roomNoField = createTextField();
    private final JTextField roomTypeField = createTextField();
    private final JTextField capacityField = createTextField();
    private final JTextField currentOccupancyField = createTextField();
    private final JLabel titleLabel = new JLabel();

    private RoomFormData formData;

    public RoomFormDialog(Window owner, String title) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        BackgroundImagePanel contentPanel = new BackgroundImagePanel("/img/AURoom.png");
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(formPanel, gbc, 0, "Room Number:", roomNoField);
        addField(formPanel, gbc, 1, "Room Type:", roomTypeField);
        addField(formPanel, gbc, 2, "Capacity:", capacityField);
        addField(formPanel, gbc, 3, "Current Occupancy:", currentOccupancyField);

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
        titleLabel.setBounds(SHADOW_MARGIN + 82, SHADOW_MARGIN + 22, 260, 40);

        formPanel.setBounds(SHADOW_MARGIN + 72, SHADOW_MARGIN + 150, 580, 250);
        actionsPanel.setBounds(SHADOW_MARGIN + 154, SHADOW_MARGIN + 465, 416, 44);

        contentPanel.add(titleLabel);
        contentPanel.add(formPanel);
        contentPanel.add(actionsPanel);

        setSize(CONTENT_WIDTH + (SHADOW_MARGIN * 2), CONTENT_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocation(758 - SHADOW_MARGIN, 390 - SHADOW_MARGIN);
    }

    public static RoomFormData showAddDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        RoomFormDialog dialog = new RoomFormDialog(owner, "Add Room");
        dialog.roomNoField.setEditable(true);
        dialog.roomNoField.setEnabled(true);
        dialog.setVisible(true);
        return dialog.formData;
    }

    public static RoomFormData showUpdateDialog(Component parent, RoomFormData initialData) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        RoomFormDialog dialog = new RoomFormDialog(owner, "Update Room");
        dialog.populateFields(initialData);
        dialog.roomNoField.setEditable(false);
        dialog.roomNoField.setEnabled(false);
        dialog.setVisible(true);
        return dialog.formData;
    }

    private void populateFields(RoomFormData initialData) {
        roomNoField.setText(initialData.getRoomNo());
        roomTypeField.setText(initialData.getRoomType());
        capacityField.setText(initialData.getCapacity());
        currentOccupancyField.setText(initialData.getCurrentOccupancy());
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
        if (roomNoField.getText().isBlank() || roomTypeField.getText().isBlank()
                || capacityField.getText().isBlank() || currentOccupancyField.getText().isBlank()) {
            StyledMessageDialog.showWarning(this, "Room", "Fill in all room fields.");
            return;
        }

        formData = new RoomFormData(
                roomNoField.getText().trim(),
                roomTypeField.getText().trim(),
                capacityField.getText().trim(),
                currentOccupancyField.getText().trim());
        dispose();
    }

    public static class RoomFormData {
        private final String roomNo;
        private final String roomType;
        private final String capacity;
        private final String currentOccupancy;

        public RoomFormData(String roomNo, String roomType, String capacity, String currentOccupancy) {
            this.roomNo = roomNo;
            this.roomType = roomType;
            this.capacity = capacity;
            this.currentOccupancy = currentOccupancy;
        }

        public String getRoomNo() {
            return roomNo;
        }

        public String getRoomType() {
            return roomType;
        }

        public String getCapacity() {
            return capacity;
        }

        public String getCurrentOccupancy() {
            return currentOccupancy;
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final Image backgroundImage;

        BackgroundImagePanel(String path) {
            backgroundImage = new ImageIcon(RoomFormDialog.class.getResource(path)).getImage();
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
