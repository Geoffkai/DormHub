package com.dormhub.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AuthService {
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASS = "admin123";

    private final String configuredUser;
    private final String configuredPass;

    public AuthService() {
        String[] creds = loadCredentials();
        this.configuredUser = creds[0];
        this.configuredPass = creds[1];
    }

    private String[] loadCredentials() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null)
                props.load(input);
        } catch (IOException e) {
            return new String[] { DEFAULT_USER, DEFAULT_PASS };
        }

        String user = props.getProperty("app.username", DEFAULT_USER);
        String pass = props.getProperty("app.password", DEFAULT_PASS);
        return new String[] { user, pass };
    }

    public boolean authenticate(String username, String password) {
        if (username == null || password == null)
            return false;
        return username.trim().equals(configuredUser) && password.trim().equals(configuredPass);
    }
}
