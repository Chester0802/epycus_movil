package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PomodoroIniciarResponse;
import es.epycus.app.model.dto.PomodoroCicloCompletadoResponse;
import es.epycus.app.model.dto.PomodoroFinalizarResponse;
import es.epycus.app.model.dto.PomodoroConfiguracionResponse;
import es.epycus.app.model.dto.PomodoroTipResponse;
import es.epycus.app.model.dto.PomodoroSesionActivaResponse;
import es.epycus.app.model.dto.PomodoroHistorialResponse;
import es.epycus.app.model.dto.PomodoroRachaResponse;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

public class PomodoroRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;

    public PomodoroRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
    }

    public Call<RespuestaApi<PomodoroIniciarResponse>> iniciar(Object body) {
        return api.getApiPomodoroService().iniciar(body);
    }

    public Call<RespuestaApi<PomodoroCicloCompletadoResponse>> cicloCompletado(int sesionId, Object body) {
        return api.getApiPomodoroService().cicloCompletado(sesionId, body);
    }

    public Call<RespuestaApi<PomodoroFinalizarResponse>> finalizar(int sesionId, Object body) {
        return api.getApiPomodoroService().finalizar(sesionId, body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> cancelar(int sesionId) {
        return api.getApiPomodoroService().cancelar(sesionId);
    }

    public Call<RespuestaApi<PomodoroConfiguracionResponse>> configuracion() {
        return api.getApiPomodoroService().configuracion();
    }

    public Call<RespuestaApi<SuccessResponseDto>> actualizarConfiguracion(Object body) {
        return api.getApiPomodoroService().actualizarConfiguracion(body);
    }

    public Call<RespuestaApi<PomodoroTipResponse>> tipAleatorio() {
        return api.getApiPomodoroService().tipAleatorio();
    }

    public Call<RespuestaApi<PomodoroSesionActivaResponse>> sesionActiva() {
        return api.getApiPomodoroService().sesionActiva();
    }

    public Call<RespuestaApi<PomodoroRachaResponse>> racha() {
        return api.getApiPomodoroService().racha();
    }

    public Call<RespuestaApi<PomodoroHistorialResponse>> historial(String desde, String hasta, int pagina, int tamano) {
        return api.getApiPomodoroService().historial(desde, hasta, pagina, tamano);
    }

    public Call<RespuestaApi<Object>> estadisticas(String desde, String hasta) {
        return api.getApiPomodoroService().estadisticas(desde, hasta);
    }

    public void cacheJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public String getCachedJson(String key) {
        return cacheManager.get(key);
    }
}
