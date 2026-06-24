package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HistorialAnimoResponse;
import es.epycus.app.model.dto.PausaActivaDto;
import es.epycus.app.model.dto.RecomendacionPausaDto;
import es.epycus.app.model.dto.BienestarResumenResponse;
import es.epycus.app.model.dto.AlertasResponse;
import es.epycus.app.model.dto.EstadoHoyResponse;
import es.epycus.app.model.dto.CantidadResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiBienestarService {
    @GET("api/v1/bienestar/resumen")
    Call<RespuestaApi<BienestarResumenResponse>> resumen();

    @GET("api/v1/bienestar/alertas")
    Call<RespuestaApi<AlertasResponse>> alertas();

    @GET("api/v1/bienestar/frase")
    Call<RespuestaApi<Object>> frase();

    @GET("api/v1/bienestar/estado-hoy")
    Call<RespuestaApi<EstadoHoyResponse>> estadoHoy();

    @GET("api/v1/bienestar/historial-animo")
    Call<RespuestaApi<HistorialAnimoResponse>> historialAnimo(@Query("dias") int dias);

    @GET("api/v1/bienestar/habitos-pendientes")
    Call<RespuestaApi<CantidadResponse>> habitosPendientes();

    @GET("api/v1/bienestar/misiones-pendientes")
    Call<RespuestaApi<CantidadResponse>> misionesPendientes();

    @POST("api/v1/bienestar/pausa-activa")
    Call<RespuestaApi<RecomendacionPausaDto>> pausaActiva(@Body PausaActivaDto body);
}
