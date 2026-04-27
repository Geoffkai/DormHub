package com.dormhub.view;

import javax.swing.*;

import com.dormhub.controller.*;
import com.dormhub.service.Impl.*;

import java.awt.*;

public class GUIMain {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        JFrame frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);

        BackgroundPanel loginPage = new BackgroundPanel("/img/Login.png");
        loginPage.setBounds(0, 0, screenSize.width, screenSize.height);
        loginPage.setLayout(null);
        loginPage.setOpaque(false);

        // Exit Button
        ImageIcon exitIcon = ImageResources.loadIcon("/img/Exit.png");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(1812, (int) 71.1, 62, 62);
        exitButton.addActionListener(e -> System.exit(0));
        loginPage.add(exitButton);

        // Login Button
        ImageIcon loginIcon = ImageResources.loadIcon("/img/LoginBtn.png");
        JButton loginButton = new JButton(loginIcon);
        loginButton.setFocusPainted(false);
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(31, 59, 44, 255));
        loginButton.setBounds((int) 1321.9, (int) 785.9, 284, (int) 64.9);
        loginPage.add(loginButton);

        // TextField
        JTextField usernameField = new JTextField();
        usernameField.setBounds((int) 1203.9, (int) 452.8, (int) 558.4, (int) 60.5);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameField.setBorder(BorderFactory.createEmptyBorder());
        usernameField.setOpaque(false);
        loginPage.add(usernameField);

        // PasswordField
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds((int) 1203.9, 613, (int) 558.4, (int) 60.5);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setBorder(BorderFactory.createEmptyBorder());
        passwordField.setOpaque(false);
        loginPage.add(passwordField);

        // Authentication process here
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword()); // dont use getText() for passwords
        // Temporary, add logic later
        loginButton.addActionListener(e -> {
            PanelsHandler panelsHandler = new PanelsHandler();

            ContentPanel contentPanel = panelsHandler.getContentPanel();

            GUIController guiController = new GUIController(new ResidentServiceImpl(), new RoomServiceImpl(),
                    new RoomAssignmentServiceImpl(), new PaymentServiceImpl(), new DormPassServiceImpl(), panelsHandler,
                    contentPanel);
            frame.setContentPane(panelsHandler);
            frame.validate();
            frame.repaint();
        });

        // Error Login Text
        JLabel Error = new JLabel("Invalid credentials. Try again.");
        Error.setBounds((int) 1145.5, (int) 691.7, (int) 470.1, (int) 38.6);
        Error.setFont(new Font("Arial", Font.BOLD, 16));
        Error.setForeground(Color.WHITE);
        Error.setVisible(false);
        loginPage.add(Error);

        // Authentication process here
        LoginController loginController = new LoginController(frame, usernameField, passwordField, Error);
        loginButton.addActionListener(e -> loginController.handleLogin());

        frame.setContentPane(loginPage);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
