package es.epycus.app.repository;

import android.content.Context;

import java.util.List;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.MisionCompletarResponse;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

public class MisionesRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;

    public MisionesRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
    }

    public Call<RespuestaApi<List<MisionDto>>> listar() {
        return api.getApiMisionesService().listar();
    }

    public Call<RespuestaApi<MisionCompletarResponse>> completar(int id) {
        return api.getApiMisionesService().completar(id);
    }

    public Call<RespuestaApi<SuccessResponseDto>> crear(Object body) {
        return api.getApiMisionesService().crear(body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> eliminar(int id) {
        return api.getApiMisionesService().eliminar(id);
    }

    public Call<RespuestaApi<Object>> categorias() {
        return api.getApiMisionesService().categorias();
    }

    public void cacheJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public String getCachedJson(String key) {
        return cacheManager.get(key);
    }
}
