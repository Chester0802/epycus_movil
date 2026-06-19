package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import retrofit2.Call;

public class HabitosRepository {
    private final RetrofitClient api;

    public HabitosRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
    }

    public Call<RespuestaApi<Object>> listar() {
        return api.getApiHabitosService().listar();
    }

    public Call<RespuestaApi<Object>> hoy() {
        return api.getApiHabitosService().hoy();
    }

    public Call<RespuestaApi<Object>> completar(int id) {
        return api.getApiHabitosService().completar(id);
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
}
