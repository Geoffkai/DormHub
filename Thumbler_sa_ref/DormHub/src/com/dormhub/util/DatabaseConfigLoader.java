package com.dormhub.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public final class DatabaseConfigLoader {

    // Resolves to C:\ProgramData\DormHub\app.env on Windows.
    // ProgramData is writable by all users without admin rights.
    private static final Path ENV_DIR = resolveEnvDir();
    private static final Path ENV_FILE = ENV_DIR.resolve("app.env");

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/dormhub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";

    private DatabaseConfigLoader() {
    }

    // -------------------------------------------------------------------------
    // Resolve storage directory
    // -------------------------------------------------------------------------

    /**
     * Returns the directory where app.env is stored.
     * Priority:
     *   1. %PROGRAMDATA%\DormHub  (C:\ProgramData\DormHub on most Windows installs)
     *   2. Falls back to the user's home directory (~/.dormhub) on non-Windows or
     *      if PROGRAMDATA is not set, so the app still works during development.
     */
    private static Path resolveEnvDir() {
        String programData = System.getenv("PROGRAMDATA");
        if (programData != null && !programData.isBlank()) {
            return Paths.get(programData, "DormHub");
        }
        // Fallback for dev/non-Windows environments
        return Paths.get(System.getProperty("user.home"), ".dormhub");
    }

    /**
     * Ensures the storage directory exists. Called lazily before every write so
     * the directory is created automatically on first run without needing the
     * installer to pre-create it.
     */
    private static void ensureDirectoryExists() throws IOException {
        if (!Files.exists(ENV_DIR)) {
            Files.createDirectories(ENV_DIR);
        }
    }

    // -------------------------------------------------------------------------
    // Database config
    // -------------------------------------------------------------------------

    public static DatabaseConfig loadDatabaseConfig() {
        Properties properties = loadFileProperties();

        String url = firstNonBlank(
                System.getProperty("db.url"),
                System.getenv("DB_URL"),
                properties.getProperty("DB_URL"),
                properties.getProperty("db.url"),
                DEFAULT_URL);
        String user = firstNonBlank(
                System.getProperty("db.user"),
                System.getenv("DB_USER"),
                properties.getProperty("DB_USER"),
                properties.getProperty("db.user"));
        String password = firstNonBlank(
                System.getProperty("db.password"),
                System.getenv("DB_PASSWORD"),
                properties.getProperty("DB_PASSWORD"),
                properties.getProperty("db.password"));

        return new DatabaseConfig(url, user, password);
    }

    public static String getDatabaseUrl() {
        return loadDatabaseConfig().url();
    }

    public static boolean hasStoredCredentials() {
        DatabaseConfig config = loadDatabaseConfig();
        return !config.user().isBlank() && !config.password().isBlank();
    }

    public static boolean canConnect(String url, String user, String password) {
        if (url == null || url.isBlank() || user == null || user.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }
        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(url, user, password)) {
            return connection.isValid(2);
        } catch (java.sql.SQLException e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // App (admin) credentials
    // -------------------------------------------------------------------------

    /**
     * Returns true when {@code app.env} already contains a saved admin username,
     * meaning first-run setup has already been completed.
     */
    public static boolean hasStoredAppCredentials() {
        Properties properties = loadFileProperties();
        String user = firstNonBlank(
                properties.getProperty("APP_USERNAME"),
                properties.getProperty("app.username"));
        return user != null && !user.isBlank();
    }

    /**
     * Loads the admin (app-level) username and password from {@code app.env}.
     * Returns an array of two strings: [username, password].
     * Both are empty strings if not found.
     */
    public static String[] loadAppCredentials() {
        Properties properties = loadFileProperties();
        String user = firstNonBlank(
                properties.getProperty("APP_USERNAME"),
                properties.getProperty("app.username"));
        String pass = firstNonBlank(
                properties.getProperty("APP_PASSWORD"),
                properties.getProperty("app.password"));
        return new String[] {
                user == null ? "" : user,
                pass == null ? "" : pass
        };
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Saves both the MySQL credentials and the app (admin) credentials together
     * into {@code app.env}. Passing {@code null} for the app fields preserves any
     * previously saved app credentials in the file.
     */
    public static void saveDatabaseCredentials(String url, String user, String password) throws IOException {
        saveAll(url, user, password, null, null);
    }

    /**
     * Saves both DB credentials and admin credentials into {@code app.env} in one
     * atomic write so neither set is ever partially persisted.
     * File is written to C:\ProgramData\DormHub\app.env (or ~/.dormhub/app.env
     * on non-Windows systems).
     */
    public static void saveAll(String dbUrl, String dbUser, String dbPassword,
            String appUsername, String appPassword) throws IOException {

        // Keep previously stored app creds if new ones are not supplied
        if (appUsername == null || appUsername.isBlank()) {
            String[] existing = loadAppCredentials();
            appUsername = existing[0];
            appPassword = existing[1];
        }

        String effectiveUrl = (dbUrl == null || dbUrl.isBlank()) ? DEFAULT_URL : dbUrl;

        // Create C:\ProgramData\DormHub\ if it doesn't exist yet
        ensureDirectoryExists();

        try (BufferedWriter writer = Files.newBufferedWriter(
                ENV_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write("# MySQL Database Configuration\n");
            writer.write("DB_URL=" + effectiveUrl + "\n");
            writer.write("DB_USER=" + (dbUser == null ? "" : dbUser) + "\n");
            writer.write("DB_PASSWORD=" + (dbPassword == null ? "" : dbPassword) + "\n");
            writer.write("\n");
            writer.write("# Admin (App) Credentials\n");
            writer.write("APP_USERNAME=" + (appUsername == null ? "" : appUsername) + "\n");
            writer.write("APP_PASSWORD=" + (appPassword == null ? "" : appPassword) + "\n");
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static Properties loadFileProperties() {
        Properties properties = new Properties();
        if (!Files.exists(ENV_FILE)) {
            return properties;
        }
        try (BufferedReader reader = Files.newBufferedReader(ENV_FILE, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            return properties;
        }
        return properties;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    // -------------------------------------------------------------------------
    // Value object
    // -------------------------------------------------------------------------

    public static final class DatabaseConfig {
        private final String url;
        private final String user;
        private final String password;

        public DatabaseConfig(String url, String user, String password) {
            this.url = url;
            this.user = user == null ? "" : user;
            this.password = password == null ? "" : password;
        }

        public String url() { return url; }
        public String user() { return user; }
        public String password() { return password; }
    }
}