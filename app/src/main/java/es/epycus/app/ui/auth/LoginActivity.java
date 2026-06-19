package es.epycus.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityLoginBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    private Call<?> activeCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);

        if (authRepository.isLoggedIn()) {
            navegarAlHome();
            return;
        }

        binding.btnToggleTheme.setOnClickListener(v -> {
            ThemeManager.getInstance(this).toggle();
            recreate();
        });
        actualizarIconoTema();

        binding.btnLogin.setOnClickListener(v -> iniciarSesion());
        binding.btnRegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));
    }

    private void actualizarIconoTema() {
        boolean isLight = ThemeManager.getInstance(this).isLightTheme();
        binding.btnToggleTheme.setText(isLight ? R.string.modo_oscuro : R.string.modo_claro);
    }

    private void iniciarSesion() {
        String correo = binding.etCorreo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Snackbar.make(binding.btnLogin, getString(R.string.completa_campos), Snackbar.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        activeCall = authRepository.login(correo, contrasena);
        activeCall.enqueue(new Callback<RespuestaApi<AuthResponse>>() {
            @Override
            public void onResponse(Call<RespuestaApi<AuthResponse>> call,
                                   Response<RespuestaApi<AuthResponse>> response) {
                activeCall = null;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                    AuthResponse authData = response.body().getDatos();
                    authRepository.saveSession(authData, -1, correo, correo);
                    navegarAlHome();
                } else {
                    String msg = getString(R.string.credenciales_incorrectas);
                    if (response.body() != null && response.body().getMensaje() != null) {
                        msg = response.body().getMensaje();
                    }
                    Snackbar.make(binding.btnLogin, msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<AuthResponse>> call, Throwable t) {
                activeCall = null;
                setLoading(false);
                int msgRes;
                if (t instanceof SocketTimeoutException) {
                    msgRes = R.string.error_timeout;
                } else if (t instanceof UnknownHostException || t instanceof ConnectException) {
                    msgRes = R.string.error_sin_conexion;
                } else {
                    msgRes = R.string.error_conexion;
                }
                Snackbar.make(binding.btnLogin, getString(msgRes), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
        binding.loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void navegarAlHome() {
        Intent intent = new Intent(LoginActivity.this, MainContainerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (activeCall != null && !activeCall.isCanceled()) {
            activeCall.cancel();
        }
        super.onDestroy();
    }
}
