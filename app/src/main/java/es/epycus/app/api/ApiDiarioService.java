package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PreguntaGuiaResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiDiarioService {
    @GET("api/diario/hoy")
    Call<RespuestaApi<Object>> hoy();

    @GET("api/diario/fecha")
    Call<RespuestaApi<Object>> porFecha(@Query("fecha") String fecha);

    @GET("api/diario/mes")
    Call<RespuestaApi<Object>> porMes(@Query("anio") int anio, @Query("mes") int mes);

    @POST("api/diario")
    Call<RespuestaApi<Object>> crear(@Body Object body);

    @PUT("api/diario/{fecha}")
    Call<RespuestaApi<Object>> actualizar(@Path("fecha") String fecha, @Body Object body);

    @GET("api/diario/racha")
    Call<RespuestaApi<Object>> racha();

    @GET("api/diario/promedio-mes")
    Call<RespuestaApi<Object>> promedioMes(@Query("anio") int anio, @Query("mes") int mes);

    @GET("api/diario/pregunta-guia")
    Call<RespuestaApi<PreguntaGuiaResponse>> preguntaGuia();
}
