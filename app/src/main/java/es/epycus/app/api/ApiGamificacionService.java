package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiGamificacionService {
    @GET("api/gamificacion/mi-progreso")
    Call<RespuestaApi<Object>> miProgreso();

    @GET("api/gamificacion/logros")
    Call<RespuestaApi<Object>> logros();
}
