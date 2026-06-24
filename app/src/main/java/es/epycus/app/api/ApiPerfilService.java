package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.LogroUsuarioItem;
import es.epycus.app.model.dto.MensajeResponseDto;
import es.epycus.app.model.dto.PerfilResponse;
import es.epycus.app.model.dto.PersonajeItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface ApiPerfilService {
    @GET("api/v1/perfil")
    Call<RespuestaApi<PerfilResponse>> obtenerPerfil();

    @PUT("api/v1/perfil")
    Call<RespuestaApi<MensajeResponseDto>> actualizar(@Body Object body);

    @PUT("api/v1/perfil/cambiar-contrasena")
    Call<RespuestaApi<MensajeResponseDto>> cambiarContrasena(@Body Object body);

    @PUT("api/v1/perfil/personaje")
    Call<RespuestaApi<MensajeResponseDto>> cambiarPersonaje(@Body Object body);

    @PUT("api/v1/perfil/tema")
    Call<RespuestaApi<MensajeResponseDto>> cambiarTema(@Body Object body);

    @GET("api/v1/perfil/personajes")
    Call<RespuestaApi<List<PersonajeItem>>> personajes();

    @GET("api/v1/perfil/logros")
    Call<RespuestaApi<List<LogroUsuarioItem>>> logros();
}
