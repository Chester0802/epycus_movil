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
import es.epycus.app.databinding.FragmentPomodoroBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.repository.PomodoroRepository;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.model.dto.PomodoroIniciarResponse;
import es.epycus.app.model.dto.PomodoroCicloCompletadoResponse;
import es.epycus.app.model.dto.PomodoroTipResponse;
import es.epycus.app.model.dto.PomodoroConfiguracionResponse;
import es.epycus.app.model.dto.PomodoroSesionActivaResponse;
import es.epycus.app.model.dto.PomodoroFinalizarResponse;
import es.epycus.app.model.dto.PomodoroRachaResponse;
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
    private int tiempoFoco = 25 * 60;
    private int tiempoPausa = 5 * 60;
    private int tiempoPausaLarga = 15 * 60;
    private int ciclosAntesPausaLarga = 4;
    private int ciclosCompletados = 0;
    private int ciclosHoy = 0;
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

        binding.swipeRefresh.setOnRefreshListener(this::recargarTodo);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        if (savedInstanceState != null) {
            segundosRestantes = savedInstanceState.getInt(KEY_SEGUNDOS, 25 * 60);
            isRunning = savedInstanceState.getBoolean(KEY_IS_RUNNING, false);
            isPausa = savedInstanceState.getBoolean(KEY_IS_PAUSA, false);
            ciclosHoy = savedInstanceState.getInt(KEY_CICLOS_HOY, 0);
            ciclosCompletados = savedInstanceState.getInt(KEY_CICLOS_COMPLETADOS, 0);
            sesionId = savedInstanceState.getInt(KEY_SESION_ID, -1);
        }

        actualizarDisplay();
        binding.tvCiclos.setText(getString(R.string.ciclos_formato, ciclosCompletados));
        binding.tvTotalHoy.setText(getString(R.string.hoy_completados_formato, ciclosHoy));
        binding.tvSessionLabel.setText(getString(R.string.sesion_formato, ciclosCompletados));

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

        recargarTodo();

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
            binding.btnControl.setIconResource(R.drawable.ic_pause);
        } else if (segundosRestantes <= 0) {
            binding.btnControl.setText(R.string.iniciar_foco);
            binding.btnControl.setIconResource(R.drawable.ic_play);
        } else {
            binding.btnControl.setText(R.string.reanudar);
            binding.btnControl.setIconResource(R.drawable.ic_play);
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
                    binding.btnControl.setIconResource(R.drawable.ic_play);
                    binding.btnControl.setText(R.string.iniciar_foco);
                    binding.tvEstado.setText(R.string.pausa_terminada);
                    isPausa = false;
                    segundosRestantes = tiempoFoco;
                    notificarCicloCompletado();
                } else {
                    ciclosCompletados++;
                    ciclosHoy++;
                    binding.tvCiclos.setText(getString(R.string.ciclos_formato, ciclosCompletados));
                    binding.tvTotalHoy.setText(getString(R.string.hoy_completados_formato, ciclosHoy));
                    binding.tvSessionLabel.setText(getString(R.string.sesion_formato, ciclosCompletados));
                    binding.btnControl.setIconResource(R.drawable.ic_play);
                    binding.btnControl.setText(R.string.iniciar_pausa);
                    binding.tvEstado.setText(R.string.foco_completado);

                    if (ciclosCompletados % ciclosAntesPausaLarga == 0) {
                        segundosRestantes = tiempoPausaLarga;
                        binding.tvEstado.setText(getString(R.string.pausa_larga_formato, tiempoPausaLarga / 60));
                    } else {
                        segundosRestantes = tiempoPausa;
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
        binding.btnControl.setIconResource(R.drawable.ic_pause);
        binding.btnControl.setText(R.string.pausar);
        reanudarTimer();
    }

    private void pausar() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        binding.btnControl.setIconResource(R.drawable.ic_play);
        binding.btnControl.setText(R.string.reanudar);
    }

    private void actualizarDisplay() {
        int minutos = segundosRestantes / 60;
        int segs = segundosRestantes % 60;
        binding.tvTiempo.setText(String.format("%02d:%02d", minutos, segs));
        binding.tvEstado.setText(isPausa ? getString(R.string.pausa) : getString(R.string.foco));
    }

    private void iniciarSesionEnBackend() {
        JsonObject body = new JsonObject();

        Call<RespuestaApi<PomodoroIniciarResponse>> call = repository.iniciar(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroIniciarResponse>> call, Response<RespuestaApi<PomodoroIniciarResponse>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    sesionId = response.body().getDatos().getSesionId();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroIniciarResponse>> call, Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    private void notificarCicloCompletado() {
        if (sesionId == -1) return;
        JsonObject body = new JsonObject();
        body.addProperty("ciclosCompletados", ciclosCompletados);

        Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call = repository.cicloCompletado(sesionId, body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call, Response<RespuestaApi<PomodoroCicloCompletadoResponse>> response) {
                activeCalls.remove(call);
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call, Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    private void finalizarSesion() {
        if (sesionId == -1) return;
        JsonObject body = new JsonObject();
        body.addProperty("ciclosCompletados", ciclosCompletados);

        Call<RespuestaApi<PomodoroFinalizarResponse>> call = repository.finalizar(sesionId, body);
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<PomodoroFinalizarResponse>>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroFinalizarResponse>> call, Response<RespuestaApi<PomodoroFinalizarResponse>> response) {
                activeCalls.remove(call);
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroFinalizarResponse>> call, Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    private void recargarTodo() {
        binding.loadingView.setVisibility(View.VISIBLE);
        cargarConfiguracion();
        cargarTipAleatorio();
        cargarRacha();
        verificarSesionActiva();
    }

    private void cargarTipAleatorio() {
        Call<RespuestaApi<PomodoroTipResponse>> call = repository.tipAleatorio();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroTipResponse>> call, Response<RespuestaApi<PomodoroTipResponse>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    String tip = response.body().getDatos().getConsejo();
                    if (tip != null && !tip.isEmpty()) {
                        binding.tvTip.setText(tip);
                        tipCargado = true;
                    }
                }
                if (!tipCargado) {
                    binding.tvTip.setText(R.string.pomodoro_tip_default);
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroTipResponse>> call, Throwable t) {
                activeCalls.remove(call);
                binding.tvTip.setText(R.string.pomodoro_tip_default);
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarConfiguracion() {
        Call<RespuestaApi<PomodoroConfiguracionResponse>> call = repository.configuracion();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroConfiguracionResponse>> call, Response<RespuestaApi<PomodoroConfiguracionResponse>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    PomodoroConfiguracionResponse cfg = response.body().getDatos();
                    tiempoFoco = cfg.getTiempoEstudio() * 60;
                    tiempoPausa = cfg.getTiempoDescanso() * 60;
                    tiempoPausaLarga = cfg.getTiempoDescansoLargo() * 60;
                    ciclosAntesPausaLarga = cfg.getCiclosAntesDescansoLargo();
                    if (!isRunning && !isPausa) {
                        segundosRestantes = tiempoFoco;
                        actualizarDisplay();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroConfiguracionResponse>> call, Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarRacha() {
        Call<RespuestaApi<PomodoroRachaResponse>> call = repository.racha();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroRachaResponse>> call, Response<RespuestaApi<PomodoroRachaResponse>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    binding.tvRacha.setText(String.valueOf(response.body().getDatos().getRacha()));
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroRachaResponse>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void mostrarErrorRed(Throwable t) {
        if (!isAdded()) return;
        Snackbar.make(requireView(),
                getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_SHORT).show();
    }

    private void verificarSesionActiva() {
        Call<RespuestaApi<PomodoroSesionActivaResponse>> call = repository.sesionActiva();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroSesionActivaResponse>> call, Response<RespuestaApi<PomodoroSesionActivaResponse>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    PomodoroSesionActivaResponse data = response.body().getDatos();
                    if (data.isActiva() && data.getSesionId() != null) {
                        sesionId = data.getSesionId();
                        if (data.getCiclosCompletados() != null) {
                            ciclosCompletados = data.getCiclosCompletados();
                            binding.tvCiclos.setText(getString(R.string.ciclos_formato, ciclosCompletados));
                            binding.tvSessionLabel.setText(getString(R.string.sesion_formato, ciclosCompletados));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroSesionActivaResponse>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        if (isRunning || sesionId != -1) {
            finalizarSesion();
        }
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) call.cancel();
        }
        activeCalls.clear();
        binding = null;
    }
}
