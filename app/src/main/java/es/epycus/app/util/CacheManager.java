package es.epycus.app.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;

public class CacheManager {

    private static CacheManager instance;
    private final AppDatabase database;
    private final Gson gson;

    public static final long TTL_DASHBOARD = 300;
    public static final long TTL_HABITOS = 120;
    public static final long TTL_MISIONES = 300;
    public static final long TTL_PROGRESO = 300;
    public static final long TTL_PERFIL = 600;
    public static final long TTL_DIARIO = 300;
    public static final long TTL_BIENESTAR = 120;
    public static final long TTL_POMODORO_CONFIG = 1800;
    public static final long TTL_CATEGORIAS = 1800;
    public static final long TTL_PREGUNTA_GUIA = 86400;

    private CacheManager(Context context) {
        this.database = AppDatabase.getInstance(context.getApplicationContext());
        this.gson = new Gson();
    }

    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context.getApplicationContext());
        }
        return instance;
    }

    public void put(String key, String jsonData, long ttlSeconds) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("data", jsonData);
        wrapper.addProperty("cachedAt", System.currentTimeMillis() / 1000);
        wrapper.addProperty("ttl", ttlSeconds);
        String wrappedJson = gson.toJson(wrapper);
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().insert(new CacheEntity(key, wrappedJson)));
    }

    public interface CacheCallback {
        void onResult(String jsonData);
    }

    public void getAsync(String key, CacheCallback callback) {
        AppDatabase.getWriteExecutor().execute(() -> {
            String wrappedJson = database.cacheDao().getValue(key);
            String result = null;
            if (wrappedJson != null) {
                try {
                    JsonObject wrapper = JsonParser.parseString(wrappedJson).getAsJsonObject();
                    long cachedAt = wrapper.get("cachedAt").getAsLong();
                    long ttl = wrapper.get("ttl").getAsLong();
                    long now = System.currentTimeMillis() / 1000;
                    if ((now - cachedAt) <= ttl) {
                        result = wrapper.get("data").getAsString();
                    } else {
                        database.cacheDao().delete(key);
                    }
                } catch (Exception ignored) {
                }
            }
            final String finalResult = result;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> callback.onResult(finalResult));
        });
    }

    public void isCachedAsync(String key, CacheCallback callback) {
        getAsync(key, callback);
    }

    public String get(String key) {
        String wrappedJson = database.cacheDao().getValue(key);
        if (wrappedJson != null) {
            try {
                com.google.gson.JsonObject wrapper = com.google.gson.JsonParser.parseString(wrappedJson).getAsJsonObject();
                long cachedAt = wrapper.get("cachedAt").getAsLong();
                long ttl = wrapper.get("ttl").getAsLong();
                long now = System.currentTimeMillis() / 1000;
                if ((now - cachedAt) <= ttl) {
                    return wrapper.get("data").getAsString();
                } else {
                    database.cacheDao().delete(key);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public void clear() {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().deleteAll());
    }

    public void clear(String key) {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().delete(key));
    }
}
