package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiMisionesService {
    @GET("api/misiones")
    Call<RespuestaApi<Object>> listar();

    @GET("api/misiones/{id}")
    Call<RespuestaApi<Object>> obtener(@Path("id") int id);

    @POST("api/misiones")
    Call<RespuestaApi<Object>> crear(@Body Object body);

    @PUT("api/misiones/{id}")
    Call<RespuestaApi<Object>> actualizar(@Path("id") int id, @Body Object body);

    @DELETE("api/misiones/{id}")
    Call<RespuestaApi<Object>> eliminar(@Path("id") int id);

    @POST("api/misiones/{id}/completar")
    Call<RespuestaApi<Object>> completar(@Path("id") int id);

    @POST("api/misiones/{id}/estado")
    Call<RespuestaApi<Object>> cambiarEstado(@Path("id") int id, @Body Object body);

    @GET("api/misiones/categorias")
    Call<RespuestaApi<Object>> categorias();
}
