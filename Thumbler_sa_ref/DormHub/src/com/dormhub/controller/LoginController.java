package com.dormhub.controller;

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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
    // Step 1 — MySQL credentials (single custom dialog)
    // -------------------------------------------------------------------------

    private String[] promptDatabaseCredentials() {
        final String dbUrl = DatabaseConfigLoader.getDatabaseUrl();
        final String[] result = { null, null }; // [user, password]

        JDialog dialog = new JDialog(frame, "Database Setup  (Step 1 of 3)", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);

        // ── Content panel ──────────────────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Connect to MySQL Database");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setAlignmentX(0f);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(4));

        JLabel subLabel = new JLabel("Enter your MySQL credentials to continue setup.");
        subLabel.setForeground(new Color(90, 90, 90));
        subLabel.setAlignmentX(0f);
        content.add(subLabel);
        content.add(Box.createVerticalStrut(18));

        // ── Form grid ──────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setAlignmentX(0f);

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(4, 0, 4, 10);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(4, 0, 4, 0);

        lc.gridx = 0; lc.gridy = 0; form.add(new JLabel("Username:"), lc);
        JTextField userField = new JTextField(22);
        fc.gridx = 1; fc.gridy = 0; form.add(userField, fc);

        lc.gridx = 0; lc.gridy = 1; form.add(new JLabel("Password:"), lc);
        JPasswordField pwField = new JPasswordField(22);
        fc.gridx = 1; fc.gridy = 1; form.add(pwField, fc);

        content.add(form);
        content.add(Box.createVerticalStrut(12));

        // ── Status label ───────────────────────────────────────────────────
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
        statusLabel.setAlignmentX(0f);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        content.add(statusLabel);
        content.add(Box.createVerticalStrut(10));

        // ── Button row ─────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(0f);

        JButton cancelBtn  = new JButton("Cancel");
        JButton testBtn    = new JButton("Test Connection");
        JButton proceedBtn = new JButton("Proceed");
        proceedBtn.setEnabled(false);

        btnRow.add(cancelBtn);
        btnRow.add(testBtn);
        btnRow.add(proceedBtn);
        content.add(btnRow);

        dialog.setContentPane(content);

        // ── Listeners ──────────────────────────────────────────────────────
        final boolean[] connectionVerified = { false };

        // Reset verified state whenever the user edits credentials
        DocumentListener resetListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { reset(); }
            public void removeUpdate(DocumentEvent e)  { reset(); }
            public void changedUpdate(DocumentEvent e) { reset(); }
            private void reset() {
                connectionVerified[0] = false;
                proceedBtn.setEnabled(false);
                statusLabel.setText(" ");
            }
        };
        userField.getDocument().addDocumentListener(resetListener);
        pwField.getDocument().addDocumentListener(resetListener);

        testBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(pwField.getPassword());

            if (user.isBlank()) {
                statusLabel.setText("Username cannot be empty.");
                statusLabel.setForeground(new Color(200, 30, 30));
                return;
            }

            testBtn.setEnabled(false);
            testBtn.setText("Testing\u2026");
            statusLabel.setText("Connecting\u2026");
            statusLabel.setForeground(new Color(90, 90, 90));

            // Run off the EDT so the UI stays responsive
            new Thread(() -> {
                boolean ok = DatabaseConfigLoader.canConnect(dbUrl, user, pass);
                SwingUtilities.invokeLater(() -> {
                    testBtn.setText("Test Connection");
                    testBtn.setEnabled(true);
                    if (ok) {
                        connectionVerified[0] = true;
                        statusLabel.setText("MySQL database connection successful.");
                        statusLabel.setForeground(new Color(30, 140, 30));
                        proceedBtn.setEnabled(true);
                        proceedBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        connectionVerified[0] = false;
                        statusLabel.setText("Could not connect to MySQL. Please re-enter credentials.");
                        statusLabel.setForeground(new Color(200, 30, 30));
                        proceedBtn.setEnabled(false);
                    }
                });
            }, "db-test-thread").start();
        });

        proceedBtn.addActionListener(e -> {
            if (connectionVerified[0]) {
                result[0] = userField.getText().trim();
                result[1] = new String(pwField.getPassword());
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        // Allow Enter in the password field to trigger Test Connection
        pwField.addActionListener(e -> testBtn.doClick());

        dialog.pack();
        dialog.setMinimumSize(new Dimension(430, dialog.getHeight()));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true); // blocks until disposed

        if (result[0] == null) return null;
        return new String[] { dbUrl, result[0], result[1] };
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
    // Step 3 — Admin credentials (single custom dialog)
    // -------------------------------------------------------------------------

    private String[] promptAdminCredentials() {
        final String[] result = { null, null };

        JDialog dialog = new JDialog(frame, "Admin Setup  (Step 3 of 3)", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Create Admin Account");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setAlignmentX(0f);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(4));

        JLabel subLabel = new JLabel("Choose a username and password to log in with.");
        subLabel.setForeground(new Color(90, 90, 90));
        subLabel.setAlignmentX(0f);
        content.add(subLabel);
        content.add(Box.createVerticalStrut(18));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setAlignmentX(0f);

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(4, 0, 4, 10);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(4, 0, 4, 0);

        lc.gridx = 0; lc.gridy = 0; form.add(new JLabel("Username:"), lc);
        JTextField userField = new JTextField(22);
        fc.gridx = 1; fc.gridy = 0; form.add(userField, fc);

        lc.gridx = 0; lc.gridy = 1; form.add(new JLabel("Password:"), lc);
        JPasswordField pwField = new JPasswordField(22);
        fc.gridx = 1; fc.gridy = 1; form.add(pwField, fc);

        lc.gridx = 0; lc.gridy = 2; form.add(new JLabel("Confirm password:"), lc);
        JPasswordField confirmField = new JPasswordField(22);
        fc.gridx = 1; fc.gridy = 2; form.add(confirmField, fc);

        content.add(form);
        content.add(Box.createVerticalStrut(10));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
        statusLabel.setForeground(new Color(200, 30, 30));
        statusLabel.setAlignmentX(0f);
        content.add(statusLabel);
        content.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(0f);

        JButton cancelBtn = new JButton("Cancel");
        JButton okBtn     = new JButton("Create Account");
        btnRow.add(cancelBtn);
        btnRow.add(okBtn);
        content.add(btnRow);

        dialog.setContentPane(content);

        okBtn.addActionListener(e -> {
            String user    = userField.getText().trim();
            String pass    = new String(pwField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (user.isBlank()) {
                statusLabel.setText("Username cannot be empty.");
                return;
            }
            if (pass.isBlank()) {
                statusLabel.setText("Password cannot be empty.");
                return;
            }
            if (!pass.equals(confirm)) {
                statusLabel.setText("Passwords do not match. Please try again.");
                pwField.setText("");
                confirmField.setText("");
                return;
            }
            result[0] = user;
            result[1] = pass;
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        // Allow Enter on confirm field to submit
        confirmField.addActionListener(e -> okBtn.doClick());

        dialog.pack();
        dialog.setMinimumSize(new Dimension(400, dialog.getHeight()));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

        if (result[0] == null) return null;
        return result;
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