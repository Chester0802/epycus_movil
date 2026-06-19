package es.epycus.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import es.epycus.app.R;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private Button btnLogin, btnRegistro;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);

        if (authRepository.isLoggedIn()) {
            navegarAlHome();
            return;
        }

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);

        btnLogin.setOnClickListener(v -> iniciarSesion());
        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Snackbar.make(btnLogin, "Completa todos los campos", Snackbar.LENGTH_SHORT).show();
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
                    String msg = "Credenciales incorrectas";
                    if (response.body() != null && response.body().getMensaje() != null) {
                        msg = response.body().getMensaje();
                    }
                    Snackbar.make(btnLogin, msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<AuthResponse>> call, Throwable t) {
                setLoading(false);
                Snackbar.make(btnLogin, "Error de conexion: verifica tu internet",
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Iniciando sesion..." : "Iniciar sesion");
    }

    private void navegarAlHome() {
        Intent intent = new Intent(LoginActivity.this, MainContainerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
