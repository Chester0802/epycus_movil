package es.epycus.app.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityRegistroBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.RegistroRequestDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private AuthRepository authRepository;
    private List<Carrera> carreras;
    private Call activeCall;
    private int carreraSeleccionadaId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository(this);

        cargarCarreras();
        configurarSpinnerGenero();

        binding.etFechaNacimiento.setFocusable(false);
        binding.etFechaNacimiento.setClickable(true);
        binding.etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());
        binding.etFechaNacimiento.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) mostrarDatePicker();
        });

        binding.spCarrera.setOnItemClickListener((parent, view, position, id) -> {
            if (carreras != null && position >= 0 && position < carreras.size()) {
                carreraSeleccionadaId = carreras.get(position).getId();
            }
        });

        binding.btnRegistrar.setOnClickListener(v -> registrar());

        SpannableString termsText = new SpannableString(getString(R.string.acepto_terminos));
        String fullText = getString(R.string.acepto_terminos);
        int start = fullText.indexOf("términos");
        if (start == -1) start = fullText.indexOf("terminos");
        if (start == -1) start = fullText.indexOf("Terminos");
        int end = start + 9;
        if (start >= 0) {
            termsText.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://epycus.es/terminos"));
                    startActivity(browserIntent);
                }
            }, start, end, 0);
        }
        binding.cbTerminos.setText(termsText);
        binding.cbTerminos.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR) - 18;
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            binding.etFechaNacimiento.setText(fecha);
        }, anio, mes, dia);
        picker.show();
    }

    private void configurarSpinnerGenero() {
        String[] generos = getResources().getStringArray(R.array.generos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, generos);
        binding.spGenero.setAdapter(adapter);
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegistroActivity.this,
                            android.R.layout.simple_spinner_item, nombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spCarrera.setAdapter(adapter);
                } else {
                    String msg = NetworkUtils.getErrorMessage(RegistroActivity.this, response);
                    Toast.makeText(RegistroActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Snackbar.make(binding.btnRegistrar,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void registrar() {
        String nombre = binding.etNombre.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString().trim();
        String confirmar = binding.etConfirmarContrasena.getText().toString().trim();
        String fechaNac = binding.etFechaNacimiento.getText().toString().trim();
        String genero = binding.spGenero.getText().toString().trim();
        if (genero.isEmpty()) {
            genero = getResources().getStringArray(R.array.generos)[0];
        }
        boolean aceptaTerminos = binding.cbTerminos.isChecked();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, getString(R.string.completa_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, getString(R.string.contrasenas_no_coinciden), Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaNac.isEmpty()) {
            Toast.makeText(this, getString(R.string.selecciona_fecha_nacimiento), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!aceptaTerminos) {
            Toast.makeText(this, getString(R.string.acepta_terminos_error), Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroRequestDto dto = new RegistroRequestDto(nombre, correo, contrasena,
                confirmar, fechaNac, genero, carreraSeleccionadaId, true);

        binding.btnRegistrar.setVisibility(View.INVISIBLE);
        binding.loadingView.setVisibility(View.VISIBLE);

        activeCall = authRepository.registro(dto);
        activeCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                binding.btnRegistrar.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null
                        && ((RespuestaApi<AuthResponse>) response.body()).isExito()) {
                    Toast.makeText(RegistroActivity.this,
                            getString(R.string.registro_exitoso),
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String msg = NetworkUtils.getErrorMessage(RegistroActivity.this, response);
                    Toast.makeText(RegistroActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                binding.btnRegistrar.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);
                Snackbar.make(binding.btnRegistrar,
                        getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (activeCall != null && !activeCall.isCanceled()) {
            activeCall.cancel();
        }
        super.onDestroy();
    }
}
