package es.epycus.app.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityCompletarRegistroGoogleBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CompletarRegistroGoogleDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.SessionManager;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletarRegistroGoogleActivity extends AppCompatActivity {

    private ActivityCompletarRegistroGoogleBinding binding;
    private AuthRepository authRepository;
    private List<Carrera> carreras;
    private Call activeCall;
    private String googleId;
    private String correo;
    private String nombre;
    private String fotoUrl;
    private int carreraSeleccionadaId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityCompletarRegistroGoogleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        googleId = getIntent().getStringExtra("googleId");
        correo = getIntent().getStringExtra("correo");
        nombre = getIntent().getStringExtra("nombre");
        fotoUrl = getIntent().getStringExtra("fotoUrl");

        if (googleId == null || correo == null) {
            Toast.makeText(this, "Error: datos de Google incompletos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authRepository = new AuthRepository(this);

        cargarCarreras();

        binding.etFechaNacimiento.setFocusable(false);
        binding.etFechaNacimiento.setClickable(true);
        binding.etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());
        binding.etFechaNacimiento.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) mostrarDatePicker();
        });

        binding.spCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (carreras != null && position >= 0 && position < carreras.size()) {
                    carreraSeleccionadaId = carreras.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                carreraSeleccionadaId = -1;
            }
        });

        binding.btnCompletarRegistro.setOnClickListener(v -> completarRegistro());
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR) - 18;
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            binding.etFechaNacimiento.setText(fecha);
        }, anio, mes, dia);
        picker.show();
    }

    private void cargarCarreras() {
        activeCall = authRepository.obtenerCarreras();
        activeCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null
                        && ((RespuestaApi<List<Carrera>>) response.body()).isExito()
                        && ((RespuestaApi<List<Carrera>>) response.body()).getDatos() != null) {
                    carreras = ((RespuestaApi<List<Carrera>>) response.body()).getDatos();
                    List<String> nombres = new ArrayList<>();
                    for (Carrera c : carreras) {
                        nombres.add(c.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CompletarRegistroGoogleActivity.this,
                            android.R.layout.simple_spinner_item, nombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spCarrera.setAdapter(adapter);
                } else {
                    String msg = NetworkUtils.getErrorMessage(CompletarRegistroGoogleActivity.this, response);
                    Toast.makeText(CompletarRegistroGoogleActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(binding.btnCompletarRegistro,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void completarRegistro() {
        String fechaNac = binding.etFechaNacimiento.getText().toString().trim();
        String genero = binding.spGenero.getSelectedItem().toString();
        boolean aceptaTerminos = binding.cbTerminos.isChecked();

        if (fechaNac.isEmpty()) {
            Toast.makeText(this, getString(R.string.selecciona_fecha_nacimiento), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!aceptaTerminos) {
            Toast.makeText(this, getString(R.string.acepta_terminos_error), Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnCompletarRegistro.setVisibility(View.INVISIBLE);
        binding.loadingView.setVisibility(View.VISIBLE);

        CompletarRegistroGoogleDto dto = new CompletarRegistroGoogleDto(
                nombre, correo, fechaNac, genero,
                carreraSeleccionadaId > 0 ? carreraSeleccionadaId : 1,
                true, googleId, fotoUrl
        );

        activeCall = authRepository.getApi().getApiAuthService().completarRegistroGoogle(dto);
        activeCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                binding.btnCompletarRegistro.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null
                        && ((RespuestaApi<AuthResponse>) response.body()).isExito()) {
                    RespuestaApi<AuthResponse> resp = (RespuestaApi<AuthResponse>) response.body();
                    AuthResponse authData = resp.getDatos();
                    String userName = SessionManager.extractNameFromToken(authData.getToken());
                    if (userName == null) userName = nombre;
                    authRepository.saveSession(authData, -1, userName, correo);
                    navegarAlHome();
                } else {
                    String msg = NetworkUtils.getErrorMessage(CompletarRegistroGoogleActivity.this, response);
                    Toast.makeText(CompletarRegistroGoogleActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                binding.btnCompletarRegistro.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);
                Snackbar.make(binding.btnCompletarRegistro,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void navegarAlHome() {
        Intent intent = new Intent(CompletarRegistroGoogleActivity.this, MainContainerActivity.class);
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
