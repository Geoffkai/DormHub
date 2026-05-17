package com.dormhub.auth;

import com.dormhub.util.DatabaseConfigLoader;

public class AuthService {

    /**
     * Authenticates the user against the admin credentials stored in
     * {@code app.env}. Unlike the previous version this class no longer reads
     * {@code db.properties} and no longer has hardcoded fallback credentials —
     * first-run setup (handled by
     * {@link com.dormhub.controller.LoginController}) must be completed before
     * any login attempt is made.
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String[] creds = DatabaseConfigLoader.loadAppCredentials();
        String storedUser = creds[0];
        String storedPass = creds[1];

        if (storedUser.isBlank() || storedPass.isBlank()) {
            // Setup has not been completed yet — deny access.
            return false;
        }

        return username.trim().equals(storedUser) && password.trim().equals(storedPass);
    }
}
