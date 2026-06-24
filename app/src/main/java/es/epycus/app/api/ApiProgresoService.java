package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.ProgresoResponseDto;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiProgresoService {
    @GET("api/v1/progreso")
    Call<RespuestaApi<ProgresoResponseDto>> progreso();

    @GET("api/v1/progreso/logros")
    Call<RespuestaApi<Object>> logros();

    @GET("api/v1/progreso/historial-animo")
    Call<RespuestaApi<Object>> historialAnimo();
}
