package es.epycus.app.api;

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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPomodoroService {
    @POST("api/pomodoro/iniciar")
    Call<RespuestaApi<PomodoroIniciarResponse>> iniciar(@Body Object body);

    @POST("api/pomodoro/{sesionId}/ciclo-completado")
    Call<RespuestaApi<PomodoroCicloCompletadoResponse>> cicloCompletado(@Path("sesionId") int sesionId, @Body Object body);

    @POST("api/pomodoro/{sesionId}/finalizar")
    Call<RespuestaApi<PomodoroFinalizarResponse>> finalizar(@Path("sesionId") int sesionId, @Body Object body);

    @POST("api/pomodoro/{sesionId}/cancelar")
    Call<RespuestaApi<SuccessResponseDto>> cancelar(@Path("sesionId") int sesionId);

    @GET("api/pomodoro/configuracion")
    Call<RespuestaApi<PomodoroConfiguracionResponse>> configuracion();

    @PUT("api/pomodoro/configuracion")
    Call<RespuestaApi<SuccessResponseDto>> actualizarConfiguracion(@Body Object body);

    @GET("api/pomodoro/tip-aleatorio")
    Call<RespuestaApi<PomodoroTipResponse>> tipAleatorio();

    @GET("api/pomodoro/sesion-activa")
    Call<RespuestaApi<PomodoroSesionActivaResponse>> sesionActiva();

    @GET("api/pomodoro/historial")
    Call<RespuestaApi<PomodoroHistorialResponse>> historial(@Query("desde") String desde, @Query("hasta") String hasta, @Query("pagina") int pagina, @Query("tamano") int tamano);

    @GET("api/pomodoro/racha")
    Call<RespuestaApi<PomodoroRachaResponse>> racha();

    @GET("api/pomodoro/estadisticas")
    Call<RespuestaApi<Object>> estadisticas(@Query("desde") String desde, @Query("hasta") String hasta);
}
