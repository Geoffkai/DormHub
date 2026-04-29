package com.dormhub.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.dormhub.service.DormPassService;
import com.dormhub.service.Impl.DormPassServiceImpl;
import com.dormhub.service.Impl.PaymentServiceImpl;
import com.dormhub.service.Impl.ResidentServiceImpl;
import com.dormhub.service.Impl.RoomAssignmentServiceImpl;
import com.dormhub.service.Impl.RoomServiceImpl;
import com.dormhub.service.PaymentService;
import com.dormhub.service.ResidentService;
import com.dormhub.service.RoomAssignmentService;
import com.dormhub.service.RoomService;
import com.dormhub.util.DBUtil;
import com.dormhub.view.PanelsHandler;

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

    private String[] loadAppCredentials() {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            return new String[] { "admin", "admin123" };
        }

        return new String[] {
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

        String[] appCredentials = loadAppCredentials();

        if (username.equals(appCredentials[0]) && password.equals(appCredentials[1])) {
            if (ensureDatabaseCredentials()) {
                openDashboard(frame);
            }
        } else {
            showError("Invalid app login credentials. Try again.");
            passwordField.setText(""); // clear password on failed attempt
        }
    }

    private boolean ensureDatabaseCredentials() {
        if (DBUtil.canConnectStoredCredentials()) {
            return true;
        }

        while (true) {
            String databaseUrl = DBUtil.getDatabaseUrl();
            String databaseUser = JOptionPane.showInputDialog(frame, "Enter MySQL Username:", "Database Setup",
                    JOptionPane.QUESTION_MESSAGE);
            if (databaseUser == null) {
                showError("Database setup was cancelled.");
                return false;
            }

            JPasswordField databasePasswordField = new JPasswordField();
            int dialogResult = JOptionPane.showConfirmDialog(
                    frame,
                    databasePasswordField,
                    "Enter MySQL Password",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (dialogResult != JOptionPane.OK_OPTION) {
                showError("Database setup was cancelled.");
                return false;
            }

            String databasePassword = new String(databasePasswordField.getPassword());

            if (DBUtil.canConnect(databaseUrl, databaseUser.trim(), databasePassword)) {
                try {
                    DBUtil.saveDatabaseCredentials(databaseUrl, databaseUser.trim(), databasePassword);
                } catch (IOException e) {
                    showError("Database login succeeded, but the credentials could not be saved.");
                    return false;
                }
                return true;
            }

            JOptionPane.showMessageDialog(
                    frame,
                    "Invalid MySQL credentials. Please try again.",
                    "Database Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public static void openDashboard(JFrame frame) {
        // Initialize all services
        ResidentService residentService = new ResidentServiceImpl();
        RoomService roomService = new RoomServiceImpl();
        RoomAssignmentService roomAssignmentService = new RoomAssignmentServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl();
        DormPassService dormPassService = new DormPassServiceImpl();

        // Build dashboard
        PanelsHandler panelsHandler = new PanelsHandler();

        GUIController controller = new GUIController(
                residentService,
                roomService,
                roomAssignmentService,
                paymentService,
                dormPassService,
                panelsHandler,
                panelsHandler.getContentPanel());
        controller.getClass();

        frame.setContentPane(panelsHandler);
        frame.validate();
        frame.repaint();
    }
}