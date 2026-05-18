package com.dormhub.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBUtil {
    private static final String SCHEMA_RESOURCE = "/com/dormhub/db/dormhub_schema.sql";
    private static final String FAT_JAR_SCHEMA_RESOURCE = "/resources/db/dormhub_schema.sql";
    private static final Object INIT_LOCK = new Object();

    private static volatile boolean schemaInitialized;

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        DatabaseConfigLoader.DatabaseConfig config = DatabaseConfigLoader.loadDatabaseConfig();
        Connection connection;

        try {
            // Try to connect to the dormhub database directly
            connection = java.sql.DriverManager.getConnection(config.url(), config.user(), config.password());
        } catch (SQLException e) {
            // Database doesn't exist yet, create it and the schema
            String baseUrl = extractBaseUrlWithoutDatabase(config.url());
            try {
                Connection baseConnection = java.sql.DriverManager.getConnection(baseUrl, config.user(),
                        config.password());

                // Read and execute the SQL script to create database and tables
                String schemaSql = readResourceSchema(SCHEMA_RESOURCE);
                if (schemaSql != null) {
                    try (Statement statement = baseConnection.createStatement()) {
                        for (String sql : splitStatements(schemaSql)) {
                            if (!sql.trim().isEmpty()) {
                                statement.execute(sql);
                            }
                        }
                    }
                }
                baseConnection.close();

                // Now connect to the created database
                connection = java.sql.DriverManager.getConnection(config.url(), config.user(), config.password());
            } catch (SQLException e2) {
                throw new SQLException(
                        "Failed to create dormhub database and schema. Check your database server and credentials.",
                        e2);
            }
        }

        initializeSchema(connection);
        return connection;
    }

    private static String extractBaseUrlWithoutDatabase(String fullUrl) {
        // Extract base URL without the database name
        // From: jdbc:mysql://localhost:3306/dormhub?...
        // To: jdbc:mysql://localhost:3306/?...
        String url = fullUrl;

        int lastSlash = url.lastIndexOf('/');
        int questionMark = url.indexOf('?');

        if (lastSlash != -1) {
            if (questionMark != -1) {
                // Has query parameters
                return url.substring(0, lastSlash + 1) + "?" + url.substring(questionMark + 1);
            } else {
                // No query parameters
                return url.substring(0, lastSlash + 1);
            }
        }

        return url;
    }

    private static void initializeSchema(Connection connection) throws SQLException {
        if (schemaInitialized) {
            return;
        }

        synchronized (INIT_LOCK) {
            if (schemaInitialized) {
                return;
            }

            try {
                if (hasCoreTables(connection)) {
                    migrateLegacyRoomTypes(connection);
                    migrateLegacyDormPassTypes(connection);
                    schemaInitialized = true;
                    return;
                }
            } catch (SQLException e) {
                throw e;
            }

            String schemaSql = readResourceSchema(SCHEMA_RESOURCE);
            if (schemaSql != null) {
                try (Statement statement = connection.createStatement()) {
                    for (String sql : splitStatements(schemaSql)) {
                        statement.execute(sql);
                    }
                }
            }

            migrateLegacyRoomTypes(connection);
            migrateLegacyDormPassTypes(connection);

            schemaInitialized = true;
        }
    }

    private static void migrateLegacyDormPassTypes(Connection connection) throws SQLException {
        if (!tableExists(connection, "dorm_pass")) {
            return;
        }

        try (PreparedStatement ps = connection
                .prepareStatement("UPDATE dorm_pass SET type = 'Home Pass' WHERE LOWER(type) = 'day'")) {
            ps.executeUpdate();
        }
    }

    private static void migrateLegacyRoomTypes(Connection connection) throws SQLException {
        if (!tableExists(connection, "room")) {
            return;
        }

        // Ensure room_type can hold both "Regular" and "Transient".
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE room MODIFY COLUMN room_type VARCHAR(9) NOT NULL");
        }

        dropLegacyRoomTypeChecks(connection);

        try (PreparedStatement updateMale = connection
                .prepareStatement("UPDATE room SET room_type = 'Regular' WHERE LOWER(room_type) = 'male'")) {
            updateMale.executeUpdate();
        }

        try (PreparedStatement updateFemale = connection
                .prepareStatement("UPDATE room SET room_type = 'Transient' WHERE LOWER(room_type) = 'female'")) {
            updateFemale.executeUpdate();
        }
    }

    private static void dropLegacyRoomTypeChecks(Connection connection) throws SQLException {
        String sql = """
                SELECT tc.CONSTRAINT_NAME, cc.CHECK_CLAUSE
                FROM information_schema.TABLE_CONSTRAINTS tc
                JOIN information_schema.CHECK_CONSTRAINTS cc
                  ON tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME
                 AND tc.CONSTRAINT_SCHEMA = cc.CONSTRAINT_SCHEMA
                WHERE tc.TABLE_SCHEMA = DATABASE()
                  AND tc.TABLE_NAME = 'room'
                  AND tc.CONSTRAINT_TYPE = 'CHECK'
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                String constraintName = result.getString("CONSTRAINT_NAME");
                String checkClause = result.getString("CHECK_CLAUSE");
                if (constraintName == null || checkClause == null) {
                    continue;
                }

                String normalizedClause = checkClause.toLowerCase();
                if (normalizedClause.contains("room_type")
                        && normalizedClause.contains("male")
                        && normalizedClause.contains("female")) {
                    try (Statement dropStatement = connection.createStatement()) {
                        dropStatement.execute("ALTER TABLE room DROP CHECK `" + constraintName + "`");
                    }
                }
            }
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

    private static String readResourceSchema(String resource) {
        String[] candidatePaths = {
                resource,
                resource.startsWith("/") ? resource.substring(1) : resource,
                FAT_JAR_SCHEMA_RESOURCE,
                FAT_JAR_SCHEMA_RESOURCE.substring(1),
                "resources/db/" + fileName(resource),
                "db/" + fileName(resource),
                "src" + resource,
                "resources" + resource,
                "resources/db/" + fileName(resource)
        };

        for (String candidate : candidatePaths) {
            String schema = readSchemaCandidate(candidate);
            if (schema != null && !schema.isBlank()) {
                return schema;
            }
        }

        return null;
    }

    private static String fileName(String resource) {
        int lastSlash = resource.lastIndexOf('/');
        return lastSlash >= 0 ? resource.substring(lastSlash + 1) : resource;
    }

    private static String readSchemaCandidate(String candidate) {
        try (java.io.InputStream input = openSchemaStream(candidate)) {
            if (input == null) {
                return null;
            }
            return new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            return null;
        }
    }

    private static java.io.InputStream openSchemaStream(String candidate) throws java.io.IOException {
        java.io.InputStream classpathStream = DBUtil.class.getResourceAsStream(candidate);
        if (classpathStream != null) {
            return classpathStream;
        }

        classpathStream = DBUtil.class.getClassLoader()
                .getResourceAsStream(candidate.startsWith("/") ? candidate.substring(1) : candidate);
        if (classpathStream != null) {
            return classpathStream;
        }

        java.nio.file.Path path = java.nio.file.Paths.get(candidate);
        if (java.nio.file.Files.exists(path)) {
            return java.nio.file.Files.newInputStream(path);
        }

        return null;
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
