package es.epycus.app.ui.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import es.epycus.app.R;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.RegistroRequestDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.model.entidades.Carrera;
import es.epycus.app.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etContrasena, etConfirmarContrasena, etFechaNacimiento;
    private Spinner spGenero, spCarrera;
    private Button btnRegistrar;
    private AuthRepository authRepository;
    private List<Carrera> carreras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        authRepository = new AuthRepository(this);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        spGenero = findViewById(R.id.spGenero);
        spCarrera = findViewById(R.id.spCarrera);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        cargarCarreras();

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void cargarCarreras() {
        // TODO: Load carreras from API and populate spinner
    }

    private void registrar() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String confirmar = etConfirmarContrasena.getText().toString().trim();
        String fechaNac = etFechaNacimiento.getText().toString().trim();
        String genero = spGenero.getSelectedItem().toString();
        int carreraId = spCarrera.getSelectedItemPosition();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, "Las contrasenas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroRequestDto dto = new RegistroRequestDto(nombre, correo, contrasena,
                confirmar, fechaNac, genero, carreraId, true);

        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        authRepository.registro(dto).enqueue(new Callback<RespuestaApi<AuthResponse>>() {
            @Override
            public void onResponse(Call<RespuestaApi<AuthResponse>> call,
                                   Response<RespuestaApi<AuthResponse>> response) {
                btnRegistrar.setEnabled(true);
                btnRegistrar.setText("Registrarse");

                if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                    Toast.makeText(RegistroActivity.this,
                            "Registro exitoso. Revisa tu correo para verificar tu cuenta.",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String msg = "Error en el registro";
                    if (response.body() != null && response.body().getMensaje() != null) {
                        msg = response.body().getMensaje();
                    }
                    Toast.makeText(RegistroActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<AuthResponse>> call, Throwable t) {
                btnRegistrar.setEnabled(true);
                btnRegistrar.setText("Registrarse");
                Toast.makeText(RegistroActivity.this,
                        "Error de conexion: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
