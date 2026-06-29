package es.epycus.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {
    private static final String PREF_NAME = "epycus_session_encrypted";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences prefs;
    private static SessionManager instance;

    private SessionManager(Context context) {
        Context appContext = context.getApplicationContext();
        try {
            MasterKey masterKey = new MasterKey.Builder(appContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            prefs = EncryptedSharedPreferences.create(
                    appContext,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to initialize EncryptedSharedPreferences. Cannot store tokens securely.", e);
        }
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveSession(String token, String refreshToken, int userId, String nombre, String email) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, nombre)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public String getRefreshToken() { return prefs.getString(KEY_REFRESH_TOKEN, null); }
    public int getUserId() { return prefs.getInt(KEY_USER_ID, -1); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, null); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, null); }

    public void updateToken(String token, String refreshToken) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null && !isTokenExpired();
    }

    public boolean isTokenExpired() {
        String token = getToken();
        if (token == null) return true;
        try {
            String json = decodeJwtPayload(token);
            if (json == null) return true;
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("exp")) {
                long exp = obj.get("exp").getAsLong();
                return System.currentTimeMillis() / 1000 > exp;
            }
        } catch (Exception ignored) { }
        return false;
    }

    public void saveUserIdFromToken() {
        String token = getToken();
        if (token == null) return;
        int id = extractIdFromToken(token);
        if (id > 0) {
            prefs.edit().putInt(KEY_USER_ID, id).apply();
        }
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public static String extractNameFromToken(String token) {
        if (token == null) return null;
        try {
            String json = decodeJwtPayload(token);
            if (json == null) return null;
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name")) {
                return obj.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name").getAsString();
            }
            if (obj.has("name")) {
                return obj.get("name").getAsString();
            }
            if (obj.has("unique_name")) {
                return obj.get("unique_name").getAsString();
            }
        } catch (Exception ignored) { }
        return null;
    }

    public static int extractIdFromToken(String token) {
        if (token == null) return -1;
        try {
            String json = decodeJwtPayload(token);
            if (json == null) return -1;
            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            String idStr = null;
            if (obj.has("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier")) {
                idStr = obj.get("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").getAsString();
            } else if (obj.has("sub")) {
                idStr = obj.get("sub").getAsString();
            } else if (obj.has("nameid")) {
                idStr = obj.get("nameid").getAsString();
            }
            if (idStr != null) {
                return Integer.parseInt(idStr);
            }
        } catch (Exception ignored) { }
        return -1;
    }

    private static String decodeJwtPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;
        byte[] decoded = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE);
        return new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
    }
}
