package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiIaService {
    @POST("api/ia/chat")
    Call<RespuestaApi<Object>> chat(@Body Object body);

    @GET("api/ia/historial")
    Call<RespuestaApi<Object>> historial(@Query("conversacionId") String conversacionId);

    @GET("api/ia/conversaciones")
    Call<RespuestaApi<Object>> conversaciones();

    @GET("api/ia/sugerencias")
    Call<RespuestaApi<Object>> sugerencias();

    @GET("api/ia/contexto-bienestar")
    Call<RespuestaApi<Object>> contextoBienestar();

    @POST("api/ia/feedback")
    Call<RespuestaApi<Object>> feedback(@Body Object body);

    @GET("api/ia/mensajes-hoy")
    Call<RespuestaApi<Object>> mensajesHoy();
}
