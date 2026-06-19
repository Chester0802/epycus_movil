package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiPomodoroService {
    @POST("api/pomodoro/iniciar")
    Call<RespuestaApi<Object>> iniciar(@Body Object body);

    @POST("api/pomodoro/{sesionId}/ciclo-completado")
    Call<RespuestaApi<Object>> cicloCompletado(@Path("sesionId") int sesionId, @Body Object body);

    @POST("api/pomodoro/{sesionId}/finalizar")
    Call<RespuestaApi<Object>> finalizar(@Path("sesionId") int sesionId, @Body Object body);

    @POST("api/pomodoro/{sesionId}/cancelar")
    Call<RespuestaApi<Object>> cancelar(@Path("sesionId") int sesionId);

    @GET("api/pomodoro/configuracion")
    Call<RespuestaApi<Object>> configuracion();

    @PUT("api/pomodoro/configuracion")
    Call<RespuestaApi<Object>> actualizarConfiguracion(@Body Object body);

    @GET("api/pomodoro/tip-aleatorio")
    Call<RespuestaApi<Object>> tipAleatorio();
}
