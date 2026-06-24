package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.LoginDto;
import es.epycus.app.model.dto.RegistroRequestDto;
import es.epycus.app.model.dto.RefreshDto;
import es.epycus.app.model.dto.RecuperarContrasenaDto;
import es.epycus.app.model.dto.RestablecerContrasenaDto;
import es.epycus.app.model.dto.GoogleAuthDto;
import es.epycus.app.model.dto.CompletarRegistroGoogleDto;
import es.epycus.app.model.dto.MensajeResponseDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiAuthService {
    @POST("api/v1/auth/login")
    Call<RespuestaApi<AuthResponse>> login(@Body LoginDto body);

    @POST("api/v1/auth/refresh")
    Call<RespuestaApi<AuthResponse>> refresh(@Body RefreshDto body);

    @POST("api/v1/auth/logout")
    Call<RespuestaApi<MensajeResponseDto>> logout();

    @POST("api/v1/auth/registro")
    Call<RespuestaApi<AuthResponse>> registro(@Body RegistroRequestDto body);

    @GET("api/v1/auth/verificar-correo")
    Call<RespuestaApi<MensajeResponseDto>> verificarCorreo(@Query("token") String token);

    @POST("api/v1/auth/recuperar-contrasena")
    Call<RespuestaApi<MensajeResponseDto>> recuperarContrasena(@Body RecuperarContrasenaDto body);

    @POST("api/v1/auth/restablecer-contrasena")
    Call<RespuestaApi<MensajeResponseDto>> restablecerContrasena(@Body RestablecerContrasenaDto body);

    @POST("api/v1/auth/google")
    Call<RespuestaApi<AuthResponse>> googleAuth(@Body GoogleAuthDto body);

    @POST("api/v1/auth/completar-registro-google")
    Call<RespuestaApi<AuthResponse>> completarRegistroGoogle(@Body CompletarRegistroGoogleDto body);

    @GET("api/v1/auth/carreras")
    Call<RespuestaApi<List<Carrera>>> obtenerCarreras();
}
