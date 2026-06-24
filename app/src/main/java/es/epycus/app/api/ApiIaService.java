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
    @POST("api/v1/ia/chat")
    Call<RespuestaApi<ChatResponse>> chat(@Body ChatRequest body);

    @GET("api/v1/ia/historial")
    Call<RespuestaApi<IaHistorialResponse>> historial(@Query("conversacionId") String conversacionId);

    @GET("api/v1/ia/conversaciones")
    Call<RespuestaApi<IaConversacionesResponse>> conversaciones();

    @GET("api/v1/ia/sugerencias")
    Call<RespuestaApi<IaSugerenciasResponse>> sugerencias();

    @GET("api/v1/ia/contexto-bienestar")
    Call<RespuestaApi<Object>> contextoBienestar();

    @POST("api/v1/ia/feedback")
    Call<RespuestaApi<SuccessResponseDto>> feedback(@Body Object body);

    @GET("api/v1/ia/mensajes-hoy")
    Call<RespuestaApi<IaMensajesHoyResponse>> mensajesHoy();
}
