package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.AdminLoginResponseDto;
import es.epycus.app.model.dto.SuccessResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiAdminService {
    @POST("api/v1/admin/login")
    Call<RespuestaApi<AdminLoginResponseDto>> login(@Body Object body);

    @GET("api/v1/admin/usuarios")
    Call<RespuestaApi<Object>> usuarios();

    @GET("api/v1/admin/usuarios/{id}")
    Call<RespuestaApi<Object>> usuario(@Path("id") int id);

    @POST("api/v1/admin/usuarios/{usuarioId}/suscripcion/activar")
    Call<RespuestaApi<SuccessResponseDto>> activarSuscripcion(@Path("usuarioId") int usuarioId);

    @POST("api/v1/admin/usuarios/{usuarioId}/suscripcion/desactivar")
    Call<RespuestaApi<SuccessResponseDto>> desactivarSuscripcion(@Path("usuarioId") int usuarioId);

    @GET("api/v1/admin/frases")
    Call<RespuestaApi<Object>> frases();

    @POST("api/v1/admin/frases")
    Call<RespuestaApi<SuccessResponseDto>> crearFrase(@Body Object body);

    @DELETE("api/v1/admin/frases/{id}")
    Call<RespuestaApi<SuccessResponseDto>> eliminarFrase(@Path("id") int id);
}
