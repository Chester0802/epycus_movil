package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PerfilResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface ApiPerfilService {
    @GET("api/perfil")
    Call<RespuestaApi<PerfilResponse>> obtenerPerfil();

    @PUT("api/perfil")
    Call<RespuestaApi<Object>> actualizar(@Body Object body);

    @PUT("api/perfil/cambiar-contrasena")
    Call<RespuestaApi<Object>> cambiarContrasena(@Body Object body);

    @PUT("api/perfil/personaje")
    Call<RespuestaApi<Object>> cambiarPersonaje(@Body Object body);

    @PUT("api/perfil/tema")
    Call<RespuestaApi<Object>> cambiarTema(@Body Object body);

    @GET("api/perfil/personajes")
    Call<RespuestaApi<Object>> personajes();

    @GET("api/perfil/logros")
    Call<RespuestaApi<Object>> logros();
}
