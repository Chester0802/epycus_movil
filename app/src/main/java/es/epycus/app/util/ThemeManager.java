package es.epycus.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREF_NAME = "epycus_theme";
    private static final String KEY_IS_LIGHT = "is_light_theme";
    private static final String KEY_HAS_PREFERENCE = "has_theme_preference";
    private static ThemeManager instance;
    private final SharedPreferences prefs;

    private ThemeManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }

    public boolean isLightTheme() {
        return prefs.getBoolean(KEY_IS_LIGHT, true);
    }

    public boolean hasPreference() {
        return prefs.getBoolean(KEY_HAS_PREFERENCE, false);
    }

    public void setLightTheme(boolean isLight) {
        prefs.edit()
                .putBoolean(KEY_IS_LIGHT, isLight)
                .putBoolean(KEY_HAS_PREFERENCE, true)
                .apply();
        AppCompatDelegate.setDefaultNightMode(
                isLight ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void applyTheme() {
        if (!hasPreference()) return;
        AppCompatDelegate.setDefaultNightMode(
                isLightTheme() ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void resetToSystem() {
        prefs.edit().remove(KEY_HAS_PREFERENCE).remove(KEY_IS_LIGHT).apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void toggle() {
        setLightTheme(!isLightTheme());
    }
}
