package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import retrofit2.Call;

public class PomodoroRepository {
    private final RetrofitClient api;

    public PomodoroRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
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
}
