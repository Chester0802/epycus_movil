package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.GamificacionResponse;
import es.epycus.app.model.dto.LogroConProgresoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiGamificacionService {
    @GET("api/v1/gamificacion/mi-progreso")
    Call<RespuestaApi<GamificacionResponse>> miProgreso();

    @GET("api/v1/gamificacion/logros")
    Call<RespuestaApi<List<LogroConProgresoResponse>>> logros();
}
