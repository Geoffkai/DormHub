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
    private static final Path ENV_FILE = Paths.get("app.env");
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/dormhub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";

    private DatabaseConfigLoader() {
    }

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

    public static void saveDatabaseCredentials(String url, String user, String password) throws IOException {
        String effectiveUrl = (url == null || url.isBlank()) ? DEFAULT_URL : url;

        try (BufferedWriter writer = Files.newBufferedWriter(
                ENV_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            writer.write("# MySQL Database Configuration\n");
            writer.write("DB_URL=" + effectiveUrl + "\n");
            writer.write("DB_USER=" + (user == null ? "" : user) + "\n");
            writer.write("DB_PASSWORD=" + (password == null ? "" : password) + "\n");
        }
    }

    public static boolean hasStoredCredentials() {
        DatabaseConfig config = loadDatabaseConfig();
        return !config.user().isBlank() && !config.password().isBlank();
    }

    public static boolean canConnect(String url, String user, String password) {
        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        try (java.sql.Connection connection = java.sql.DriverManager.getConnection(url, user, password)) {
            return connection.isValid(2);
        } catch (java.sql.SQLException e) {
            return false;
        }
    }

    public static String getDatabaseUrl() {
        return loadDatabaseConfig().url();
    }

    public static final class DatabaseConfig {
        private final String url;
        private final String user;
        private final String password;

        public DatabaseConfig(String url, String user, String password) {
            this.url = url;
            this.user = user == null ? "" : user;
            this.password = password == null ? "" : password;
        }

        public String url() {
            return url;
        }

        public String user() {
            return user;
        }

        public String password() {
            return password;
        }
    }
}
