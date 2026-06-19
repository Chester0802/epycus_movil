package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiEstadoAnimoService {
    @POST("api/estado-animo")
    Call<RespuestaApi<Object>> registrar(@Body Object body);

    @GET("api/estado-animo/historial")
    Call<RespuestaApi<Object>> historial();
}
