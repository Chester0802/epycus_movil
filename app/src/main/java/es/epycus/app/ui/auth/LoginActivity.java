package es.epycus.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityLoginBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);

        if (authRepository.isLoggedIn()) {
            navegarAlHome();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> iniciarSesion());
        binding.btnRegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));
    }

    private void iniciarSesion() {
        String correo = binding.etCorreo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Snackbar.make(binding.btnLogin, getString(R.string.completa_campos), Snackbar.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        authRepository.login(correo, contrasena).enqueue(new Callback<RespuestaApi<AuthResponse>>() {
            @Override
            public void onResponse(Call<RespuestaApi<AuthResponse>> call,
                                   Response<RespuestaApi<AuthResponse>> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                    AuthResponse authData = response.body().getDatos();
                    authRepository.saveSession(authData, 0, correo, correo);
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
                setLoading(false);
                Snackbar.make(binding.btnLogin, getString(R.string.error_conexion),
                        Snackbar.LENGTH_LONG).show();
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
}
