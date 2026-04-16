import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {

    JButton ViewBtn, AddBtn, UpdateBtn, DeleteBtn, ExportBtn;

    // Dashboard panels
    JPanel TR = new JPanel();
    JPanel PDPR = new JPanel();
    JPanel RA = new JPanel();
    JPanel RO = new JPanel();

    // Search
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JTextField searchField;
    private JComboBox<String> attributeComboBox;

    public ContentPanel(Dimension screenSize) {
        setLayout(null);
        setBounds(0, 0, screenSize.width, screenSize.height);
        setOpaque(false);

        // Action buttons
        ViewBtn = makeButton("src/img/View all.png", (int) 375.1, (int) 120.1, 238, 60);
        AddBtn = makeButton("src/img/Add.png", (int) 688.3, (int) 120.1, 238, 60);
        UpdateBtn = makeButton("src/img/Update.png", (int) 1001.3, (int) 120.1, 238, 60);
        DeleteBtn = makeButton("src/img/Delete.png", (int) 1314.1, (int) 120.1, 238, 60);
        ExportBtn = makeButton("src/img/Export.png", (int) 1626.8, (int) 120.1, 238, 60);

        // Search bar
        searchField = new JTextField(23);
        searchField.setPreferredSize(new Dimension(356, 31));
        searchField.setFont(new Font("Arial", Font.PLAIN, 20));

        attributeComboBox = new JComboBox<>(new String[]{""});
        attributeComboBox.setPreferredSize(new Dimension(205, 31));
        attributeComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        attributeComboBox.setBackground(Color.WHITE);

        // Search Button
        JButton searchBtn = new JButton("Search");
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
        searchPanel.setVisible(false);


        // Dashboard panels
        TR.setBounds((int) 444.8, (int) 217.2, 534, 200);
        TR.setBackground(Color.WHITE);
        PDPR.setBounds((int) 1218.1, (int) 217.2, 534, 200);
        PDPR.setBackground(Color.WHITE);
        RA.setBounds((int) 1126.2, (int) 652.8, 649, 341);
        RA.setBackground(Color.WHITE);
        RO.setBounds((int) 412.5, (int) 639.3, 598, 368);
        RO.setBackground(Color.WHITE);

        add(searchPanel);
        add(TR); add(PDPR); add(RA); add(RO);
        add(ViewBtn); add(AddBtn); add(UpdateBtn); add(DeleteBtn); add(ExportBtn);

        showDashboard(); // initial state
    }

    private JButton makeButton(String path, int x, int y, int w, int h) {
        JButton btn = new JButton(new ImageIcon(path));
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
    }

    public void showManager() {
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
    }
}