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

public class PaymentFormDialog extends JDialog {
    private static final int SHADOW_MARGIN = 12;
    private static final int CONTENT_WIDTH = 720;
    private static final int CONTENT_HEIGHT = 582;

    private final JTextField paymentIdField = createTextField();
    private final JTextField residentIdField = createTextField();
    private final JTextField amountField = createTextField();
    private final JDateChooser paymentDateChooser = createDateChooser();
    private final JTextField statusField = createTextField();
    private final JLabel titleLabel = new JLabel();

    private PaymentFormData formData;

    public PaymentFormDialog(Window owner, String title) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        BackgroundImagePanel contentPanel = new BackgroundImagePanel("/img/AUPayment.png");
        contentPanel.setLayout(null);
        setContentPane(contentPanel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(formPanel, gbc, 0, "Payment ID:", paymentIdField);
        addField(formPanel, gbc, 1, "Resident ID:", residentIdField);
        addField(formPanel, gbc, 2, "Amount:", amountField);
        addField(formPanel, gbc, 3, "Payment Date:", paymentDateChooser);
        addField(formPanel, gbc, 4, "Status:", statusField);

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
        titleLabel.setBounds(SHADOW_MARGIN + 82, SHADOW_MARGIN + 22, 280, 40);

        formPanel.setBounds(SHADOW_MARGIN + 72, SHADOW_MARGIN + 138, 580, 292);
        actionsPanel.setBounds(SHADOW_MARGIN + 154, SHADOW_MARGIN + 490, 416, 44);

        contentPanel.add(titleLabel);
        contentPanel.add(formPanel);
        contentPanel.add(actionsPanel);

        setSize(CONTENT_WIDTH + (SHADOW_MARGIN * 2), CONTENT_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public static PaymentFormData showAddDialog(Component parent) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        PaymentFormDialog dialog = new PaymentFormDialog(owner, "Add Payment");
        dialog.paymentIdField.setEditable(true);
        dialog.paymentIdField.setEnabled(true);
        dialog.setVisible(true);
        return dialog.formData;
    }

    public static PaymentFormData showUpdateDialog(Component parent, PaymentFormData initialData) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        PaymentFormDialog dialog = new PaymentFormDialog(owner, "Update Payment");
        dialog.populateFields(initialData);
        dialog.paymentIdField.setEditable(false);
        dialog.paymentIdField.setEnabled(false);
        dialog.setVisible(true);
        return dialog.formData;
    }

    private void populateFields(PaymentFormData initialData) {
        paymentIdField.setText(initialData.getPaymentId());
        residentIdField.setText(initialData.getResidentId());
        amountField.setText(initialData.getAmount());
        setDateField(paymentDateChooser, initialData.getPaymentDate());
        statusField.setText(initialData.getStatus());
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
        if (paymentIdField.getText().isBlank() || residentIdField.getText().isBlank()
                || amountField.getText().isBlank() || paymentDateChooser.getDate() == null
                || statusField.getText().isBlank()) {
            StyledMessageDialog.showWarning(this, "Payment", "Fill in all payment fields.");
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        formData = new PaymentFormData(
                paymentIdField.getText().trim(),
                residentIdField.getText().trim(),
                amountField.getText().trim(),
                formatter.format(paymentDateChooser.getDate()),
                statusField.getText().trim());
        dispose();
    }

    public static class PaymentFormData {
        private final String paymentId;
        private final String residentId;
        private final String amount;
        private final String paymentDate;
        private final String status;

        public PaymentFormData(String paymentId, String residentId, String amount, String paymentDate, String status) {
            this.paymentId = paymentId;
            this.residentId = residentId;
            this.amount = amount;
            this.paymentDate = paymentDate;
            this.status = status;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public String getResidentId() {
            return residentId;
        }

        public String getAmount() {
            return amount;
        }

        public String getPaymentDate() {
            return paymentDate;
        }

        public String getStatus() {
            return status;
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final Image backgroundImage;

        BackgroundImagePanel(String path) {
            backgroundImage = new ImageIcon(PaymentFormDialog.class.getResource(path)).getImage();
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