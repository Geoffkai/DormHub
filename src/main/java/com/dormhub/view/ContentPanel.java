package com.dormhub.view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

import com.dormhub.model.DormPass;
import com.dormhub.model.Payment;
import com.dormhub.model.Resident;
import com.dormhub.model.Room;
import com.dormhub.model.RoomAssignment;

public class ContentPanel extends JPanel {

    JButton ViewBtn, AddBtn, UpdateBtn, DeleteBtn, ExportBtn;
    JButton searchBtn;

    // Dashboard panels
    JPanel TR   = new JPanel();
    JPanel PDPR = new JPanel();
    JPanel RA   = new JPanel();
    JPanel RO   = new JPanel();

    // Search
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JTextField searchField;
    private JComboBox<String> attributeComboBox;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    JScrollPane scrollPane;

    // Dashboard colors
    private static final Color DARK_GREEN  = new Color(31, 59, 44);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color CARD_BORDER = new Color(220, 230, 225);
    private static final Color TEXT_DARK   = Color.BLACK;
    private static final Color TEXT_MUTED  = new Color(80, 80, 80);

    // Program color palette
    private static final Color[] PROGRAM_COLORS = {
        new Color(255, 222, 24),
        new Color(209, 225, 3),
        new Color(248, 255, 156),
        new Color(140, 165, 3),
        new Color(5, 99, 45),
        new Color(178, 246, 80),
        new Color(236, 255, 240),
        new Color(144, 206, 142),
        new Color(84, 197, 40),
        new Color(31, 158, 65),
    };

    // Live dashboard data
    private List<Resident>       _dashResidents    = new ArrayList<>();
    private List<DormPass>       _dashDormPasses   = new ArrayList<>();
    private List<Room>           _dashRooms        = new ArrayList<>();
    private List<RoomAssignment> _dashAssignments  = new ArrayList<>();
    private List<Payment>        _dashPayments     = new ArrayList<>();

    public ContentPanel(Dimension screenSize) {
        setLayout(null);
        setBounds(0, 0, screenSize.width, screenSize.height);
        setOpaque(false);

        // Action buttons
        ViewBtn   = makeButton("/img/View all.png", (int) 375.1,  (int) 120.1, 238, 60);
        AddBtn    = makeButton("/img/Add.png",       (int) 688.3,  (int) 120.1, 238, 60);
        UpdateBtn = makeButton("/img/Update.png",    (int) 1001.3, (int) 120.1, 238, 60);
        DeleteBtn = makeButton("/img/Delete.png",    (int) 1314.1, (int) 120.1, 238, 60);
        ExportBtn = makeButton("/img/Export.png",    (int) 1626.8, (int) 120.1, 238, 60);

        // Search bar
        searchField = new JTextField(23);
        searchField.setPreferredSize(new Dimension(356, 31));
        searchField.setFont(new Font("Arial", Font.PLAIN, 20));

        attributeComboBox = new JComboBox<>(new String[]{""});
        attributeComboBox.setPreferredSize(new Dimension(205, 31));
        attributeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        attributeComboBox.setBackground(Color.WHITE);

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

        // Table model
        String[] columnNames = { "Resident ID", "First Name", "Last Name", "Contact no.", "Year level", "Program",
                "Move-in-date" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Live search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { applySearchFilter(); }
            @Override public void removeUpdate(DocumentEvent e)  { applySearchFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });
        attributeComboBox.addActionListener(e -> applySearchFilter());

        // Exit button
        ImageIcon exitIcon = ImageResources.loadIcon("/img/ExitGreen.png");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(1840, (int) 26.7, 50, 50);
        exitButton.addActionListener(e -> System.exit(0));

        TR.setBounds((int) 448.2, 198, 534, 240);
        TR.setOpaque(true);
        TR.setBackground(new Color(0, 0, 0, 0));
        TR.setLayout(new BorderLayout());
        TR.setBorder(new EmptyBorder(4, 8, 4, 8));

        PDPR.setBounds((int) 1218.1, (int) 198.1, 535, 240);
        PDPR.setOpaque(true);
        PDPR.setBackground(new Color(0, 0, 0, 0));
        PDPR.setLayout(new BorderLayout());
        PDPR.setBorder(new EmptyBorder(4, 8, 4, 8));

        RA.setBounds((int) 1126.2, 630, 649, 373);
        RA.setOpaque(true);
        RA.setBackground(new Color(0, 0, 0, 0));
        RA.setLayout(new BorderLayout());
        RA.setBorder(new EmptyBorder(4, 8, 4, 8));

        RO.setBounds((int) 412.5, 640, 598, 260);
        RO.setOpaque(true);
        RO.setBackground(Color.WHITE);
        RO.setLayout(new BorderLayout());
        RO.setBorder(new EmptyBorder(4, 8, 4, 8));

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

        showDashboard();
    }

    // ── Dashboard Data Refresh ─────────────────────────────────────────────

    public void refreshDashboard(List<Resident> residents, List<DormPass> dormPasses,
                                  List<Room> rooms, List<RoomAssignment> assignments,
                                  List<Payment> payments) {
        _dashResidents   = residents   != null ? residents   : new ArrayList<>();
        _dashDormPasses  = dormPasses  != null ? dormPasses  : new ArrayList<>();
        _dashRooms       = rooms       != null ? rooms       : new ArrayList<>();
        _dashAssignments = assignments != null ? assignments : new ArrayList<>();
        _dashPayments    = payments    != null ? payments    : new ArrayList<>();
        rebuildDashboardPanels();
    }

    private void rebuildDashboardPanels() {
        TR.removeAll();
        PDPR.removeAll();
        RA.removeAll();
        RO.removeAll();

        buildTotalResidentsPanel();
        buildPendingDormPassPanel();
        buildRecentActivityPanel();
        buildRoomOccupancyPanel();

        TR.revalidate();   TR.repaint();
        PDPR.revalidate(); PDPR.repaint();
        RA.revalidate();   RA.repaint();
        RO.revalidate();   RO.repaint();
    }

    // ── TR: Total Residents ────────────────────────────────────────────────

    private void buildTotalResidentsPanel() {
        Map<String, Integer> programCount = new LinkedHashMap<>();
        for (Resident r : _dashResidents) {
            String prog = r.getProgram() == null || r.getProgram().isBlank() ? "Unknown" : r.getProgram();
            programCount.merge(prog, 1, Integer::sum);
        }
        int total = _dashResidents.size();

        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setOpaque(false);

        // --- Pie chart on the LEFT ---
        java.util.List<Integer> values = new ArrayList<>(programCount.values());
        java.util.List<Color> colors = new ArrayList<>();
        int colorIndex = 0;
        for (String ignored : programCount.keySet()) {
            colors.add(PROGRAM_COLORS[colorIndex % PROGRAM_COLORS.length]);
            colorIndex++;
        }

        JPanel pieChart = new PieChartPanel(values, colors, total);
        pieChart.setOpaque(false);
        pieChart.setPreferredSize(new Dimension(200, 210));

        // --- Breakdown on the RIGHT ---

        JPanel breakdown = new JPanel();
        breakdown.setOpaque(false);
        breakdown.setLayout(new BoxLayout(breakdown, BoxLayout.Y_AXIS));

        final int itemsPerRow = 3;
        JPanel currentRow = null;
        int i = 0;
        for (Map.Entry<String, Integer> e : programCount.entrySet()) {
            if (i % itemsPerRow == 0) {
                currentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
                currentRow.setOpaque(false);
                currentRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                breakdown.add(currentRow);
            }

            Color c = PROGRAM_COLORS[i % PROGRAM_COLORS.length];
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
            item.setOpaque(false);

            JLabel dot = new JLabel("\u25cf");
            dot.setFont(new Font("Arial", Font.PLAIN, 25));
            dot.setForeground(c);

            JLabel lbl = new JLabel(e.getKey() + " (" + e.getValue() + ")");
            lbl.setFont(new Font("Arial", Font.BOLD, 16));
            lbl.setForeground(Color.WHITE);

            item.add(dot);
            item.add(lbl);
            currentRow.add(item);
            i++;
        }

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(breakdown);

        body.add(pieChart, BorderLayout.WEST);
        body.add(rightPanel, BorderLayout.CENTER);

        TR.removeAll();
        TR.add(body, BorderLayout.CENTER);
        TR.revalidate();
        TR.repaint();
    }

    // ── PDPR: Pending Dorm Pass Requests ──────────────────────────────────
    private void buildPendingDormPassPanel() {
        List<DormPass> pending = new ArrayList<>();
        for (DormPass dp : _dashDormPasses) {
            String s = dp.getStatus();
            if (s == null || s.isBlank() || s.equalsIgnoreCase("Pending")) pending.add(dp);
        }

        pending.sort((a, b) -> {
            if (a.getDateApplied() == null && b.getDateApplied() == null) return 0;
            if (a.getDateApplied() == null) return 1;
            if (b.getDateApplied() == null) return -1;
            return b.getDateApplied().compareTo(a.getDateApplied());
        });

        // --- List ---
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (pending.isEmpty()) {
            JLabel none = new JLabel("No pending requests", SwingConstants.CENTER);
            none.setFont(new Font("Arial", Font.ITALIC, 13));
            none.setForeground(TEXT_MUTED);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            list.add(none);
        } else {
            for (DormPass dp : pending) {
                list.add(buildDormPassRow(dp));
                list.add(Box.createVerticalStrut(8));
            }
        }

        Color dashBg = new Color(31, 59, 44);

        list.setOpaque(true);
        list.setBackground(dashBg);

        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setOpaque(false);
        listWrapper.add(list, BorderLayout.NORTH);

        listWrapper.setOpaque(true);
        listWrapper.setBackground(dashBg);


        JScrollPane listScrollPane = new JScrollPane(listWrapper) {
            @Override
            public void paint(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paint(g);
            }
        };
        listScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        listScrollPane.setOpaque(true);
        listScrollPane.setBackground(dashBg);
        listScrollPane.getViewport().setOpaque(true);
        listScrollPane.getViewport().setBackground(dashBg);


        // --- Count bar pinned to bottom ---
        JPanel countBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        countBar.setOpaque(false);
        countBar.setBorder(new EmptyBorder(10, 0, 0, 0));
        countBar.setPreferredSize(new Dimension(0, 30));
        JLabel countLbl = new JLabel("Total pending: " + pending.size());
        countLbl.setFont(new Font("Arial", Font.BOLD, 15));
        countLbl.setForeground(pending.isEmpty() ? new Color(107, 20, 26) : new Color(255, 255, 255));
        countBar.add(countLbl);

        // --- Clear and rebuild PDPR ---
        PDPR.removeAll();
        PDPR.setLayout(new BorderLayout());
        PDPR.add(listScrollPane, BorderLayout.CENTER);
        PDPR.add(countBar, BorderLayout.SOUTH);
        PDPR.revalidate();
        PDPR.repaint();
    }

    private JPanel buildDormPassRow(DormPass dp) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(true);
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(3, 6, 3, 6)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel badge = new JLabel(dp.getType() == null ? "\u2014" : dp.getType());
        badge.setFont(new Font("Arial", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(new Color(31, 59, 44, 174));
        badge.setBorder(new EmptyBorder(2, 5, 2, 5));
        badge.setPreferredSize(new Dimension(80, 18));
        badge.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        left.setOpaque(false);
        left.add(badge);

        String dest = dp.getDestination() == null || dp.getDestination().isBlank() ? "" : " \u2192 " + dp.getDestination();
        JLabel info = new JLabel("Resident #" + dp.getResidentId() + dest);
        info.setFont(new Font("Arial", Font.PLAIN, 15));
        info.setForeground(Color.BLACK);

        JLabel date = new JLabel(dp.getDateApplied() == null ? "" : dp.getDateApplied().toString());
        date.setFont(new Font("Arial", Font.ITALIC, 13));
        date.setForeground(TEXT_MUTED);

        row.add(left,  BorderLayout.WEST);
        row.add(info,  BorderLayout.CENTER);
        row.add(date,  BorderLayout.EAST);
        return row;
    }

    // ── RA: Recent Activity ────────────────────────────────────────────────

    private void buildRecentActivityPanel() {
        List<ActivityItem> events = new ArrayList<>();

        for (Payment p : _dashPayments) {
            if (p.getPaymentDate() != null) {
                events.add(new ActivityItem(p.getPaymentDate(),
                        "Payment \u20b1" + String.format("%.0f", p.getAmount()) + " \u2014 Resident #" + p.getResidentId(),
                        "\ud83d\udcb3", new Color(43, 112, 0)));
            }
        }
        for (RoomAssignment a : _dashAssignments) {
            if (a.getDateAssigned() != null)
                events.add(new ActivityItem(a.getDateAssigned(),
                        "Resident #" + a.getResidentId() + " assigned to Room " + a.getRoomId(),
                        "\ud83c\udfe0", new Color(81, 190, 21)));
            if (a.getDateVacated() != null)
                events.add(new ActivityItem(a.getDateVacated(),
                        "Resident #" + a.getResidentId() + " vacated Room " + a.getRoomId(),
                        "\ud83d\udce4", new Color(181, 208, 2)));
        }
        for (DormPass dp : _dashDormPasses) {
            if (dp.getDateApplied() != null)
                events.add(new ActivityItem(dp.getDateApplied(),
                        "Dorm pass applied \u2014 Resident #" + dp.getResidentId(),
                        "\ud83d\udccb", new Color(224, 182, 3)));
        }

        events.sort((a, b) -> b.date.compareTo(a.date));
        List<ActivityItem> shown = new ArrayList<>(events); // show all

        Color dashBg = new Color(107, 20, 26);

        JPanel list = new JPanel();
        list.setOpaque(true);
        list.setBackground(dashBg);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (shown.isEmpty()) {
            JLabel none = new JLabel("No recent activity", SwingConstants.CENTER);
            none.setFont(new Font("Arial", Font.ITALIC, 15));
            none.setForeground(TEXT_MUTED);
            none.setAlignmentX(Component.CENTER_ALIGNMENT);
            list.add(none);
        } else {
            for (ActivityItem item : shown) {
                list.add(buildActivityRow(item));
                list.add(Box.createVerticalStrut(8));
            }
        }

        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setOpaque(true);
        listWrapper.setBackground(dashBg);
        listWrapper.add(list, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(listWrapper) {
            @Override
            public void paint(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paint(g);
            }
        };
        scrollPane.setOpaque(true);
        scrollPane.setBackground(dashBg);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(dashBg);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        RA.removeAll();
        RA.setLayout(new BorderLayout());
        RA.add(scrollPane, BorderLayout.CENTER);
        RA.revalidate();
        RA.repaint();
    }

    private JPanel buildActivityRow(ActivityItem item) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(true);
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, item.color),
            new EmptyBorder(4, 6, 4, 6)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel icon = new JLabel(item.icon);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        icon.setPreferredSize(new Dimension(28, 20));

        JLabel label = new JLabel(item.label);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);

        JLabel date = new JLabel(item.date.toString());
        date.setFont(new Font("Arial", Font.PLAIN, 12));
        date.setForeground(TEXT_MUTED);

        row.add(icon,  BorderLayout.WEST);
        row.add(label, BorderLayout.CENTER);
        row.add(date,  BorderLayout.EAST);
        return row;
    }

    // ── RO: Room Occupancy ─────────────────────────────────────────────────

    private void buildRoomOccupancyPanel() {
        int totalCapacity  = _dashRooms.stream().mapToInt(Room::getCapacity).sum();
        int totalOccupied  = _dashRooms.stream().mapToInt(Room::getCurrentOccupancy).sum();
        int totalAvailable = Math.max(0, totalCapacity - totalOccupied);
        double pct = totalCapacity == 0 ? 0 : (totalOccupied * 100.0 / totalCapacity);

        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 3));
        statsBar.setOpaque(false);
        statsBar.add(statChip("Capacity",  String.valueOf(totalCapacity),  new Color(179, 138, 1)));
        statsBar.add(statChip("Occupied",  String.valueOf(totalOccupied),  new Color(10, 71, 38)));
        statsBar.add(statChip("Available", String.valueOf(totalAvailable), new Color(3, 175, 74)));
        statsBar.add(statChip("Fill Rate", String.format("%.0f%%", pct),  new Color(80, 80, 80)));

        OccupancyBarChart chart = new OccupancyBarChart(_dashRooms);

        JPanel body = new JPanel(new BorderLayout(2, 4));
        body.setOpaque(false);
        body.add(statsBar, BorderLayout.NORTH);
        body.add(chart,    BorderLayout.CENTER);

        RO.add(body, BorderLayout.CENTER);
    }

    private JPanel statChip(String label, String value, Color color) {
        JPanel chip = new JPanel(new BorderLayout(0, 0));
        chip.setOpaque(true);
        chip.setBackground(new Color(245, 245, 245));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(3, 5, 3, 8)
        ));
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Arial", Font.BOLD, 20));
        val.setForeground(color);
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        chip.add(val, BorderLayout.CENTER);
        chip.add(lbl, BorderLayout.SOUTH);
        return chip;
    }

    // ── Inner classes ──────────────────────────────────────────────────────

    private class OccupancyBarChart extends JPanel {
        private final List<Room> rooms;
        OccupancyBarChart(List<Room> rooms) { this.rooms = rooms; setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (rooms.isEmpty()) {
                g.setColor(TEXT_MUTED);
                g.setFont(new Font("Arial", Font.ITALIC, 13));
                g.drawString("No room data", 20, getHeight() / 2);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int n = Math.min(rooms.size(), 8);
            int padLeft = 68, padRight = 50, padTop = 6;
            int barHeight = (getHeight() - padTop * 2) / n - 5;
            int chartW = getWidth() - padLeft - padRight;
            g2.setFont(new Font("Arial", Font.PLAIN, 11));

            for (int i = 0; i < n; i++) {
                Room room = rooms.get(i);
                int cap = Math.max(1, room.getCapacity());
                int occ = Math.min(room.getCurrentOccupancy(), cap);
                int y = padTop + i * (barHeight + 5);

                g2.setColor(Color.BLACK);
                g2.drawString("Rm " + room.getRoomNo(), 4, y + barHeight - 3);

                g2.setColor(new Color(220, 220, 220));
                g2.fillRoundRect(padLeft, y, chartW, barHeight, 6, 6);

                int fillW = (int) ((double) occ / cap * chartW);
                Color barColor = occ >= cap ? new Color(43, 112, 0, 255)
                    : occ >= cap * 0.75 ? new Color(81, 190, 21)
                    : new Color(181, 208, 2);
                if (fillW > 0) {
                    g2.setColor(barColor);
                    g2.fillRoundRect(padLeft, y, fillW, barHeight, 6, 6);
                }
                g2.setColor(Color.BLACK);
                g2.drawString(occ + "/" + cap, padLeft + chartW + 4, y + barHeight - 3);
            }
            g2.dispose();
        }
    }

    private static class ActivityItem {
        final Date date; final String label, icon; final Color color;
        ActivityItem(Date date, String label, String icon, Color color) {
            this.date = date; this.label = label; this.icon = icon; this.color = color;
        }
    }

    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }
        @Override public Dimension minimumLayoutSize(Container target)   { return layoutSize(target, false); }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int width = 0, height = 0, rowWidth = 0, rowHeight = 0;
                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            width = Math.max(width, rowWidth);
                            height += rowHeight + vgap;
                            rowWidth = 0; rowHeight = 0;
                        }
                        if (rowWidth != 0) rowWidth += hgap;
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                width = Math.max(width, rowWidth);
                height += rowHeight;
                return new Dimension(width + insets.left + insets.right + hgap * 2,
                                     height + insets.top + insets.bottom + vgap * 2);
            }
        }
    }

    private static class PieChartPanel extends JPanel {
        private final java.util.List<Integer> values;
        private final java.util.List<Color> colors;
        private final int total;

        PieChartPanel(java.util.List<Integer> values, java.util.List<Color> colors, int total) {
            this.values = values;
            this.colors = colors;
            this.total = total;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 15;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if (total <= 0 || values.isEmpty()) {
                g2.setColor(new Color(220, 230, 225));
                g2.fillOval(x, y, size, size);
            } else {
                int startAngle = 90;
                int drawnAngle = 0;
                for (int i = 0; i < values.size(); i++) {
                    int arcAngle = (i == values.size() - 1)
                            ? 360 - drawnAngle
                            : (int) Math.round((values.get(i) * 360.0) / total);
                    g2.setColor(colors.get(i % colors.size()));
                    g2.fillArc(x, y, size, size, startAngle, -arcAngle);
                    startAngle -= arcAngle;
                    drawnAngle += arcAngle;
                }
            }

            int innerSize = (int) (size * 0.5);
            int innerX = x + (size - innerSize) / 2;
            int innerY = y + (size - innerSize) / 2;
            g2.setColor(new Color(0x1F3B2C));
            g2.fillOval(innerX, innerY, innerSize, innerSize);

            String totalText = String.valueOf(total);
            Font numberFont = new Font("Arial", Font.BOLD, 40);
            FontMetrics numberMetrics = g2.getFontMetrics(numberFont);

            g2.setColor(new Color(0xEAF8F2));
            g2.setFont(numberFont);
            int numberX = (getWidth() - numberMetrics.stringWidth(totalText)) / 2;
            int numberY = (getHeight() + numberMetrics.getAscent()) / 2 - 6;
            g2.drawString(totalText, numberX, numberY);

            g2.dispose();
        }
    }

    // ── Visibility toggles ─────────────────────────────────────────────────

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
                new String[]{"Resident ID", "First Name", "Last Name", "Contact no.", "Year level", "Program", "Move-in-date"},
                "Residents");
    }

    public void showRooms() {
        showManagerPanel(new String[]{"Room Number", "Room Type", "Capacity", "Current Occupancy"}, "Rooms");
    }

    public void showAssignments() {
        showManagerPanel(new String[]{"Assignment ID", "Resident ID", "Room ID", "Date Assigned", "Date Vacated"}, "Assignments");
    }

    public void showPayments() {
        showManagerPanel(new String[]{"Payment ID", "Resident ID", "Amount", "Payment Date", "Status"}, "Payments");
    }

    public void showDormPass() {
        showManagerPanel(new String[]{"Pass ID", "Resident ID", "Type", "Reason", "Destination", "Date Applied", "Status"}, "DormPass");
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
        for (String columnName : columnNames) attributeComboBox.addItem(columnName);

        bindPlaceholderActions(label);
    }

    private void bindPlaceholderActions(String label) {
        resetActionListeners(ViewBtn);
        resetActionListeners(AddBtn);
        resetActionListeners(UpdateBtn);
        resetActionListeners(DeleteBtn);
        resetActionListeners(ExportBtn);
        resetActionListeners(searchBtn);

        ViewBtn.addActionListener(e   -> System.out.println("View all " + label + " clicked"));
        AddBtn.addActionListener(e    -> System.out.println("Add " + label + " clicked"));
        UpdateBtn.addActionListener(e -> System.out.println("Update " + label + " clicked"));
        DeleteBtn.addActionListener(e -> System.out.println("Delete " + label + " clicked"));
        ExportBtn.addActionListener(e -> System.out.println("Export " + label + " clicked"));
        searchBtn.addActionListener(e -> applySearchFilter());
    }

    private void resetActionListeners(AbstractButton button) {
        for (java.awt.event.ActionListener listener : button.getActionListeners())
            button.removeActionListener(listener);
    }

    // ── Public action setters ──────────────────────────────────────────────

    public void setViewAction(ActionListener listener)   { resetActionListeners(ViewBtn);   ViewBtn.addActionListener(listener); }
    public void setAddAction(ActionListener listener)    { resetActionListeners(AddBtn);    AddBtn.addActionListener(listener); }
    public void setUpdateAction(ActionListener listener) { resetActionListeners(UpdateBtn); UpdateBtn.addActionListener(listener); }
    public void setDeleteAction(ActionListener listener) { resetActionListeners(DeleteBtn); DeleteBtn.addActionListener(listener); }
    public void setExportAction(ActionListener listener) { resetActionListeners(ExportBtn); ExportBtn.addActionListener(listener); }
    public void setSearchAction(ActionListener listener) { resetActionListeners(searchBtn); searchBtn.addActionListener(listener); }

    public void clearSearch() {
        searchField.setText("");
        attributeComboBox.setSelectedIndex(attributeComboBox.getItemCount() > 0 ? 0 : -1);
        sorter.setRowFilter(null);
    }

    // ── Selected row getters ───────────────────────────────────────────────

    public Resident getSelectedResident() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int r = table.convertRowIndexToModel(viewRow);
        Resident resident = new Resident();
        resident.setResidentId(Integer.parseInt(tableModel.getValueAt(r, 0).toString()));
        resident.setFirstName(tableModel.getValueAt(r, 1).toString());
        resident.setLastName(tableModel.getValueAt(r, 2).toString());
        resident.setContactNo(tableModel.getValueAt(r, 3).toString());
        resident.setYearLevel(Integer.parseInt(tableModel.getValueAt(r, 4).toString()));
        resident.setProgram(tableModel.getValueAt(r, 5).toString());
        resident.setMoveInDate(Date.valueOf(tableModel.getValueAt(r, 6).toString()));
        return resident;
    }

    public Room getSelectedRoom() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int r = table.convertRowIndexToModel(viewRow);
        Room room = new Room();
        room.setRoomNo(Integer.parseInt(tableModel.getValueAt(r, 0).toString()));
        room.setRoomType(tableModel.getValueAt(r, 1).toString());
        room.setCapacity(Integer.parseInt(tableModel.getValueAt(r, 2).toString()));
        room.setCurrentOccupancy(Integer.parseInt(tableModel.getValueAt(r, 3).toString()));
        return room;
    }

    public RoomAssignment getSelectedAssignment() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int r = table.convertRowIndexToModel(viewRow);
        RoomAssignment assignment = new RoomAssignment();
        assignment.setAssignmentId(Integer.parseInt(tableModel.getValueAt(r, 0).toString()));
        assignment.setResidentId(Integer.parseInt(tableModel.getValueAt(r, 1).toString()));
        assignment.setRoomId(Integer.parseInt(tableModel.getValueAt(r, 2).toString()));
        assignment.setDateAssigned(Date.valueOf(tableModel.getValueAt(r, 3).toString()));
        Object vacatedValue = tableModel.getValueAt(r, 4);
        if (vacatedValue != null && !vacatedValue.toString().isBlank())
            assignment.setDateVacated(Date.valueOf(vacatedValue.toString()));
        return assignment;
    }

    public Payment getSelectedPayment() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int r = table.convertRowIndexToModel(viewRow);
        Payment payment = new Payment();
        payment.setPaymentId(Integer.parseInt(tableModel.getValueAt(r, 0).toString()));
        payment.setResidentId(Integer.parseInt(tableModel.getValueAt(r, 1).toString()));
        payment.setAmount(Double.parseDouble(tableModel.getValueAt(r, 2).toString()));
        payment.setPaymentDate(Date.valueOf(tableModel.getValueAt(r, 3).toString()));
        payment.setStatus(tableModel.getValueAt(r, 4).toString());
        return payment;
    }

    public DormPass getSelectedDormPass() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int r = table.convertRowIndexToModel(viewRow);
        DormPass dormPass = new DormPass();
        dormPass.setPassId(Integer.parseInt(tableModel.getValueAt(r, 0).toString()));
        dormPass.setResidentId(Integer.parseInt(tableModel.getValueAt(r, 1).toString()));
        dormPass.setType(tableModel.getValueAt(r, 2).toString());
        dormPass.setReason(tableModel.getValueAt(r, 3).toString());
        dormPass.setDestination(tableModel.getValueAt(r, 4).toString());
        dormPass.setDateApplied(Date.valueOf(tableModel.getValueAt(r, 5).toString()));
        dormPass.setStatus(tableModel.getValueAt(r, 6).toString());
        return dormPass;
    }

    // ── Table loaders ──────────────────────────────────────────────────────

    public void showResidentsTable(List<Resident> residents) {
        tableModel.setRowCount(0);
        for (Resident resident : residents)
            tableModel.addRow(new Object[]{resident.getResidentId(), resident.getFirstName(), resident.getLastName(),
                    resident.getContactNo(), resident.getYearLevel(), resident.getProgram(), resident.getMoveInDate()});
    }

    public void showRoomsTable(List<Room> rooms) {
        tableModel.setRowCount(0);
        for (Room room : rooms)
            tableModel.addRow(new Object[]{room.getRoomNo(), room.getRoomType(), room.getCapacity(), room.getCurrentOccupancy()});
    }

    public void showAssignmentsTable(List<RoomAssignment> assignments) {
        tableModel.setRowCount(0);
        for (RoomAssignment a : assignments)
            tableModel.addRow(new Object[]{a.getAssignmentId(), a.getResidentId(), a.getRoomId(),
                    a.getDateAssigned(), a.getDateVacated()});
    }

    public void showPaymentsTable(List<Payment> payments) {
        tableModel.setRowCount(0);
        for (Payment payment : payments)
            tableModel.addRow(new Object[]{payment.getPaymentId(), payment.getResidentId(), payment.getAmount(),
                    payment.getPaymentDate(), payment.getStatus()});
    }

    public void showDormPassesTable(List<DormPass> dormPasses) {
        tableModel.setRowCount(0);
        for (DormPass dormPass : dormPasses)
            tableModel.addRow(new Object[]{dormPass.getPassId(), dormPass.getResidentId(), dormPass.getType(),
                    dormPass.getReason(), dormPass.getDestination(), dormPass.getDateApplied(), dormPass.getStatus()});
    }

    // ── Dialogs ────────────────────────────────────────────────────────────

    public void showMessage(String message)                      { StyledMessageDialog.showInfo(this, "Message", message); }
    public void showSuccessMessage(String title, String message) { StyledMessageDialog.showSuccess(this, title, message); }
    public void showWarningMessage(String title, String message) { StyledMessageDialog.showWarning(this, title, message); }
    public void showErrorMessage(String title, String message)   { StyledMessageDialog.showError(this, title, message); }

    // ── Search filter ──────────────────────────────────────────────────────

    private void applySearchFilter() {
        String query = searchField.getText();
        if (query == null || query.isBlank()) { sorter.setRowFilter(null); return; }
        String safeQuery = "(?i)" + Pattern.quote(query.trim());
        int selectedColumnIndex = attributeComboBox.getSelectedIndex();
        if (selectedColumnIndex < 0) { sorter.setRowFilter(RowFilter.regexFilter(safeQuery)); return; }
        sorter.setRowFilter(RowFilter.regexFilter(safeQuery, selectedColumnIndex));
    }
}
