package com.dormhub.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class PanelsHandler extends JPanel {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Main background, manager dashboard
    BackgroundPanel mgr = new BackgroundPanel("/img/MngDshB.png");
    // Content changes on right side
    ContentPanel contentPanel;

    public PanelsHandler() {
        setLayout(null);
        setBounds(0, 0, screenSize.width, screenSize.height);

        mgr.setLayout(null);
        mgr.setBounds(0, 0, screenSize.width, screenSize.height);

        contentPanel = new ContentPanel(screenSize);

        // Side panels
        BackgroundPanel residentMngrBtn = new BackgroundPanel("/img/10w.png", false);
        residentMngrBtn.setBounds(0, (int) 217.2, 320, 144);
        residentMngrBtn.setOpaque(false);
        addHoverEffect(residentMngrBtn, "/img/10.png", "/img/10w.png", "/img/ResM.png",
                () -> contentPanel.showResidents());

        BackgroundPanel roomMngrBtn = new BackgroundPanel("/img/11w.png", false);
        roomMngrBtn.setBounds(0, (int) 361.3, 320, 144);
        roomMngrBtn.setOpaque(false);
        addHoverEffect(roomMngrBtn, "/img/11.png", "/img/11w.png", "/img/RoomM.png", () -> contentPanel.showRooms());

        BackgroundPanel assignmentMngrBtn = new BackgroundPanel("/img/12w.png", false);
        assignmentMngrBtn.setBounds(0, (int) 505.4, 320, 144);
        assignmentMngrBtn.setOpaque(false);
        addHoverEffect(assignmentMngrBtn, "/img/12.png", "/img/12w.png", "/img/AssM.png",
                () -> contentPanel.showAssignments());

        BackgroundPanel paymentMngrBtn = new BackgroundPanel("/img/13w.png", false);
        paymentMngrBtn.setBounds(0, (int) 649.5, 320, 144);
        paymentMngrBtn.setOpaque(false);
        addHoverEffect(paymentMngrBtn, "/img/13.png", "/img/13w.png", "/img/PayM.png",
                () -> contentPanel.showPayments());

        BackgroundPanel dormPassMngrBtn = new BackgroundPanel("/img/14w.png", false);
        dormPassMngrBtn.setBounds(0, (int) 793.9, 320, 144);
        dormPassMngrBtn.setOpaque(false);
        addHoverEffect(dormPassMngrBtn, "/img/14.png", "/img/14w.png", "/img/DormM.png",
                () -> contentPanel.showDormPass());

        BackgroundPanel dashboardBtn = new BackgroundPanel("/img/15w.png", false); // Dashboard
        dashboardBtn.setBounds(0, (int) 936.1, 320, 144);
        dashboardBtn.setOpaque(false);
        addHoverEffect(dashboardBtn, "/img/15.png", "/img/15w.png", "/img/MngDshB.png",
                () -> contentPanel.showDashboard());

        mgr.add(contentPanel);
        mgr.add(dashboardBtn);
        mgr.add(dormPassMngrBtn);
        mgr.add(paymentMngrBtn);
        mgr.add(assignmentMngrBtn);
        mgr.add(roomMngrBtn);
        mgr.add(residentMngrBtn); // residentMngrBtn on top
        add(mgr);
    }

    private void addHoverEffect(BackgroundPanel panel, String hoverImg, String defaultImg, String bgImg,
            Runnable onClick) {
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                panel.setBackground(hoverImg);
                panel.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                panel.setBackground(defaultImg);
                panel.setOpaque(false);
                panel.repaint();
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mgr.setBackground(bgImg);
                mgr.repaint();
                onClick.run();
            }
        });
    }
}
