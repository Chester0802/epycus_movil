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

import android.app.AlertDialog;
import android.widget.EditText;

import es.epycus.app.BuildConfig;
import es.epycus.app.R;
import es.epycus.app.databinding.ActivityLoginBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.GoogleAuthDto;
import es.epycus.app.model.dto.RecuperarContrasenaDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.SessionManager;
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
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);
        sessionManager = SessionManager.getInstance(this);

        if (authRepository.isLoggedIn()) {
            if (sessionManager.isTokenExpired()) {
                sessionManager.logout();
            } else {
                navegarAlHome();
                return;
            }
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
        binding.tvRecuperarContrasena.setOnClickListener(v -> mostrarDialogoRecuperarContrasena());
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
        String googleId = account.getId();
        String email = account.getEmail() != null ? account.getEmail() : "";
        String nombre = account.getDisplayName() != null ? account.getDisplayName() : "";
        String fotoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

        GoogleAuthDto dto = new GoogleAuthDto(googleId, email, nombre, fotoUrl);

        activeCall = authRepository.loginGoogle(dto);
        activeCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                activeCall = null;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && ((RespuestaApi<AuthResponse>) response.body()).isExito()) {
                    AuthResponse authData = ((RespuestaApi<AuthResponse>) response.body()).getDatos();
                    String userName = SessionManager.extractNameFromToken(authData.getToken());
                    if (userName == null) userName = nombre;
                    int userIdGoogle = SessionManager.extractIdFromToken(authData.getToken());
                    authRepository.saveSession(authData, userIdGoogle > 0 ? userIdGoogle : -1, userName, email);
                    navegarAlHome();
                } else {
                    String msg = NetworkUtils.getErrorMessage(LoginActivity.this, response);
                    if ("completar_registro".equalsIgnoreCase(msg)) {
                        abrirCompletarRegistroGoogle(googleId, email, nombre, fotoUrl);
                    } else {
                        Snackbar.make(binding.btnGoogleAuth, msg, Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                activeCall = null;
                setLoading(false);
                Snackbar.make(binding.btnGoogleAuth,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void abrirCompletarRegistroGoogle(String googleId, String email, String nombre, String fotoUrl) {
        Intent intent = new Intent(LoginActivity.this, CompletarRegistroGoogleActivity.class);
        intent.putExtra("googleId", googleId);
        intent.putExtra("correo", email);
        intent.putExtra("nombre", nombre);
        intent.putExtra("fotoUrl", fotoUrl);
        startActivity(intent);
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
        activeCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                activeCall = null;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && ((RespuestaApi<AuthResponse>) response.body()).isExito()) {
                    RespuestaApi<AuthResponse> resp = (RespuestaApi<AuthResponse>) response.body();
                    AuthResponse authData = resp.getDatos();
                    String nombre = SessionManager.extractNameFromToken(authData.getToken());
                    if (nombre == null) nombre = correo;
                    int userIdLogin = SessionManager.extractIdFromToken(authData.getToken());
                    authRepository.saveSession(authData, userIdLogin > 0 ? userIdLogin : -1, nombre, correo);
                    navegarAlHome();
                } else {
                    String msg = NetworkUtils.getErrorMessage(LoginActivity.this, response);
                    Snackbar.make(binding.btnLogin, msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
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

    private void mostrarDialogoRecuperarContrasena() {
        EditText input = new EditText(this);
        input.setHint(getString(R.string.correo_hint));
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setPadding(48, 16, 48, 16);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.recuperar_contrasena));
        builder.setMessage(getString(R.string.recuperar_contrasena_mensaje));
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.enviar), (dialog, which) -> {
            String correo = input.getText().toString().trim();
            if (correo.isEmpty()) {
                Snackbar.make(binding.getRoot(), getString(R.string.ingresa_correo), Snackbar.LENGTH_SHORT).show();
                return;
            }
            recuperarContrasena(correo);
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    private void recuperarContrasena(String correo) {
        RecuperarContrasenaDto dto = new RecuperarContrasenaDto(correo);
        activeCall = authRepository.recuperarContrasena(dto);
        activeCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                activeCall = null;
                if (response.isSuccessful() && response.body() != null
                        && ((RespuestaApi<Object>) response.body()).isExito()) {
                    Snackbar.make(binding.getRoot(), getString(R.string.correo_recuperacion_enviado), Snackbar.LENGTH_LONG).show();
                } else {
                    String msg = NetworkUtils.getErrorMessage(LoginActivity.this, response);
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                activeCall = null;
                Snackbar.make(binding.getRoot(), getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
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
