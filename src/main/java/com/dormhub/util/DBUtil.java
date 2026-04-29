package com.dormhub.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBUtil {
    private static final String SCHEMA_RESOURCE = "/com/dormhub/db/dormhub.sql";
    private static final Object INIT_LOCK = new Object();

    private static volatile boolean schemaInitialized;

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        DatabaseConfigLoader.DatabaseConfig config = DatabaseConfigLoader.loadDatabaseConfig();
        Connection connection = java.sql.DriverManager.getConnection(config.url(), config.user(), config.password());
        initializeSchema(connection);
        return connection;
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

    private static String readResourceSchema(String resource) {
        try (java.io.InputStream input = DBUtil.class.getResourceAsStream(resource)) {
            if (input == null)
                return null;
            return new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            return null;
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
