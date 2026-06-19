package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PreguntaGuiaResponse;
import retrofit2.Call;

public class DiarioRepository {
    private final RetrofitClient api;
    private final AppDatabase database;

    public DiarioRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<Object>> hoy() {
        return api.getApiDiarioService().hoy();
    }

    public Call<RespuestaApi<Object>> porFecha(String fecha) {
        return api.getApiDiarioService().porFecha(fecha);
    }

    public Call<RespuestaApi<Object>> crear(Object body) {
        return api.getApiDiarioService().crear(body);
    }

    public Call<RespuestaApi<Object>> racha() {
        return api.getApiDiarioService().racha();
    }

    public Call<RespuestaApi<PreguntaGuiaResponse>> preguntaGuia() {
        return api.getApiDiarioService().preguntaGuia();
    }

    public void cacheJson(String key, String json) {
        AppDatabase.getWriteExecutor().execute(() ->
                database.cacheDao().insert(new CacheEntity(key, json)));
    }

    public String getCachedJson(String key) {
        return database.cacheDao().getValue(key);
    }
}
