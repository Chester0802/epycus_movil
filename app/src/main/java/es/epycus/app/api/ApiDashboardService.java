package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DashboardResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiDashboardService {
    @GET("api/v1/dashboard/resumen")
    Call<RespuestaApi<DashboardResponse>> resumen();

    @GET("api/v1/dashboard/frase-del-dia")
    Call<RespuestaApi<Object>> fraseDelDia();
}
