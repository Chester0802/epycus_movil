package es.epycus.app.util;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;

public class CacheManager {

    private static CacheManager instance;
    private final AppDatabase database;

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
        // Limpieza inicial: las entradas expiradas que nunca se vuelven a leer no se
        // borran solas en get(); este barrido evita que la BD crezca indefinidamente.
        purgeExpired();
    }

    /** Constructor para tests: inyecta la BD y no lanza la purga asíncrona. */
    @VisibleForTesting
    CacheManager(AppDatabase database) {
        this.database = database;
    }

    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context.getApplicationContext());
        }
        return instance;
    }

    public void put(String key, String jsonData, long ttlSeconds) {
        String wrappedJson = wrap(jsonData, ttlSeconds, nowSeconds());
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().insert(new CacheEntity(key, wrappedJson)));
    }

    public interface CacheCallback {
        void onResult(String jsonData);
    }

    public void getAsync(String key, CacheCallback callback) {
        AppDatabase.getWriteExecutor().execute(() -> {
            String result = readFresh(key);
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .post(() -> callback.onResult(result));
        });
    }

    public void isCachedAsync(String key, CacheCallback callback) {
        getAsync(key, callback);
    }

    /** Devuelve el dato cacheado si no ha expirado; si expiró lo elimina y devuelve null. */
    public String get(String key) {
        return readFresh(key);
    }

    private String readFresh(String key) {
        String wrappedJson = database.cacheDao().getValue(key);
        if (wrappedJson == null) return null;
        try {
            JsonObject wrapper = JsonParser.parseString(wrappedJson).getAsJsonObject();
            long cachedAt = wrapper.get("cachedAt").getAsLong();
            long ttl = wrapper.get("ttl").getAsLong();
            if (!isExpired(cachedAt, ttl, nowSeconds())) {
                return wrapper.get("data").getAsString();
            }
            database.cacheDao().delete(key);
        } catch (Exception ignored) {
        }
        return null;
    }

    /** Elimina todas las entradas de cache cuyo TTL haya vencido (asíncrono). */
    public void purgeExpired() {
        AppDatabase.getWriteExecutor().execute(this::purgeExpiredBlocking);
    }

    @VisibleForTesting
    void purgeExpiredBlocking() {
        long now = nowSeconds();
        for (CacheEntity entry : database.cacheDao().getAll()) {
            try {
                JsonObject wrapper = JsonParser.parseString(entry.getValue()).getAsJsonObject();
                long cachedAt = wrapper.get("cachedAt").getAsLong();
                long ttl = wrapper.get("ttl").getAsLong();
                if (isExpired(cachedAt, ttl, now)) {
                    database.cacheDao().delete(entry.getKey());
                }
            } catch (Exception e) {
                // Entrada corrupta o con formato antiguo: descartarla.
                database.cacheDao().delete(entry.getKey());
            }
        }
    }

    public void clear() {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().deleteAll());
    }

    public void clear(String key) {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().delete(key));
    }

    // --- Helpers puros (testables) ---

    private static long nowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /** Una entrada está expirada cuando han pasado más de {@code ttl} segundos desde {@code cachedAt}. */
    @VisibleForTesting
    static boolean isExpired(long cachedAt, long ttl, long now) {
        return (now - cachedAt) > ttl;
    }

    /** Envuelve el dato con su marca de tiempo y TTL (formato persistido en la tabla cache). */
    @VisibleForTesting
    static String wrap(String data, long ttlSeconds, long cachedAt) {
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("data", data);
        wrapper.addProperty("cachedAt", cachedAt);
        wrapper.addProperty("ttl", ttlSeconds);
        return wrapper.toString();
    }
}
