package es.epycus.app.repository;

import android.content.Context;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.UsuarioEntity;
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
    private final AppDatabase database;
    private final Context context;

    public AuthRepository(Context context) {
        this.context = context.getApplicationContext();
        this.api = RetrofitClient.getInstance(context);
        this.sessionManager = SessionManager.getInstance(context);
        this.database = AppDatabase.getInstance(context);
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
        cacheUsuario(new UsuarioEntity(userId, nombre, email,
                authResponse.getToken(), authResponse.getRefreshToken(), ""));
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void clearSession() {
        sessionManager.logout();
        database.usuarioDao().deleteAll();
    }

    public void cacheUsuario(UsuarioEntity usuario) {
        database.usuarioDao().insert(usuario);
    }

    public UsuarioEntity getCachedUsuario() {
        int userId = sessionManager.getUserId();
        if (userId <= 0) return null;
        return database.usuarioDao().getById(userId);
    }
}
