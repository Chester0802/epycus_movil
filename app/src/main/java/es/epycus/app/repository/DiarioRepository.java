package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PreguntaGuiaResponse;
import es.epycus.app.model.dto.DiarioRachaResponse;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

public class DiarioRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;

    public DiarioRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
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

    public Call<RespuestaApi<DiarioRachaResponse>> racha() {
        return api.getApiDiarioService().racha();
    }

    public Call<RespuestaApi<PreguntaGuiaResponse>> preguntaGuia() {
        return api.getApiDiarioService().preguntaGuia();
    }

    public void cacheJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public String getCachedJson(String key) {
        return cacheManager.get(key);
    }
}
