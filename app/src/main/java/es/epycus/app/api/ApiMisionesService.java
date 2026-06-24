package es.epycus.app.api;

import java.util.List;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CategoriaDto;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.MisionCompletarResponse;
import es.epycus.app.model.dto.SuccessResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiMisionesService {
    @GET("api/v1/misiones")
    Call<RespuestaApi<List<MisionDto>>> listar();

    @GET("api/v1/misiones/{id}")
    Call<RespuestaApi<MisionDto>> obtener(@Path("id") int id);

    @POST("api/v1/misiones")
    Call<RespuestaApi<SuccessResponseDto>> crear(@Body Object body);

    @PUT("api/v1/misiones/{id}")
    Call<RespuestaApi<SuccessResponseDto>> actualizar(@Path("id") int id, @Body Object body);

    @DELETE("api/v1/misiones/{id}")
    Call<RespuestaApi<SuccessResponseDto>> eliminar(@Path("id") int id);

    @POST("api/v1/misiones/{id}/completar")
    Call<RespuestaApi<MisionCompletarResponse>> completar(@Path("id") int id);

    @POST("api/v1/misiones/{id}/estado")
    Call<RespuestaApi<SuccessResponseDto>> cambiarEstado(@Path("id") int id, @Body Object body);

    @GET("api/v1/misiones/categorias")
    Call<RespuestaApi<List<CategoriaDto>>> categorias();
}
