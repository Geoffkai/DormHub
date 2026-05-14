package com.dormhub.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.dormhub.model.Resident;

public class ResidentSearchField extends JTextField {

    private static final int ROW_HEIGHT = 34;
    private static final int MAX_VISIBLE_ROWS = 6;

    private final List<Resident> residents;
    private final JPanel popupPanel;
    private final JList<String> suggestionList;
    private final DefaultListModel<String> listModel;
    private final JScrollPane scroll;
    private boolean selecting;

    public ResidentSearchField(List<Resident> residents, Color borderColor) {
        super(25);
        this.residents = residents != null ? residents : new ArrayList<>();

        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 20));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        listModel = new DefaultListModel<>();

        suggestionList = new JList<>(listModel);
        suggestionList.setFont(new Font("Arial", Font.PLAIN, 16));
        suggestionList.setBackground(new Color(30, 30, 50));
        suggestionList.setForeground(Color.WHITE);
        suggestionList.setSelectionBackground(new Color(80, 80, 140));
        suggestionList.setSelectionForeground(Color.WHITE);
        suggestionList.setFixedCellHeight(ROW_HEIGHT);
        // Keep focus on the text field so key events still work after clicking a row
        suggestionList.setFocusable(false);

        scroll = new JScrollPane(suggestionList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(110, 110, 180), 1));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        scroll.setFocusable(false);

        popupPanel = new JPanel(new BorderLayout());
        popupPanel.add(scroll, BorderLayout.CENTER);
        popupPanel.setVisible(false);
        popupPanel.setFocusable(false);

        // Forward scroll events to the popup when the text field is focused
        addMouseWheelListener(e -> {
            if (!popupPanel.isVisible()) return;
            JScrollBar bar = scroll.getVerticalScrollBar();
            bar.setValue(bar.getValue() + e.getWheelRotation() * ROW_HEIGHT);
        });

        // Forward scroll events that originate over the popup itself
        scroll.addMouseWheelListener(e -> {
            JScrollBar bar = scroll.getVerticalScrollBar();
            bar.setValue(bar.getValue() + e.getWheelRotation() * ROW_HEIGHT);
        });

        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!popupPanel.isVisible()) return;
                int idx  = suggestionList.getSelectedIndex();
                int size = listModel.getSize();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        suggestionList.setSelectedIndex(Math.min(idx + 1, size - 1));
                        suggestionList.ensureIndexIsVisible(suggestionList.getSelectedIndex());
                        e.consume();
                        break;
                    case KeyEvent.VK_UP:
                        suggestionList.setSelectedIndex(Math.max(idx - 1, 0));
                        suggestionList.ensureIndexIsVisible(suggestionList.getSelectedIndex());
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        confirmSelection();
                        e.consume();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        hidePopup();
                        e.consume();
                        break;
                }
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                confirmSelection();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(ResidentSearchField.this::hidePopup);
            }
        });
    }

    @Override
    public void setText(String t) {
        selecting = true;
        super.setText(t);
        selecting = false;
    }

    @Override
    public void removeNotify() {
        hidePopup();
        super.removeNotify();
    }

    private void onTextChanged() {
        if (selecting) return;
        SwingUtilities.invokeLater(this::refreshPopup);
    }

    private void refreshPopup() {
        if (!isShowing()) {
            hidePopup();
            return;
        }

        String query = getText().trim().toLowerCase();
        listModel.clear();

        if (!query.isEmpty()) {
            for (Resident r : residents) {
                String id   = String.valueOf(r.getResidentId());
                String name = r.getFirstName() + " " + r.getLastName();
                if (id.contains(query) || name.toLowerCase().contains(query)) {
                    listModel.addElement(id + " — " + r.getLastName() + ", " + r.getFirstName());
                }
            }
        }

        if (listModel.isEmpty()) {
            hidePopup();
            return;
        }

        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane == null) return;

        JLayeredPane layeredPane = rootPane.getLayeredPane();
        Point loc  = SwingUtilities.convertPoint(this, 0, getHeight(), layeredPane);
        int   rows = Math.min(listModel.getSize(), MAX_VISIBLE_ROWS);
        popupPanel.setBounds(loc.x, loc.y, getWidth(), rows * ROW_HEIGHT + 4);

        if (popupPanel.getParent() != layeredPane) {
            if (popupPanel.getParent() != null) popupPanel.getParent().remove(popupPanel);
            layeredPane.add(popupPanel, JLayeredPane.POPUP_LAYER);
        }

        popupPanel.setVisible(true);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private void hidePopup() {
        if (popupPanel.getParent() != null) {
            Container parent = popupPanel.getParent();
            popupPanel.setVisible(false);
            parent.remove(popupPanel);
            parent.revalidate();
            parent.repaint();
        }
    }

    private void confirmSelection() {
        String selected = suggestionList.getSelectedValue();
        if (selected == null && listModel.getSize() > 0) selected = listModel.getElementAt(0);
        if (selected == null) return;
        setText(selected.split(" — ")[0].trim());
        hidePopup();
    }
}
