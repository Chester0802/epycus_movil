package es.epycus.app.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityRegistroBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.RegistroRequestDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private AuthRepository authRepository;
    private List<Carrera> carreras;
    private Call<?> activeCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);

        cargarCarreras();

        binding.btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void cargarCarreras() {
        activeCall = authRepository.obtenerCarreras();
        activeCall.enqueue(new Callback<RespuestaApi<List<Carrera>>>() {
            @Override
            public void onResponse(Call<RespuestaApi<List<Carrera>>> call,
                                   Response<RespuestaApi<List<Carrera>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    List<Carrera> lista = response.body().getDatos();
                    List<String> nombres = new ArrayList<>();
                    for (Carrera c : lista) {
                        nombres.add(c.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistroActivity.this,
                            android.R.layout.simple_spinner_item, nombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spCarrera.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistroActivity.this,
                            getString(R.string.error_carreras), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<List<Carrera>>> call, Throwable t) {
                mostrarErrorRed(t);
            }
        });
    }

    private void registrar() {
        String nombre = binding.etNombre.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString().trim();
        String confirmar = binding.etConfirmarContrasena.getText().toString().trim();
        String fechaNac = binding.etFechaNacimiento.getText().toString().trim();
        String genero = binding.spGenero.getSelectedItem().toString();
        int carreraId = binding.spCarrera.getSelectedItemPosition();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, getString(R.string.completa_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, getString(R.string.contrasenas_no_coinciden), Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroRequestDto dto = new RegistroRequestDto(nombre, correo, contrasena,
                confirmar, fechaNac, genero, carreraId, true);

        binding.btnRegistrar.setVisibility(View.INVISIBLE);
        binding.loadingView.setVisibility(View.VISIBLE);

        activeCall = authRepository.registro(dto);
        activeCall.enqueue(new Callback<RespuestaApi<AuthResponse>>() {
            @Override
            public void onResponse(Call<RespuestaApi<AuthResponse>> call,
                                   Response<RespuestaApi<AuthResponse>> response) {
                binding.btnRegistrar.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                    Toast.makeText(RegistroActivity.this,
                            getString(R.string.registro_exitoso),
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String msg = getString(R.string.error_registro);
                    if (response.body() != null && response.body().getMensaje() != null) {
                        msg = response.body().getMensaje();
                    }
                    Toast.makeText(RegistroActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<AuthResponse>> call, Throwable t) {
                binding.btnRegistrar.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);
                mostrarErrorRed(t);
            }
        });
    }

    private void mostrarErrorRed(Throwable t) {
        int msgRes;
        if (t instanceof SocketTimeoutException) {
            msgRes = R.string.error_timeout;
        } else if (t instanceof UnknownHostException || t instanceof ConnectException) {
            msgRes = R.string.error_sin_conexion;
        } else {
            msgRes = R.string.error_conexion;
        }
        Snackbar.make(binding.btnRegistrar, getString(msgRes), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (activeCall != null && !activeCall.isCanceled()) {
            activeCall.cancel();
        }
        super.onDestroy();
    }
}
