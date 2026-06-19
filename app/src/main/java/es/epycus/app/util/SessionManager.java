package es.epycus.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "epycus_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences prefs;
    private static SessionManager instance;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
        return getToken() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
