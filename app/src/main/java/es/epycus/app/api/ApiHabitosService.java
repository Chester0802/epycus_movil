package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CompletarHabitoResponse;
import es.epycus.app.model.dto.FallarHabitoResponse;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.model.dto.RegistroSemanaDto;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.model.entidades.Habito;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiHabitosService {
    @GET("api/v1/habitos")
    Call<RespuestaApi<Object>> listar();

    @GET("api/v1/habitos/hoy")
    Call<RespuestaApi<List<HabitoHoyDto>>> hoy();

    @POST("api/v1/habitos/{id}/completar")
    Call<RespuestaApi<CompletarHabitoResponse>> completar(@Path("id") int id);

    @POST("api/v1/habitos/{id}/fallar")
    Call<RespuestaApi<FallarHabitoResponse>> fallar(@Path("id") int id);

    @GET("api/v1/habitos/{id}/semana")
    Call<RespuestaApi<List<RegistroSemanaDto>>> semana(@Path("id") int id);

    @GET("api/v1/habitos/{id}")
    Call<RespuestaApi<Habito>> obtener(@Path("id") int id);

    @POST("api/v1/habitos")
    Call<RespuestaApi<SuccessResponseDto>> crear(@Body Object body);

    @PUT("api/v1/habitos/{id}")
    Call<RespuestaApi<SuccessResponseDto>> actualizar(@Path("id") int id, @Body Object body);

    @DELETE("api/v1/habitos/{id}")
    Call<RespuestaApi<SuccessResponseDto>> eliminar(@Path("id") int id);

    @GET("api/v1/habitos/dashboard")
    Call<RespuestaApi<Object>> dashboard();

    @GET("api/v1/habitos/categorias")
    Call<RespuestaApi<Object>> categorias();
}
