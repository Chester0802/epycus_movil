package es.epycus.app.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityDashboardBinding;
import es.epycus.app.util.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        String nombre = sessionManager.getUserName();
        binding.tvBienvenida.setText(getString(R.string.hola_formato, nombre != null ? nombre : "Usuario"));
    }
}
