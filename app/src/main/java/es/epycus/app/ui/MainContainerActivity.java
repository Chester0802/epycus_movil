package es.epycus.app.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import es.epycus.app.R;
import es.epycus.app.databinding.ActivityMainContainerBinding;
import es.epycus.app.ui.habitos.HabitosFragment;
import es.epycus.app.util.ThemeManager;
import es.epycus.app.ui.home.InicioFragment;
import es.epycus.app.ui.pomodoro.PomodoroFragment;
import es.epycus.app.ui.diario.DiarioFragment;
import es.epycus.app.ui.perfil.PerfilFragment;
import es.epycus.app.ui.misiones.MisionesFragment;

public class MainContainerActivity extends AppCompatActivity {

    private ActivityMainContainerBinding binding;
    private FragmentManager fragmentManager;
    private Fragment fragmentoActual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityMainContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentoActual = new InicioFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragmentoActual, "inicio")
                    .commit();
        } else {
            fragmentoActual = fragmentManager.findFragmentById(R.id.fragmentContainer);
        }

        fragmentManager.addOnBackStackChangedListener(() -> {
            fragmentoActual = fragmentManager.findFragmentById(R.id.fragmentContainer);
        });

        setupNavigation();
    }

    private void setupNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment nuevo;
            String tag;

            if (id == R.id.nav_inicio) {
                tag = "inicio";
                nuevo = fragmentManager.findFragmentByTag(tag);
                if (nuevo == null) nuevo = new InicioFragment();
            } else if (id == R.id.nav_habitos) {
                tag = "habitos";
                nuevo = fragmentManager.findFragmentByTag(tag);
                if (nuevo == null) nuevo = new HabitosFragment();
            } else if (id == R.id.nav_misiones) {
                tag = "misiones";
                nuevo = fragmentManager.findFragmentByTag(tag);
                if (nuevo == null) nuevo = new MisionesFragment();
            } else if (id == R.id.nav_diario) {
                tag = "diario";
                nuevo = fragmentManager.findFragmentByTag(tag);
                if (nuevo == null) nuevo = new DiarioFragment();
            } else if (id == R.id.nav_perfil) {
                tag = "perfil";
                nuevo = fragmentManager.findFragmentByTag(tag);
                if (nuevo == null) nuevo = new PerfilFragment();
            } else {
                return false;
            }

            if (nuevo != fragmentoActual) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                if (fragmentoActual != null) {
                    transaction.hide(fragmentoActual);
                }
                if (nuevo.isAdded()) {
                    transaction.show(nuevo);
                } else {
                    transaction.add(R.id.fragmentContainer, nuevo, tag);
                }
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.commit();
                fragmentoActual = nuevo;
                return true;
            }
            return false;
        });
    }

    public void seleccionarTab(int itemId) {
        binding.bottomNav.setSelectedItemId(itemId);
    }

    public void navegarAIAChat() {
        startActivity(new Intent(this, es.epycus.app.ui.ia.IaChatActivity.class));
    }

    public void navegarAPomodoro() {
        String tag = "pomodoro";
        PomodoroFragment pomodoro = (PomodoroFragment) fragmentManager.findFragmentByTag(tag);
        if (pomodoro == null) pomodoro = new PomodoroFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentoActual != null) {
            transaction.hide(fragmentoActual);
        }
        if (pomodoro.isAdded()) {
            transaction.show(pomodoro);
        } else {
            transaction.add(R.id.fragmentContainer, pomodoro, tag);
        }
        transaction.addToBackStack("pomodoro");
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.commit();
        fragmentoActual = pomodoro;
    }
}
