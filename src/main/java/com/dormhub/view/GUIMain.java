package com.dormhub.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.dormhub.controller.LoginController;

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

        // Login Button
        ImageIcon loginIcon = ImageResources.loadIcon("/img/LoginBtn.png");
        JButton loginButton = new JButton(loginIcon);
        loginButton.setFocusPainted(false);
        loginButton.setBounds(1322, (int) 785.9, 288, 66);
        loginPage.add(loginButton);

        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginButton.setIcon(ImageResources.loadIcon("/img/LoginBtnHover.png")); // optional hover image
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginButton.setIcon(loginIcon); // back to normal
            }
        });

        // Exit Button
        ImageIcon exitIcon = ImageResources.loadIcon("/img/Exit.png");
        JButton exitButton = new JButton(exitIcon);
        exitButton.setFocusPainted(false);
        exitButton.setBounds(1812, (int) 71.1, 62, 62);
        exitButton.addActionListener(e -> System.exit(0));
        loginPage.add(exitButton);

        // TextField
        JTextField usernameField = new JTextField();
        usernameField.setBounds((int) 1205.9, (int) 452.8, (int) 558.4, (int) 60.5);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 19));
        usernameField.setBorder(BorderFactory.createEmptyBorder());
        usernameField.setOpaque(false);
        loginPage.add(usernameField);

        // PasswordField
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds((int) 1203.9, 613, (int) 558.4, (int) 60.5);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 19));
        passwordField.setBorder(BorderFactory.createEmptyBorder());
        passwordField.setOpaque(false);
        loginPage.add(passwordField);

        // Error Login Text
        JLabel Error = new JLabel("Invalid credentials. Try again.");
        Error.setBounds((int) 1145.5, (int) 691.7, (int) 470.1, (int) 38.6);
        Error.setFont(new Font("Arial", Font.ITALIC, 18));
        Error.setForeground(new Color(255, 222, 125));
        Error.setVisible(false);
        loginPage.add(Error);

        // Authentication process here
        LoginController loginController = new LoginController(frame, usernameField, passwordField, Error);
        loginButton.addActionListener(e -> loginController.handleLogin());
        usernameField.addActionListener(e -> loginButton.doClick());
        passwordField.addActionListener(e -> loginButton.doClick());

        frame.setContentPane(loginPage);
        frame.getRootPane().setDefaultButton(loginButton);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
