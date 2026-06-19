package es.epycus.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREF_NAME = "epycus_theme";
    private static final String KEY_IS_LIGHT = "is_light_theme";
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

    public void setLightTheme(boolean isLight) {
        prefs.edit().putBoolean(KEY_IS_LIGHT, isLight).apply();
        AppCompatDelegate.setDefaultNightMode(
                isLight ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
                isLightTheme() ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void toggle() {
        setLightTheme(!isLightTheme());
    }
}
