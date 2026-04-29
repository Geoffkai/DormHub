package com.dormhub.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class DBUtil {
    private static final Path ENV_FILE = Paths.get("app.env");
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/dormhub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC";
    private static final String SCHEMA_RESOURCE = "/com/dormhub/db/dormhub.sql";
    private static final Object INIT_LOCK = new Object();

    private static volatile boolean schemaInitialized;

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        DatabaseConfig config = loadConfig();
        Connection connection = DriverManager.getConnection(config.url(), config.user(), config.password());
        initializeSchema(connection);
        return connection;
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

    public static boolean hasStoredCredentials() {
        DatabaseConfig config = loadDatabaseConfig();
        return !config.user().isBlank() && !config.password().isBlank();
    }

    public static boolean canConnectStoredCredentials() {
        DatabaseConfig config = loadDatabaseConfig();
        return canConnect(config.url(), config.user(), config.password());
    }

    public static boolean canConnect(String url, String user, String password) {
        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getDatabaseUrl() {
        return loadDatabaseConfig().url();
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

    private static void initializeSchema(Connection connection) throws SQLException {
        if (schemaInitialized) {
            return;
        }

        synchronized (INIT_LOCK) {
            if (schemaInitialized) {
                return;
            }

            // If the core tables already exist, skip schema import to avoid
            // "Table already exists" errors and duplicate seed data.
            if (hasCoreTables(connection)) {
                schemaInitialized = true;
                return;
            }

            String schemaSql = loadSchemaScript();
            try (Statement statement = connection.createStatement()) {
                for (String sql : splitStatements(schemaSql)) {
                    statement.execute(sql);
                }
            }

            schemaInitialized = true;
        }
    }

    private static boolean hasCoreTables(Connection connection) throws SQLException {
        return tableExists(connection, "room")
                && tableExists(connection, "resident")
                && tableExists(connection, "room_assignments")
                && tableExists(connection, "payment")
                && tableExists(connection, "dorm_pass");
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        String catalog = connection.getCatalog();

        try (ResultSet result = metadata.getTables(catalog, null, tableName, new String[] { "TABLE" })) {
            return result.next();
        }
    }

    private static DatabaseConfig loadConfig() {
        return loadDatabaseConfig();
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

    private static String loadSchemaScript() throws SQLException {
        try (InputStream input = DBUtil.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (input != null) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new SQLException("Unable to read dormhub schema script.", e);
        }

        Path sourcePath = Paths.get("src", "main", "java", "com", "dormhub", "db", "dormhub.sql");
        if (!Files.exists(sourcePath)) {
            sourcePath = Paths.get("src", "main", "resources", "com", "dormhub", "db", "dormhub.sql");
        }

        try {
            return Files.readString(sourcePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Unable to load dormhub schema script.", e);
        }
    }

    private static String[] splitStatements(String script) {
        java.util.List<String> statements = new java.util.ArrayList<>();
        StringBuilder statement = new StringBuilder();
        boolean inSingleQuote = false;

        for (String line : script.replace("\r", "").split("\n")) {
            String trimmed = stripLineComment(line).trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            for (int i = 0; i < trimmed.length(); i++) {
                char current = trimmed.charAt(i);
                if (current == '\'' && (i == 0 || trimmed.charAt(i - 1) != '\\')) {
                    inSingleQuote = !inSingleQuote;
                }

                if (current == ';' && !inSingleQuote) {
                    statement.append(trimmed, 0, i);
                    String sql = statement.toString().trim();
                    if (!sql.isEmpty()) {
                        statements.add(sql);
                    }
                    statement.setLength(0);
                    trimmed = trimmed.substring(i + 1).trim();
                    i = -1;
                }
            }

            if (!trimmed.isEmpty()) {
                statement.append(trimmed).append(' ');
            }
        }

        String lastStatement = statement.toString().trim();
        if (lastStatement.isEmpty()) {
            return statements.toArray(String[]::new);
        }
        statements.add(lastStatement);
        return statements.toArray(String[]::new);
    }

    public static final class DatabaseConfig {
        private final String url;
        private final String user;
        private final String password;

        public DatabaseConfig(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
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

    private static String stripLineComment(String line) {
        boolean inSingleQuote = false;
        for (int i = 0; i < line.length() - 1; i++) {
            char current = line.charAt(i);
            if (current == '\'' && (i == 0 || line.charAt(i - 1) != '\\')) {
                inSingleQuote = !inSingleQuote;
            }
            if (!inSingleQuote && current == '-' && line.charAt(i + 1) == '-') {
                return line.substring(0, i);
            }
            if (!inSingleQuote && current == '#') {
                return line.substring(0, i);
            }
        }
        return line;
    }

}
