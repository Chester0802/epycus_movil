package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.model.RespuestaApi;
import retrofit2.Call;

public class MisionesRepository {
    private final RetrofitClient api;
    private final AppDatabase database;

    public MisionesRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<Object>> listar() {
        return api.getApiMisionesService().listar();
    }

    public Call<RespuestaApi<Object>> completar(int id) {
        return api.getApiMisionesService().completar(id);
    }

    public Call<RespuestaApi<Object>> crear(Object body) {
        return api.getApiMisionesService().crear(body);
    }

    public Call<RespuestaApi<Object>> eliminar(int id) {
        return api.getApiMisionesService().eliminar(id);
    }

    public Call<RespuestaApi<Object>> categorias() {
        return api.getApiMisionesService().categorias();
    }

    public void cacheJson(String key, String json) {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().insert(new CacheEntity(key, json)));
    }

    public String getCachedJson(String key) {
        return database.cacheDao().getValue(key);
    }
}
