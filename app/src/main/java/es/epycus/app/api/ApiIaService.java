package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.ChatRequest;
import es.epycus.app.model.dto.ChatResponse;
import es.epycus.app.model.dto.IaConversacionesResponse;
import es.epycus.app.model.dto.IaHistorialResponse;
import es.epycus.app.model.dto.IaMensajesHoyResponse;
import es.epycus.app.model.dto.IaSugerenciasResponse;
import es.epycus.app.model.dto.SuccessResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiIaService {
    @POST("api/ia/chat")
    Call<RespuestaApi<ChatResponse>> chat(@Body ChatRequest body);

    @GET("api/ia/historial")
    Call<RespuestaApi<IaHistorialResponse>> historial(@Query("conversacionId") String conversacionId);

    @GET("api/ia/conversaciones")
    Call<RespuestaApi<IaConversacionesResponse>> conversaciones();

    @GET("api/ia/sugerencias")
    Call<RespuestaApi<IaSugerenciasResponse>> sugerencias();

    @GET("api/ia/contexto-bienestar")
    Call<RespuestaApi<Object>> contextoBienestar();

    @POST("api/ia/feedback")
    Call<RespuestaApi<SuccessResponseDto>> feedback(@Body Object body);

    @GET("api/ia/mensajes-hoy")
    Call<RespuestaApi<IaMensajesHoyResponse>> mensajesHoy();
}
