import javax.swing.*;
import java.awt.*;

public class GUIMain {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        JFrame frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);

        BackgroundPanel loginPage = new BackgroundPanel("src/img/Login.png");
        loginPage.setBounds(0, 0, screenSize.width, screenSize.height);
        loginPage.setLayout(null);
        loginPage.setOpaque(false);

        // Exit Button
        ImageIcon exitIcon = new ImageIcon("src/img/Exit.png");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(1770, 59, 83, 83);
        exitButton.addActionListener(e -> System.exit(0));
        loginPage.add(exitButton);

        // Login Button
        ImageIcon loginIcon = new ImageIcon("src/img/LoginBtn.png");
        JButton loginButton = new JButton(loginIcon);
        loginButton.setFocusPainted(false);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(31, 59, 44, 255));
        loginButton.setBounds(1286, 809, 324, 74);
        loginPage.add(loginButton);

        // TextField
        JTextField usernameField = new JTextField();
        usernameField.setBounds((int) 1151.4, 429, 637, 69);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setBorder(BorderFactory.createEmptyBorder());
        usernameField.setOpaque(false);
        loginPage.add(usernameField);

        // PasswordField
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds((int) 1151.4, (int) 611.8, 637, 69);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBorder(BorderFactory.createEmptyBorder());
        passwordField.setOpaque(false);
        loginPage.add(passwordField);

        // Authentication process here
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword()); // dont use getText() for passwords
        // Temporary, add logic later
        loginButton.addActionListener(e -> {
            frame.setContentPane(new PanelsHandler());
            frame.validate();
            frame.repaint();
        });

        // Error Login Text
        JLabel Error = new JLabel("Invalid credentials. Try again.");
        Error.setBounds((int) 1084.7, 701, 537, 44);
        Error.setFont(new Font("Arial", Font.BOLD, 19));
        Error.setForeground(Color.WHITE);
        Error.setVisible(false);
        loginPage.add(Error);

        frame.setContentPane(loginPage);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
