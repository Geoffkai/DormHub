package com.dormhub.controller;

import javax.swing.*;
import java.awt.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.dormhub.view.PanelsHandler;
import com.dormhub.service.ResidentService;
import com.dormhub.service.RoomService;
import com.dormhub.service.RoomAssignmentService;
import com.dormhub.service.PaymentService;
import com.dormhub.service.DormPassService;
import com.dormhub.service.Impl.ResidentServiceImpl;
import com.dormhub.service.Impl.RoomServiceImpl;
import com.dormhub.service.Impl.RoomAssignmentServiceImpl;
import com.dormhub.service.Impl.PaymentServiceImpl;
import com.dormhub.service.Impl.DormPassServiceImpl;

public class LoginController {

    private final JFrame frame;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    public LoginController(JFrame frame, JTextField usernameField,
            JPasswordField passwordField, JLabel errorLabel) {
        this.frame = frame;
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.errorLabel = errorLabel;
    }

    private String[] loadCredentials() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input != null) props.load(input);
        } catch (IOException e) {
            return new String[]{"admin", "admin123"};
        }
        return new String[]{
            props.getProperty("app.username", "admin"),
            props.getProperty("app.password", "admin123")
        };
    }

    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Empty field checks
        if (username.isEmpty() && password.isEmpty()) {
            showError("Please enter your username and password.");
            return;
        }
        if (username.isEmpty()) {
            showError("Username cannot be empty.");
            return;
        }
        if (password.isEmpty()) {
            showError("Password cannot be empty.");
            return;
        }

        // Credential check
        String[] creds = loadCredentials();
        if (username.equals(creds[0]) && password.equals(creds[1])) {
            goToDashboard();
        } else {
            showError("Invalid credentials. Try again.");
            passwordField.setText(""); // clear password on failed attempt
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void goToDashboard() {
        errorLabel.setVisible(false);

        // Initialize all services
        ResidentService residentService = new ResidentServiceImpl();
        RoomService roomService = new RoomServiceImpl();
        RoomAssignmentService roomAssignmentService = new RoomAssignmentServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl();
        DormPassService dormPassService = new DormPassServiceImpl();

        // Build dashboard
        PanelsHandler panelsHandler = new PanelsHandler();

        new GUIController(
                residentService,
                roomService,
                roomAssignmentService,
                paymentService,
                dormPassService,
                panelsHandler,
                panelsHandler.getContentPanel());

        frame.setContentPane(panelsHandler);
        frame.validate();
        frame.repaint();
    }
}