package es.epycus.app.ui.pomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.epycus.app.R;
import es.epycus.app.databinding.FragmentPomodoroBinding;

public class PomodoroFragment extends Fragment {

    private static final String KEY_SEGUNDOS = "segundosRestantes";
    private static final String KEY_IS_RUNNING = "isRunning";
    private static final String KEY_IS_PAUSA = "isPausa";
    private static final String KEY_CICLOS_HOY = "ciclosHoy";
    private static final String KEY_CICLOS_COMPLETADOS = "ciclosCompletados";

    private FragmentPomodoroBinding binding;
    private CountDownTimer timer;
    private boolean isRunning = false;
    private boolean isPausa = false;
    private int segundosRestantes = 25 * 60;
    private int ciclosHoy = 0;
    private final int TIEMPO_FOCO = 25 * 60;
    private final int TIEMPO_PAUSA = 5 * 60;
    private final int TIEMPO_PAUSA_LARGA = 15 * 60;
    private int ciclosAntesPausaLarga = 4;
    private int ciclosCompletados = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (savedInstanceState != null) {
            segundosRestantes = savedInstanceState.getInt(KEY_SEGUNDOS, TIEMPO_FOCO);
            isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING, false);
            isPausa = savedInstanceState.getBoolean(KEY_IS_PAUSA, false);
            ciclosHoy = savedInstanceState.getInt(KEY_CICLOS_HOY, 0);
            ciclosCompletados = savedInstanceState.getInt(KEY_CICLOS_COMPLETADOS, 0);
        }

        actualizarDisplay();
        binding.tvCiclos.setText(getString(R.string.ciclos_formato, ciclosCompletados));
        binding.tvTotalHoy.setText(getString(R.string.hoy_completados_formato, ciclosHoy));

        binding.btnControl.setOnClickListener(v -> {
            if (isRunning) {
                pausar();
            } else {
                iniciar();
            }
        });
        actualizarBoton();

        if (isRunning) {
            reanudarTimer();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SEGUNDOS, segundosRestantes);
        outState.putBoolean(KEY_IS_RUNNING, isRunning);
        outState.putBoolean(KEY_IS_PAUSA, isPausa);
        outState.putInt(KEY_CICLOS_HOY, ciclosHoy);
        outState.putInt(KEY_CICLOS_COMPLETADOS, ciclosCompletados);
    }

    private void actualizarBoton() {
        if (isRunning) {
            binding.btnControl.setText(R.string.pausar);
        } else if (segundosRestantes <= 0) {
            binding.btnControl.setText(R.string.iniciar_foco);
        } else {
            binding.btnControl.setText(R.string.reanudar);
        }
    }

    private void reanudarTimer() {
        timer = new CountDownTimer(segundosRestantes * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                segundosRestantes = (int) (millisUntilFinished / 1000);
                actualizarDisplay();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                if (isPausa) {
                    binding.btnControl.setText(R.string.iniciar_foco);
                    binding.tvEstado.setText(R.string.pausa_terminada);
                    isPausa = false;
                    segundosRestantes = TIEMPO_FOCO;
                } else {
                    ciclosCompletados++;
                    ciclosHoy++;
                    binding.tvCiclos.setText(getString(R.string.ciclos_formato, ciclosCompletados));
                    binding.tvTotalHoy.setText(getString(R.string.hoy_completados_formato, ciclosHoy));
                    binding.btnControl.setText(R.string.iniciar_pausa);
                    binding.tvEstado.setText(R.string.foco_completado);

                    if (ciclosCompletados % ciclosAntesPausaLarga == 0) {
                        segundosRestantes = TIEMPO_PAUSA_LARGA;
                        binding.tvEstado.setText(getString(R.string.pausa_larga_formato, TIEMPO_PAUSA_LARGA / 60));
                    } else {
                        segundosRestantes = TIEMPO_PAUSA;
                    }
                    isPausa = true;
                }
                actualizarDisplay();
            }
        }.start();
    }

    private void iniciar() {
        isRunning = true;
        binding.btnControl.setText(R.string.pausar);
        reanudarTimer();
    }

    private void pausar() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        binding.btnControl.setText(getString(R.string.reanudar));
    }

    private void actualizarDisplay() {
        int minutos = segundosRestantes / 60;
        int segs = segundosRestantes % 60;
        binding.tvTiempo.setText(String.format("%02d:%02d", minutos, segs));
        binding.tvEstado.setText(isPausa ? (CharSequence) getString(R.string.pausa) : getString(R.string.foco));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        binding = null;
    }
}
