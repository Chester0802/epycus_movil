package es.epycus.app.ui.pomodoro;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.epycus.app.R;

@SuppressLint("SetTextI18n")
public class PomodoroFragment extends Fragment {

    private TextView tvTiempo, tvEstado, tvCiclos, tvTotalHoy;
    private Button btnControl;
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
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        tvTiempo = view.findViewById(R.id.tvTiempo);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvCiclos = view.findViewById(R.id.tvCiclos);
        tvTotalHoy = view.findViewById(R.id.tvTotalHoy);
        btnControl = view.findViewById(R.id.btnControl);

        actualizarDisplay();
        tvCiclos.setText("Ciclos: 0");
        tvTotalHoy.setText("Hoy: 0 completados");

        btnControl.setOnClickListener(v -> {
            if (isRunning) {
                pausar();
            } else {
                iniciar();
            }
        });

        return view;
    }

    private void iniciar() {
        isRunning = true;
        btnControl.setText("Pausar");

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
                    btnControl.setText("Iniciar foco");
                    tvEstado.setText("Pausa terminada!");
                    isPausa = false;
                    segundosRestantes = TIEMPO_FOCO;
                } else {
                    ciclosCompletados++;
                    ciclosHoy++;
                    tvCiclos.setText("Ciclos: " + ciclosCompletados);
                    tvTotalHoy.setText("Hoy: " + ciclosHoy + " completados");
                    btnControl.setText("Iniciar pausa");
                    tvEstado.setText("Foco completado! Toma un descanso");

                    if (ciclosCompletados % ciclosAntesPausaLarga == 0) {
                        segundosRestantes = TIEMPO_PAUSA_LARGA;
                        tvEstado.setText("Pausa larga - " + TIEMPO_PAUSA_LARGA / 60 + " min");
                    } else {
                        segundosRestantes = TIEMPO_PAUSA;
                    }
                    isPausa = true;
                }
                actualizarDisplay();
            }
        }.start();
    }

    private void pausar() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        btnControl.setText("Reanudar");
    }

    private void actualizarDisplay() {
        int minutos = segundosRestantes / 60;
        int segs = segundosRestantes % 60;
        tvTiempo.setText(String.format("%02d:%02d", minutos, segs));
        tvEstado.setText(isPausa ? "Pausa" : "Foco");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }
}
