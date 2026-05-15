package com.dormhub.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class StyledConfirmDialog extends JDialog {
    private static final int SHADOW_MARGIN = 12;
    private static final int CARD_WIDTH = 440;
    private static final int CARD_HEIGHT = 235;

    private boolean confirmed;

    private StyledConfirmDialog(Window owner, String title, String message) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        ShadowPanel root = new ShadowPanel();
        root.setLayout(null);
        setContentPane(root);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 18));
        cardPanel.setBackground(new Color(247, 241, 232));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        cardPanel.setBounds(SHADOW_MARGIN, SHADOW_MARGIN, CARD_WIDTH, CARD_HEIGHT);

        JLabel titleLabel = new JLabel(title == null || title.isBlank() ? "Confirm Delete" : title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(145, 43, 43));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(145, 43, 43));
        accentBar.setBounds(SHADOW_MARGIN + 24, SHADOW_MARGIN + 62, CARD_WIDTH - 48, 4);

        JLabel messageLabel = new JLabel(toHtml(message), SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setForeground(new Color(46, 36, 30));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        actionsPanel.setOpaque(false);

        JButton deleteButton = new JButton("Delete");
        styleButton(deleteButton, new Color(145, 43, 43), Color.WHITE);
        deleteButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.WHITE, Color.BLACK);
        cancelButton.addActionListener(e -> dispose());

        actionsPanel.add(deleteButton);
        actionsPanel.add(cancelButton);

        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(messageLabel, BorderLayout.CENTER);
        cardPanel.add(actionsPanel, BorderLayout.SOUTH);

        root.add(cardPanel);
        root.add(accentBar);

        setSize(CARD_WIDTH + (SHADOW_MARGIN * 2), CARD_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocation(904, 510);
    }

    public static boolean showConfirm(Component parent, String title, String message) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        StyledConfirmDialog dialog = new StyledConfirmDialog(owner, title, message);
        dialog.setLocation(904, 510);
        dialog.setVisible(true);
        return dialog.confirmed;
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setPreferredSize(new Dimension(130, 42));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
    }

    private static String toHtml(String message) {
        return "<html><div style='text-align:center; width:320px;'>" + escapeHtml(message) + "</div></html>";
    }

    private static String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\n", "<br>");
    }

    private static class ShadowPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 12; i >= 1; i--) {
                g2d.setColor(new Color(0, 0, 0, Math.max(2, 11 - (i / 2))));
                g2d.fillRoundRect(
                        SHADOW_MARGIN - 2 + i,
                        SHADOW_MARGIN + 2 + i,
                        CARD_WIDTH - (i * 2),
                        CARD_HEIGHT - (i * 2),
                        28,
                        28);
            }

            g2d.dispose();
        }
    }
}
