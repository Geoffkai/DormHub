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
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class StyledMessageDialog extends JDialog {
    public enum MessageType {
        INFO(new Color(10, 71, 38), "Message"),
        SUCCESS(new Color(10, 71, 38), "Success"),
        WARNING(new Color(107, 20, 26), "Warning"),
        ERROR(new Color(107, 20, 26), "Error");

        private final Color accentColor;
        private final String defaultTitle;

        MessageType(Color accentColor, String defaultTitle) {
            this.accentColor = accentColor;
            this.defaultTitle = defaultTitle;
        }
    }

    private static final int SHADOW_MARGIN = 12;
    private static final int CARD_WIDTH = 420;
    private static final int CARD_HEIGHT = 220;

    private StyledMessageDialog(Window owner, String title, String message, MessageType type) {
        super(owner instanceof Frame ? (Frame) owner : null, "", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        ShadowPanel root = new ShadowPanel();
        root.setLayout(null);
        setContentPane(root);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 18));
        cardPanel.setBackground(new Color(246, 247, 238));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 207), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        cardPanel.setBounds(SHADOW_MARGIN, SHADOW_MARGIN, CARD_WIDTH, CARD_HEIGHT);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title == null || title.isBlank() ? type.defaultTitle : title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(type.accentColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(type.accentColor);
        accentBar.setPreferredSize(new Dimension(0, 4));

        JLabel messageLabel = new JLabel(toHtml(message), SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 22));
        messageLabel.setForeground(new Color(46, 36, 30));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionsPanel.setOpaque(false);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 18));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(type.accentColor);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 34, 10, 34));
        okButton.setPreferredSize(new Dimension(126, 42));
        okButton.addActionListener(e -> dispose());
        actionsPanel.add(okButton);

        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(messageLabel, BorderLayout.CENTER);
        cardPanel.add(actionsPanel, BorderLayout.SOUTH);

        root.add(cardPanel);
        root.add(accentBar);
        accentBar.setBounds(SHADOW_MARGIN + 24, SHADOW_MARGIN + 62, CARD_WIDTH - 48, 4);

        setSize(CARD_WIDTH + (SHADOW_MARGIN * 2), CARD_HEIGHT + (SHADOW_MARGIN * 2));
        setResizable(false);
        setLocation(904, 510);
    }

    public static void showInfo(Component parent, String title, String message) {
        show(parent, title, message, MessageType.INFO);
    }

    public static void showSuccess(Component parent, String title, String message) {
        show(parent, title, message, MessageType.SUCCESS);
    }

    public static void showWarning(Component parent, String title, String message) {
        show(parent, title, message, MessageType.WARNING);
    }

    public static void showError(Component parent, String title, String message) {
        show(parent, title, message, MessageType.ERROR);
    }

    private static void show(Component parent, String title, String message, MessageType type) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        StyledMessageDialog dialog = new StyledMessageDialog(owner, title, message, type);
        dialog.setVisible(true);
    }

    private static String toHtml(String message) {
        return "<html><div style='text-align:center; width:300px;'>" + escapeHtml(message) + "</div></html>";
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
