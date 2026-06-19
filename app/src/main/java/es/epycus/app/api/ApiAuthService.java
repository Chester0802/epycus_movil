package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.LoginDto;
import es.epycus.app.model.dto.RegistroRequestDto;
import es.epycus.app.model.dto.RefreshDto;
import es.epycus.app.model.dto.RecuperarContrasenaDto;
import es.epycus.app.model.dto.RestablecerContrasenaDto;
import es.epycus.app.model.dto.GoogleAuthDto;
import es.epycus.app.model.dto.CompletarRegistroGoogleDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiAuthService {
    @POST("api/auth/login")
    Call<RespuestaApi<AuthResponse>> login(@Body LoginDto body);

    @POST("api/auth/refresh")
    Call<RespuestaApi<AuthResponse>> refresh(@Body RefreshDto body);

    @POST("api/auth/logout")
    Call<RespuestaApi<Void>> logout();

    @POST("api/auth/registro")
    Call<RespuestaApi<AuthResponse>> registro(@Body RegistroRequestDto body);

    @GET("api/auth/verificar-correo")
    Call<RespuestaApi<Object>> verificarCorreo(@Query("token") String token);

    @POST("api/auth/recuperar-contrasena")
    Call<RespuestaApi<Object>> recuperarContrasena(@Body RecuperarContrasenaDto body);

    @POST("api/auth/restablecer-contrasena")
    Call<RespuestaApi<Object>> restablecerContrasena(@Body RestablecerContrasenaDto body);

    @POST("api/auth/google")
    Call<RespuestaApi<AuthResponse>> googleAuth(@Body GoogleAuthDto body);

    @POST("api/auth/completar-registro-google")
    Call<RespuestaApi<AuthResponse>> completarRegistroGoogle(@Body CompletarRegistroGoogleDto body);

    @GET("api/auth/carreras")
    Call<RespuestaApi<List<Carrera>>> obtenerCarreras();
}
