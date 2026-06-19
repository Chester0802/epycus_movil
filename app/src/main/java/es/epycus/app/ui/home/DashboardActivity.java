package es.epycus.app.ui.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import es.epycus.app.R;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class DashboardActivity extends AppCompatActivity {

    private TextView tvBienvenida;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = SessionManager.getInstance(this);
        tvBienvenida = findViewById(R.id.tvBienvenida);

        String nombre = sessionManager.getUserName();
        tvBienvenida.setText("Bienvenido, " + (nombre != null ? nombre : "Usuario"));
    }
}
