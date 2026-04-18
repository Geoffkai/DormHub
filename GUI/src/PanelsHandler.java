import javax.swing.*;
import java.awt.*;

public class PanelsHandler extends JPanel {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Main background, manager dashboard
    BackgroundPanel mgr = new BackgroundPanel("src/img/MngDshB.png");
    // Content changes on right side
    ContentPanel contentPanel;

    public PanelsHandler() {
        setLayout(null);
        setBounds(0, 0, screenSize.width, screenSize.height);

        mgr.setLayout(null);
        mgr.setBounds(0, 0, screenSize.width, screenSize.height);

        contentPanel = new ContentPanel(screenSize);

        // Side panels
        BackgroundPanel p1 = new BackgroundPanel("src/img/10w.png", false);
        p1.setBounds(0, (int) 217.2, 320, 144);
        p1.setOpaque(false);
        addHoverEffect(p1, "src/img/10.png", "src/img/10w.png", "src/img/ResM.png", () -> contentPanel.showResidents());

        BackgroundPanel p2 = new BackgroundPanel("src/img/11w.png", false);
        p2.setBounds(0, (int) 361.3, 320, 144);
        p2.setOpaque(false);
        addHoverEffect(p2, "src/img/11.png", "src/img/11w.png", "src/img/RoomM.png", () -> contentPanel.showRooms());

        BackgroundPanel p3 = new BackgroundPanel("src/img/12w.png", false);
        p3.setBounds(0, (int) 505.4, 320, 144);
        p3.setOpaque(false);
        addHoverEffect(p3, "src/img/12.png", "src/img/12w.png", "src/img/AssM.png", () -> contentPanel.showAssignments());

        BackgroundPanel p4 = new BackgroundPanel("src/img/13w.png", false);
        p4.setBounds(0, (int) 649.5, 320, 144);
        p4.setOpaque(false);
        addHoverEffect(p4, "src/img/13.png", "src/img/13w.png", "src/img/PayM.png", () -> contentPanel.showPayments());

        BackgroundPanel p5 = new BackgroundPanel("src/img/14w.png", false);
        p5.setBounds(0, (int) 793.9, 320, 144);
        p5.setOpaque(false);
        addHoverEffect(p5, "src/img/14.png", "src/img/14w.png", "src/img/DormM.png", () -> contentPanel.showDormPass());

        BackgroundPanel p6 = new BackgroundPanel("src/img/15w.png", false); // Dashboard
        p6.setBounds(0, (int) 936.1, 320, 144);
        p6.setOpaque(false);
        addHoverEffect(p6, "src/img/15.png", "src/img/15w.png", "src/img/MngDshB.png", () -> contentPanel.showDashboard());

        mgr.add(contentPanel);
        mgr.add(p6);
        mgr.add(p5);
        mgr.add(p4);
        mgr.add(p3);
        mgr.add(p2);
        mgr.add(p1); // p1 on top
        add(mgr);
    }

    private void addHoverEffect(BackgroundPanel panel, String hoverImg, String defaultImg, String bgImg, Runnable onClick) {
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
