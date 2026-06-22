package es.epycus.app.repository;

import android.content.Context;

import java.util.List;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.HabitoEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CompletarHabitoResponse;
import es.epycus.app.model.dto.FallarHabitoResponse;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.model.dto.RegistroSemanaDto;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

public class HabitosRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;
    private final AppDatabase database;

    public HabitosRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<List<HabitoHoyDto>>> hoy() {
        return api.getApiHabitosService().hoy();
    }

    public Call<RespuestaApi<CompletarHabitoResponse>> completar(int id) {
        return api.getApiHabitosService().completar(id);
    }

    public Call<RespuestaApi<FallarHabitoResponse>> fallar(int id) {
        return api.getApiHabitosService().fallar(id);
    }

    public Call<RespuestaApi<List<RegistroSemanaDto>>> semana(int id) {
        return api.getApiHabitosService().semana(id);
    }

    public Call<RespuestaApi<Object>> listar() {
        return api.getApiHabitosService().listar();
    }

    public Call<RespuestaApi<SuccessResponseDto>> crear(Object body) {
        return api.getApiHabitosService().crear(body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> actualizar(int id, Object body) {
        return api.getApiHabitosService().actualizar(id, body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> eliminar(int id) {
        return api.getApiHabitosService().eliminar(id);
    }

    public Call<RespuestaApi<Object>> categorias() {
        return api.getApiHabitosService().categorias();
    }

    public void cacheHabitosJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public String getCachedHabitosJson(String key) {
        return cacheManager.get(key);
    }

    public void cacheHabitos(List<HabitoEntity> habitos) {
        database.habitoDao().deleteAll();
        database.habitoDao().insertAll(habitos);
    }

    public List<HabitoEntity> getCachedHabitos() {
        return database.habitoDao().getActivos();
    }
}
