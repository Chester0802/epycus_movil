package es.epycus.app.repository;

import android.content.Context;

import java.util.List;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.data.local.entity.HabitoEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HabitoHoyDto;
import retrofit2.Call;

public class HabitosRepository {
    private final RetrofitClient api;
    private final AppDatabase database;

    public HabitosRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<List<HabitoHoyDto>>> hoy() {
        return api.getApiHabitosService().hoy();
    }

    public Call<RespuestaApi<Object>> completar(int id) {
        return api.getApiHabitosService().completar(id);
    }

    public Call<RespuestaApi<Object>> fallar(int id) {
        return api.getApiHabitosService().fallar(id);
    }

    public Call<RespuestaApi<Object>> listar() {
        return api.getApiHabitosService().listar();
    }

    public Call<RespuestaApi<Object>> crear(Object body) {
        return api.getApiHabitosService().crear(body);
    }

    public Call<RespuestaApi<Object>> actualizar(int id, Object body) {
        return api.getApiHabitosService().actualizar(id, body);
    }

    public Call<RespuestaApi<Object>> eliminar(int id) {
        return api.getApiHabitosService().eliminar(id);
    }

    public Call<RespuestaApi<Object>> categorias() {
        return api.getApiHabitosService().categorias();
    }

    public void cacheHabitosJson(String key, String json) {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().insert(new CacheEntity(key, json)));
    }

    public String getCachedHabitosJson(String key) {
        return database.cacheDao().getValue(key);
    }

    public void cacheHabitos(List<HabitoEntity> habitos) {
        database.habitoDao().deleteAll();
        database.habitoDao().insertAll(habitos);
    }

    public List<HabitoEntity> getCachedHabitos() {
        return database.habitoDao().getActivos();
    }
}
