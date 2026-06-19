package es.epycus.app.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.epycus.app.R;
import es.epycus.app.ui.habitos.HabitosFragment;
import es.epycus.app.ui.home.InicioFragment;
import es.epycus.app.ui.pomodoro.PomodoroFragment;
import es.epycus.app.ui.diario.DiarioFragment;
import es.epycus.app.ui.perfil.PerfilFragment;

public class MainContainerActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        bottomNav = findViewById(R.id.bottomNav);
        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            cargarFragmento(new InicioFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragmento = null;
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                fragmento = new InicioFragment();
            } else if (id == R.id.nav_habitos) {
                fragmento = new HabitosFragment();
            } else if (id == R.id.nav_pomodoro) {
                fragmento = new PomodoroFragment();
            } else if (id == R.id.nav_diario) {
                fragmento = new DiarioFragment();
            } else if (id == R.id.nav_perfil) {
                fragmento = new PerfilFragment();
            }

            if (fragmento != null) {
                cargarFragmento(fragmento);
                return true;
            }
            return false;
        });
    }

    private void cargarFragmento(Fragment fragmento) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragmento)
                .commit();
    }
}
