package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.DiarioEntradaEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DiarioEntradaResponse;
import es.epycus.app.model.dto.DiarioEntradasResponse;
import es.epycus.app.model.dto.PreguntaGuiaResponse;
import es.epycus.app.model.dto.DiarioRachaResponse;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

public class DiarioRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;
    private final AppDatabase database;

    public DiarioRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<DiarioEntradaResponse>> hoy() {
        return api.getApiDiarioService().hoy();
    }

    public Call<RespuestaApi<DiarioEntradaResponse>> porFecha(String fecha) {
        return api.getApiDiarioService().porFecha(fecha);
    }

    public Call<RespuestaApi<DiarioEntradaResponse>> crear(Object body) {
        return api.getApiDiarioService().crear(body);
    }

    public Call<RespuestaApi<DiarioEntradasResponse>> porMes(int anio, int mes) {
        return api.getApiDiarioService().porMes(anio, mes);
    }

    public Call<RespuestaApi<DiarioEntradaResponse>> actualizar(String fecha, Object body) {
        return api.getApiDiarioService().actualizar(fecha, body);
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

    public void cacheEntrada(DiarioEntradaEntity entrada) {
        database.diarioEntradaDao().insertOrUpdate(entrada);
    }

    public DiarioEntradaEntity getCachedEntrada(String fecha) {
        return database.diarioEntradaDao().getByFecha(fecha);
    }

    public DiarioEntradaEntity getUltimaEntradaCached() {
        return database.diarioEntradaDao().getUltima();
    }

    public DiarioEntradaEntity toEntity(DiarioEntradaResponse response) {
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(response.getEntrada());
        com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String fecha = obj.has("fecha") && !obj.get("fecha").isJsonNull()
                ? obj.get("fecha").getAsString() : sdf.format(new java.util.Date());
        String estado = obj.has("estado") && !obj.get("estado").isJsonNull()
                ? obj.get("estado").getAsString() : "";
        String nota = obj.has("nota") && !obj.get("nota").isJsonNull()
                ? obj.get("nota").getAsString() : "";
        return new DiarioEntradaEntity(fecha, estado, nota);
    }
}
