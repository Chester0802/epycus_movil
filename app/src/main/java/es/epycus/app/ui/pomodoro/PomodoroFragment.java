package es.epycus.app.ui.pomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.FragmentPomodoroBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.repository.PomodoroRepository;
import es.epycus.app.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PomodoroFragment extends Fragment {

    private static final String KEY_SEGUNDOS = "segundosRestantes";
    private static final String KEY_IS_RUNNING = "isRunning";
    private static final String KEY_IS_PAUSA = "isPausa";
    private static final String KEY_CICLOS_HOY = "ciclosHoy";
    private static final String KEY_CICLOS_COMPLETADOS = "ciclosCompletados";
    private static final String KEY_SESION_ID = "sesionId";

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
    private int sesionId = -1;
    private PomodoroRepository repository;
    private final List<Call<?>> activeCalls = new ArrayList<>();
    private boolean tipCargado = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        repository = new PomodoroRepository(requireContext());

        if (savedInstanceState != null) {
            segundosRestantes = savedInstanceState.getInt(KEY_SEGUNDOS, TIEMPO_FOCO);
            isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING, false);
            isPausa = savedInstanceState.getBoolean(KEY_IS_PAUSA, false);
            ciclosHoy = savedInstanceState.getInt(KEY_CICLOS_HOY, 0);
            ciclosCompletados = savedInstanceState.getInt(KEY_CICLOS_COMPLETADOS, 0);
            sesionId = savedInstanceState.getInt(KEY_SESION_ID, -1);
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

        cargarTipAleatorio();
        cargarConfiguracion();

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
        outState.putInt(KEY_SESION_ID, sesionId);
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
                    notificarCicloCompletado();
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
                    notificarCicloCompletado();
                }
                actualizarDisplay();
            }
        }.start();
    }

    private void iniciar() {
        if (sesionId == -1) {
            iniciarSesionEnBackend();
        }
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

    private void iniciarSesionEnBackend() {
        JsonObject body = new JsonObject();
        body.addProperty("duracionFoco", TIEMPO_FOCO / 60);
        body.addProperty("duracionPausa", TIEMPO_PAUSA / 60);

        Call<RespuestaApi<Object>> call = repository.iniciar(body);
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String json = gson.toJson(response.body().getDatos());
                        JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                        if (obj.has("id")) {
                            sesionId = obj.get("id").getAsInt();
                        }
                    } catch (Exception e) {
                        // sesionId remains -1, local mode
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    private void notificarCicloCompletado() {
        if (sesionId == -1) return;
        JsonObject body = new JsonObject();
        body.addProperty("tipo", isPausa ? "pausa" : "foco");

        Call<RespuestaApi<Object>> call = repository.cicloCompletado(sesionId, body);
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    private void cargarTipAleatorio() {
        Call<RespuestaApi<Object>> call = repository.tipAleatorio();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String json = gson.toJson(response.body().getDatos());
                        JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                        if (obj.has("consejo") || obj.has("texto")) {
                            String tip = obj.has("consejo") ? obj.get("consejo").getAsString() : obj.get("texto").getAsString();
                            binding.tvTip.setText(tip);
                            tipCargado = true;
                        } else if (obj.has("mensaje")) {
                            binding.tvTip.setText(obj.get("mensaje").getAsString());
                            tipCargado = true;
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
                if (!tipCargado) {
                    binding.tvTip.setText(R.string.pomodoro_tip_default);
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                binding.tvTip.setText(R.string.pomodoro_tip_default);
            }
        });
    }

    private void cargarConfiguracion() {
        Call<RespuestaApi<Object>> call = repository.configuracion();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String json = gson.toJson(response.body().getDatos());
                        JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                        if (obj.has("duracionFoco")) {
                            // We would update TIEMPO_FOCO based on config, but for now just notify
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) call.cancel();
        }
        activeCalls.clear();
        binding = null;
    }
}