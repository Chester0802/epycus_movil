package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiDashboardService {
    @GET("api/dashboard/resumen")
    Call<RespuestaApi<Object>> resumen();

    @GET("api/dashboard/frase-del-dia")
    Call<RespuestaApi<Object>> fraseDelDia();
}
