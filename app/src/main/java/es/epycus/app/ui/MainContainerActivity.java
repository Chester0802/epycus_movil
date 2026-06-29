package es.epycus.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import es.epycus.app.R;
import es.epycus.app.api.SignalRService;
import es.epycus.app.databinding.ActivityMainContainerBinding;
import es.epycus.app.ui.pomodoro.PomodoroFragment;
import es.epycus.app.util.SessionManager;
import es.epycus.app.util.ThemeManager;

public class MainContainerActivity extends AppCompatActivity implements SignalRService.SignalRListener {

    private ActivityMainContainerBinding binding;
    private FragmentManager fragmentManager;
    private ViewPager2 viewPager;
    private SignalRService signalRService;
    private boolean signalRIniciado = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();
        viewPager = binding.viewPager;

        MainFragmentAdapter adapter = new MainFragmentAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setUserInputEnabled(true);

        binding.viewPager.post(() -> aplicarInsets());

        setupNavigation();

        if (!signalRIniciado && SessionManager.getInstance(this).isLoggedIn()) {
            signalRIniciado = true;
            signalRService = SignalRService.getInstance(this, this);
            signalRService.iniciarConexion();
        }
    }

    private void aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPager, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            int bottomAppBarHeight = binding.bottomAppBar.getHeight();
            v.setPadding(0, topInset, 0, bottomInset + bottomAppBarHeight);
            return insets;
        });
        binding.viewPager.requestApplyInsets();
    }

    private void setupNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            int position = menuIdToPosition(id);
            if (position >= 0) {
                ocultarPomodoro();
                viewPager.setCurrentItem(position, true);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int menuId = positionToMenuId(position);
                if (menuId >= 0) {
                    binding.bottomNav.setSelectedItemId(menuId);
                }
            }
        });
    }

    private int menuIdToPosition(int menuId) {
        if (menuId == R.id.nav_inicio) return 0;
        if (menuId == R.id.nav_habitos) return 1;
        if (menuId == R.id.nav_misiones) return 2;
        if (menuId == R.id.nav_diario) return 3;
        if (menuId == R.id.nav_perfil) return 4;
        return -1;
    }

    private int positionToMenuId(int position) {
        switch (position) {
            case 0: return R.id.nav_inicio;
            case 1: return R.id.nav_habitos;
            case 2: return R.id.nav_misiones;
            case 3: return R.id.nav_diario;
            case 4: return R.id.nav_perfil;
            default: return -1;
        }
    }

    public void seleccionarTab(int itemId) {
        int position = menuIdToPosition(itemId);
        if (position >= 0) {
            ocultarPomodoro();
            viewPager.setCurrentItem(position, true);
        }
    }

    private void ocultarPomodoro() {
        if (binding.pomodoroContainer.getVisibility() == android.view.View.VISIBLE) {
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
            binding.pomodoroContainer.setVisibility(android.view.View.GONE);
        }
    }

    public void navegarAIAChat() {
        startActivity(new Intent(this, es.epycus.app.ui.ia.IaChatActivity.class));
    }

    public void navegarAPomodoro() {
        String tag = "pomodoro";
        Fragment current = fragmentManager.findFragmentById(R.id.pomodoroContainer);
        PomodoroFragment pomodoro = (PomodoroFragment) fragmentManager.findFragmentByTag(tag);
        if (pomodoro == null) pomodoro = new PomodoroFragment();

        binding.pomodoroContainer.setVisibility(android.view.View.VISIBLE);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (current != null) {
            transaction.hide(current);
        }
        if (pomodoro.isAdded()) {
            transaction.show(pomodoro);
        } else {
            transaction.add(R.id.pomodoroContainer, pomodoro, tag);
        }
        transaction.addToBackStack("pomodoro");
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            if (fragmentManager.getBackStackEntryCount() == 0) {
                binding.pomodoroContainer.setVisibility(android.view.View.GONE);
            }
        } else if (binding.pomodoroContainer.getVisibility() == android.view.View.VISIBLE) {
            ocultarPomodoro();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (signalRService != null) {
            signalRService.desconectar();
            SignalRService.resetInstance();
            signalRIniciado = false;
        }
    }

    @Override
    public void onPomodoroCicloCompletado(int xpGanado, boolean sugerirDescanso, String pausaActiva) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Pomodoro ciclo completado: xp=" + xpGanado);
            Toast.makeText(this, "+" + xpGanado + " XP", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onPomodoroFinalizado(int xpTotal, boolean sesionGuardada) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Pomodoro finalizado: xpTotal=" + xpTotal);
            Toast.makeText(this, "Sesión completada: " + xpTotal + " XP", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onHabitoCompletado(int habitId, int xpGanado) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Hábito completado: " + habitId);
            Toast.makeText(this, "Hábito completado: +" + xpGanado + " XP", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMisionCompletada(int misionId, int xpGanado) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Misión completada: " + misionId);
            Toast.makeText(this, "Misión completada: +" + xpGanado + " XP", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onNotificacionRecibida(String titulo, String mensaje, String tipo) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Notificación: " + titulo);
            Toast.makeText(this, titulo + ": " + mensaje, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onEstadoCambio(String entidad, int entidadId, String nuevoEstado) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Estado cambio: " + entidad + " " + entidadId + " -> " + nuevoEstado);
        });
    }

    @Override
    public void onConnectionStateChanged(boolean conectado) {
        runOnUiThread(() -> {
            Log.d("SignalR", "Conexión: " + (conectado ? "Conectado" : "Desconectado"));
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Log.e("SignalR", "Error: " + error);
            Toast.makeText(this, "Error conexión: " + error, Toast.LENGTH_SHORT).show();
        });
    }
}
