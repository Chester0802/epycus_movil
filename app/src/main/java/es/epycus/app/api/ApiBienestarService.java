package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PausaActivaDto;
import es.epycus.app.model.dto.RecomendacionPausaDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiBienestarService {
    @GET("api/bienestar/resumen")
    Call<RespuestaApi<Object>> resumen();

    @GET("api/bienestar/alertas")
    Call<RespuestaApi<Object>> alertas();

    @GET("api/bienestar/frase")
    Call<RespuestaApi<Object>> frase();

    @GET("api/bienestar/estado-hoy")
    Call<RespuestaApi<Object>> estadoHoy();

    @GET("api/bienestar/historial-animo")
    Call<RespuestaApi<Object>> historialAnimo(@Query("dias") int dias);

    @GET("api/bienestar/habitos-pendientes")
    Call<RespuestaApi<Object>> habitosPendientes();

    @GET("api/bienestar/misiones-pendientes")
    Call<RespuestaApi<Object>> misionesPendientes();

    @POST("api/bienestar/pausa-activa")
    Call<RespuestaApi<RecomendacionPausaDto>> pausaActiva(@Body PausaActivaDto body);
}
