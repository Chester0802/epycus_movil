package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiAdminService {
    @POST("api/admin/login")
    Call<RespuestaApi<Object>> login(@Body Object body);

    @GET("api/admin/usuarios")
    Call<RespuestaApi<Object>> usuarios();

    @GET("api/admin/usuarios/{id}")
    Call<RespuestaApi<Object>> usuario(@Path("id") int id);

    @POST("api/admin/usuarios/{usuarioId}/suscripcion/activar")
    Call<RespuestaApi<Object>> activarSuscripcion(@Path("usuarioId") int usuarioId);

    @POST("api/admin/usuarios/{usuarioId}/suscripcion/desactivar")
    Call<RespuestaApi<Object>> desactivarSuscripcion(@Path("usuarioId") int usuarioId);

    @GET("api/admin/frases")
    Call<RespuestaApi<Object>> frases();

    @POST("api/admin/frases")
    Call<RespuestaApi<Object>> crearFrase(@Body Object body);

    @DELETE("api/admin/frases/{id}")
    Call<RespuestaApi<Object>> eliminarFrase(@Path("id") int id);
}
