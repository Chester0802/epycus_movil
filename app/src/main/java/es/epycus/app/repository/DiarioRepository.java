package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import retrofit2.Call;

public class DiarioRepository {
    private final RetrofitClient api;

    public DiarioRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
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

    public Call<RespuestaApi<Object>> preguntaGuia() {
        return api.getApiDiarioService().preguntaGuia();
    }
}
