package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.entidades.ProgresoUsuario;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiProgresoService {
    @GET("api/progreso")
    Call<RespuestaApi<ProgresoUsuario>> progreso();

    @GET("api/progreso/logros")
    Call<RespuestaApi<Object>> logros();

    @GET("api/progreso/historial-animo")
    Call<RespuestaApi<Object>> historialAnimo();
}
