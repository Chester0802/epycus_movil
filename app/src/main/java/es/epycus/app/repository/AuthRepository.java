package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.LoginDto;
import es.epycus.app.model.dto.RefreshDto;
import es.epycus.app.model.dto.RegistroRequestDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;
import es.epycus.app.util.SessionManager;
import java.util.List;
import retrofit2.Call;

public class AuthRepository {
    private final RetrofitClient api;
    private final SessionManager sessionManager;

    public AuthRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.sessionManager = SessionManager.getInstance(context);
    }

    public Call<RespuestaApi<AuthResponse>> login(String correo, String contrasena) {
        return api.getApiAuthService().login(new LoginDto(correo, contrasena));
    }

    public Call<RespuestaApi<AuthResponse>> registro(RegistroRequestDto dto) {
        return api.getApiAuthService().registro(dto);
    }

    public Call<RespuestaApi<AuthResponse>> refresh() {
        String refreshToken = sessionManager.getRefreshToken();
        return api.getApiAuthService().refresh(new RefreshDto(refreshToken));
    }

    public Call<RespuestaApi<List<Carrera>>> obtenerCarreras() {
        return api.getApiAuthService().obtenerCarreras();
    }

    public Call<RespuestaApi<Void>> logout() {
        return api.getApiAuthService().logout();
    }

    public void saveSession(AuthResponse authResponse, int userId, String nombre, String email) {
        sessionManager.saveSession(
                authResponse.getToken(),
                authResponse.getRefreshToken(),
                userId,
                nombre,
                email
        );
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void clearSession() {
        sessionManager.logout();
    }
}
