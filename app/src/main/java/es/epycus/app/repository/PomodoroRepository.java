package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.model.RespuestaApi;
import retrofit2.Call;

public class PomodoroRepository {
    private final RetrofitClient api;
    private final AppDatabase database;

    public PomodoroRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<Object>> iniciar(Object body) {
        return api.getApiPomodoroService().iniciar(body);
    }

    public Call<RespuestaApi<Object>> cicloCompletado(int sesionId, Object body) {
        return api.getApiPomodoroService().cicloCompletado(sesionId, body);
    }

    public Call<RespuestaApi<Object>> finalizar(int sesionId, Object body) {
        return api.getApiPomodoroService().finalizar(sesionId, body);
    }

    public Call<RespuestaApi<Object>> cancelar(int sesionId) {
        return api.getApiPomodoroService().cancelar(sesionId);
    }

    public Call<RespuestaApi<Object>> configuracion() {
        return api.getApiPomodoroService().configuracion();
    }

    public Call<RespuestaApi<Object>> actualizarConfiguracion(Object body) {
        return api.getApiPomodoroService().actualizarConfiguracion(body);
    }

    public void cacheJson(String key, String json) {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().insert(new CacheEntity(key, json)));
    }

    public String getCachedJson(String key) {
        return database.cacheDao().getValue(key);
    }
}
