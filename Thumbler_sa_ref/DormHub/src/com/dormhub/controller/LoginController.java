package com.dormhub.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.dormhub.auth.AuthService;
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
import com.dormhub.util.DatabaseConfigLoader;
import com.dormhub.view.PanelsHandler;

public class LoginController {

    private final JFrame frame;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;
    private final AuthService authService;

    public LoginController(JFrame frame, JTextField usernameField,
            JPasswordField passwordField, JLabel errorLabel) {
        this.frame = frame;
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.errorLabel = errorLabel;
        this.authService = new AuthService();

        if (!DatabaseConfigLoader.hasStoredAppCredentials()) {
            runFirstTimeSetup();
        }
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

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

        if (authService.authenticate(username, password)) {
            openDashboard(frame);
        } else {
            showError("Invalid credentials. Please try again.");
            passwordField.setText("");
        }
    }

    // -------------------------------------------------------------------------
    // First-run setup wizard
    // -------------------------------------------------------------------------

    private void runFirstTimeSetup() {
        JOptionPane.showMessageDialog(
                frame,
                "Welcome to DormHub!\n\n"
                        + "This appears to be your first time running the application.\n"
                        + "You will be guided through a one-time setup:\n\n"
                        + "  Step 1 — Connect to your MySQL database\n"
                        + "  Step 2 — Optionally populate the database with sample data\n"
                        + "  Step 3 — Create an admin login\n",
                "First-Time Setup",
                JOptionPane.INFORMATION_MESSAGE);

        // Step 1: MySQL credentials
        String[] dbCreds = promptDatabaseCredentials();
        if (dbCreds == null) {
            exitApplication("Setup was cancelled. The application cannot start without database credentials.");
            return;
        }
        String dbUrl      = dbCreds[0];
        String dbUser     = dbCreds[1];
        String dbPassword = dbCreds[2];

        // Always create the schema (tables only, no data)
        try {
            runSqlScript(dbUrl, dbUser, dbPassword, "com/dormhub/db/dormhub_schema.sql");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to create database schema:\n" + e.getMessage(),
                    "Schema Error", JOptionPane.ERROR_MESSAGE);
            exitApplication("Cannot continue without the database schema.");
            return;
        }

        // Step 2: Ask about sample data — only inserts run here, never the schema again
        promptPopulateDatabase(dbUrl, dbUser, dbPassword);

        // Step 3: Admin credentials
        String[] appCreds = promptAdminCredentials();
        if (appCreds == null) {
            exitApplication("Setup was cancelled. The application cannot start without admin credentials.");
            return;
        }

        // Save everything to app.env
        try {
            DatabaseConfigLoader.saveAll(dbUrl, dbUser, dbPassword, appCreds[0], appCreds[1]);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame,
                    "Setup completed but credentials could not be saved:\n" + e.getMessage()
                            + "\n\nYou will need to repeat setup on the next launch.",
                    "Save Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(frame,
                "Setup complete!\nYou can now log in with your new admin credentials.",
                "Setup Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // -------------------------------------------------------------------------
    // Step 1 — MySQL credentials
    // -------------------------------------------------------------------------

    private String[] promptDatabaseCredentials() {
        String dbUrl = DatabaseConfigLoader.getDatabaseUrl();

        while (true) {
            String dbUser = JOptionPane.showInputDialog(
                    frame,
                    "Enter MySQL Username:",
                    "Database Setup  (Step 1 of 3)",
                    JOptionPane.QUESTION_MESSAGE);
            if (dbUser == null) return null;

            dbUser = dbUser.trim();
            if (dbUser.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "MySQL username cannot be empty. Please try again.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            JPasswordField pwField = new JPasswordField(20);
            int pwResult = JOptionPane.showConfirmDialog(
                    frame,
                    new Object[] { "Enter MySQL Password:", pwField },
                    "Database Setup  (Step 1 of 3)",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (pwResult != JOptionPane.OK_OPTION) return null;

            String dbPassword = new String(pwField.getPassword());

            if (DatabaseConfigLoader.canConnect(dbUrl, dbUser, dbPassword)) {
                JOptionPane.showMessageDialog(frame,
                        "Database connected successfully!\n"
                                + "The 'dormhub' database has been created if it did not already exist.",
                        "Connection Successful", JOptionPane.INFORMATION_MESSAGE);
                return new String[] { dbUrl, dbUser, dbPassword };
            }

            JOptionPane.showMessageDialog(frame,
                    "Could not connect to MySQL with the provided credentials.\n"
                            + "Please check your username and password and try again.",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Step 2 — Populate sample data (inserts only)
    // -------------------------------------------------------------------------

    private void promptPopulateDatabase(String dbUrl, String dbUser, String dbPassword) {
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Would you like to populate the database with sample data?\n\n"
                        + "  Yes — load sample residents, rooms, payments, and dorm passes\n"
                        + "  No  — start with a blank (empty) database\n",
                "Sample Data  (Step 2 of 3)",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] { "Yes", "No" },
                "No");

        if (choice != JOptionPane.YES_OPTION) {
            // User said No — schema already created above, nothing else to do.
            return;
        }

        // Run the data-only script (INSERTs only, no CREATE TABLE)
        try {
            runSqlScript(dbUrl, dbUser, dbPassword, "com/dormhub/db/dormhub_data.sql");
            JOptionPane.showMessageDialog(frame,
                    "Sample data loaded successfully!",
                    "Data Populated", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to load sample data:\n" + e.getMessage()
                            + "\n\nThe database schema is still intact; you can add data manually.",
                    "Population Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // SQL script runner
    // -------------------------------------------------------------------------

    /**
     * Loads a SQL file from the classpath and executes it statement by statement.
     * @param classpathResource e.g. "com/dormhub/db/dormhub_schema.sql"
     */
    private void runSqlScript(String dbUrl, String dbUser, String dbPassword,
            String classpathResource) throws SQLException, IOException {

        InputStream sqlStream = getClass().getClassLoader().getResourceAsStream(classpathResource);
        if (sqlStream == null) {
            throw new IOException("SQL file not found in classpath: " + classpathResource);
        }

        String sql;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(sqlStream, StandardCharsets.UTF_8))) {
            sql = reader.lines().collect(Collectors.joining("\n"));
        }

        String[] statements = sql.split(";");

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Step 3 — Admin credentials
    // -------------------------------------------------------------------------

    private String[] promptAdminCredentials() {
        while (true) {
            String appUsername = JOptionPane.showInputDialog(
                    frame,
                    "Choose an admin username:",
                    "Admin Setup  (Step 3 of 3)",
                    JOptionPane.QUESTION_MESSAGE);
            if (appUsername == null) return null;

            appUsername = appUsername.trim();
            if (appUsername.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Username cannot be empty. Please try again.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            JPasswordField pwField      = new JPasswordField(20);
            JPasswordField confirmField = new JPasswordField(20);

            int pwResult = JOptionPane.showConfirmDialog(
                    frame,
                    new Object[] {
                            "Choose a password:",  pwField,
                            "Re-enter password:",  confirmField
                    },
                    "Admin Setup  (Step 3 of 3)",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (pwResult != JOptionPane.OK_OPTION) return null;

            String appPassword        = new String(pwField.getPassword());
            String appPasswordConfirm = new String(confirmField.getPassword());

            if (appPassword.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Password cannot be empty. Please try again.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!appPassword.equals(appPasswordConfirm)) {
                JOptionPane.showMessageDialog(frame,
                        "Passwords do not match. Please try again.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            return new String[] { appUsername, appPassword };
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void exitApplication(String message) {
        JOptionPane.showMessageDialog(frame, message, "Setup Cancelled", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    // -------------------------------------------------------------------------
    // Dashboard
    // -------------------------------------------------------------------------

    public static void openDashboard(JFrame frame) {
        ResidentService residentService             = new ResidentServiceImpl();
        RoomService roomService                     = new RoomServiceImpl();
        RoomAssignmentService roomAssignmentService = new RoomAssignmentServiceImpl();
        PaymentService paymentService               = new PaymentServiceImpl();
        DormPassService dormPassService             = new DormPassServiceImpl();

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