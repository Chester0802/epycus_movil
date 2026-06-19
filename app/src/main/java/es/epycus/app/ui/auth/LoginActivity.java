package es.epycus.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import es.epycus.app.BuildConfig;
import es.epycus.app.R;
import es.epycus.app.databinding.ActivityLoginBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.GoogleAuthDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    private GoogleSignInClient googleSignInClient;
    private Call activeCall;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnToggleTheme.setOnClickListener(v -> {
            ThemeManager.getInstance(this).toggle();
            recreate();
        });
        actualizarIconoTema();

        binding.btnLogin.setOnClickListener(v -> iniciarSesion());
        binding.btnGoogleAuth.setOnClickListener(v -> iniciarGoogleSignIn());
        binding.btnRegistro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));
    }

    private void iniciarGoogleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    enviarTokenGoogle(account);
                }
            } catch (ApiException e) {
                Snackbar.make(binding.btnGoogleAuth,
                        R.string.error_conexion, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void enviarTokenGoogle(GoogleSignInAccount account) {
        setLoading(true);
        GoogleAuthDto dto = new GoogleAuthDto(
                account.getId(),
                account.getEmail() != null ? account.getEmail() : "",
                account.getDisplayName() != null ? account.getDisplayName() : "",
                account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : ""
        );

        activeCall = authRepository.loginGoogle(dto);
        activeCall.enqueue(new Callback<RespuestaApi<AuthResponse>>() {
            @Override
            public void onResponse(Call<RespuestaApi<AuthResponse>> call,
                                   Response<RespuestaApi<AuthResponse>> response) {
                activeCall = null;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                    AuthResponse authData = response.body().getDatos();
                    String email = account.getEmail() != null ? account.getEmail() : "";
                    String nombre = account.getDisplayName() != null ? account.getDisplayName() : "";
                    authRepository.saveSession(authData, -1, nombre, email);
                    navegarAlHome();
                } else {
                    String msg = NetworkUtils.getErrorMessage(LoginActivity.this, response);
                    Snackbar.make(binding.btnGoogleAuth, msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<AuthResponse>> call, Throwable t) {
                activeCall = null;
                setLoading(false);
                Snackbar.make(binding.btnGoogleAuth,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
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
                    String msg = NetworkUtils.getErrorMessage(LoginActivity.this, response);
                    Snackbar.make(binding.btnLogin, msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<AuthResponse>> call, Throwable t) {
                activeCall = null;
                setLoading(false);
                Snackbar.make(binding.btnLogin,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
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
