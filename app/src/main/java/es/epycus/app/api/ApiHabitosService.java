package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HabitoHoyDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiHabitosService {
    @GET("api/habitos")
    Call<RespuestaApi<Object>> listar();

    @GET("api/habitos/hoy")
    Call<RespuestaApi<List<HabitoHoyDto>>> hoy();

    @POST("api/habitos/{id}/completar")
    Call<RespuestaApi<Object>> completar(@Path("id") int id);

    @POST("api/habitos/{id}/fallar")
    Call<RespuestaApi<Object>> fallar(@Path("id") int id);

    @GET("api/habitos/{id}/semana")
    Call<RespuestaApi<Object>> semana(@Path("id") int id);

    @GET("api/habitos/{id}")
    Call<RespuestaApi<Object>> obtener(@Path("id") int id);

    @POST("api/habitos")
    Call<RespuestaApi<Object>> crear(@Body Object body);

    @PUT("api/habitos/{id}")
    Call<RespuestaApi<Object>> actualizar(@Path("id") int id, @Body Object body);

    @DELETE("api/habitos/{id}")
    Call<RespuestaApi<Object>> eliminar(@Path("id") int id);

    @GET("api/habitos/dashboard")
    Call<RespuestaApi<Object>> dashboard();

    @GET("api/habitos/categorias")
    Call<RespuestaApi<Object>> categorias();
}
