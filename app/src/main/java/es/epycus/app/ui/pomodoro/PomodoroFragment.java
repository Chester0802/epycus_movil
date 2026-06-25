package es.epycus.app.ui.pomodoro;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.epycus.app.model.dto.PomodoroHistorialResponse.SesionHistorial;

import es.epycus.app.R;
import es.epycus.app.databinding.FragmentPomodoroBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.repository.PomodoroRepository;
import es.epycus.app.util.CacheManager;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.model.dto.PomodoroIniciarResponse;
import es.epycus.app.model.dto.PomodoroCicloCompletadoResponse;
import es.epycus.app.model.dto.PomodoroTipResponse;
import es.epycus.app.model.dto.PomodoroConfiguracionResponse;
import es.epycus.app.model.dto.PomodoroSesionActivaResponse;
import es.epycus.app.model.dto.PomodoroFinalizarResponse;
import es.epycus.app.model.dto.PomodoroRachaResponse;
import es.epycus.app.model.dto.PomodoroHistorialResponse;
import es.epycus.app.model.dto.SuccessResponseDto;
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
    private boolean sesionFinalizada = false;
    private int ciclosCompletadosSesion = 0;
    private static final String CHANNEL_ID = "pomodoro_channel";
    private static final int NOTIFICATION_ID_CICLO = 1001;
    private static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 100;
    private boolean notificacionesPermitidas = false;
    private static final String CACHE_KEY_CONFIG = "pomodoro_config";
    private static final String CACHE_KEY_TIP = "pomodoro_tip";
    private boolean sonidoActivo = true;
    private boolean vibracionActiva = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        repository = new PomodoroRepository(requireContext());

        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (!isRunning) {
                recargarTodo();
            } else {
                binding.swipeRefresh.setRefreshing(false);
            }
        });
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);
        binding.progressIndicator.setMax(1000);

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

        binding.btnConfig.setOnClickListener(v -> showConfigDialog());
        binding.btnHistorial.setOnClickListener(v -> showHistorialDialog());
        actualizarBoton();

        if (isRunning) {
            reanudarTimer();
        }

        recargarTodo();

        solicitarPermisoNotificacion();

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
                    mostrarNotificacion(false);
                    if (sonidoActivo) reproducirSonido();
                    if (vibracionActiva) vibrar();
                } else {
                    ciclosCompletados++;
                    ciclosCompletadosSesion++;
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
                    mostrarNotificacion(true);
                    if (sonidoActivo) reproducirSonido();
                    if (vibracionActiva) vibrar();
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

    private int getTiempoActual() {
        if (isPausa && ciclosCompletados > 0 && ciclosCompletados % ciclosAntesPausaLarga == 0) {
            return tiempoPausaLarga;
        } else if (isPausa) {
            return tiempoPausa;
        }
        return tiempoFoco;
    }

    private void actualizarDisplay() {
        int minutos = segundosRestantes / 60;
        int segs = segundosRestantes % 60;
        binding.tvTiempo.setText(String.format("%02d:%02d", minutos, segs));
        binding.tvEstado.setText(isPausa ? getString(R.string.pausa) : getString(R.string.foco));
        int total = getTiempoActual();
        if (total > 0) {
            int progress = (int) ((long) segundosRestantes * 1000 / total);
            binding.progressIndicator.setProgress(progress);
        }
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
        sesionFinalizada = true;
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

    private void cancelarSesion() {
        if (sesionId == -1) return;

        Call<RespuestaApi<SuccessResponseDto>> call = repository.cancelar(sesionId);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<SuccessResponseDto>> call, Response<RespuestaApi<SuccessResponseDto>> response) {
                activeCalls.remove(call);
            }

            @Override
            public void onFailure(Call<RespuestaApi<SuccessResponseDto>> call, Throwable t) {
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
        String cached = repository.getCachedJson(CACHE_KEY_TIP);
        if (cached != null) {
            try {
                RespuestaApi<PomodoroTipResponse> cachedResp =
                        new Gson().fromJson(cached, new com.google.gson.reflect.TypeToken<RespuestaApi<PomodoroTipResponse>>(){}.getType());
                if (cachedResp != null && cachedResp.getDatos() != null) {
                    String tip = cachedResp.getDatos().getConsejo();
                    if (tip != null && !tip.isEmpty()) {
                        binding.tvTip.setText(tip);
                        tipCargado = true;
                    }
                }
            } catch (Exception ignored) {}
        }

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
                        try {
                            String json = new Gson().toJson(response.body());
                            repository.cacheJson(CACHE_KEY_TIP, json, CacheManager.TTL_DIARIO);
                        } catch (Exception ignored) {}
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

    private void aplicarConfiguracion(PomodoroConfiguracionResponse cfg) {
        tiempoFoco = cfg.getTiempoEstudio() * 60;
        tiempoPausa = cfg.getTiempoDescanso() * 60;
        tiempoPausaLarga = cfg.getTiempoDescansoLargo() * 60;
        ciclosAntesPausaLarga = cfg.getCiclosAntesDescansoLargo();
        sonidoActivo = cfg.isSonidoActivo();
        vibracionActiva = cfg.isVibracionActiva();
        if (!isRunning && !isPausa) {
            segundosRestantes = tiempoFoco;
            actualizarDisplay();
        }
    }

    private void cargarConfiguracion() {
        String cached = repository.getCachedJson(CACHE_KEY_CONFIG);
        if (cached != null) {
            try {
                RespuestaApi<PomodoroConfiguracionResponse> cachedResp =
                        new Gson().fromJson(cached, new com.google.gson.reflect.TypeToken<RespuestaApi<PomodoroConfiguracionResponse>>(){}.getType());
                if (cachedResp != null && cachedResp.getDatos() != null) {
                    aplicarConfiguracion(cachedResp.getDatos());
                }
            } catch (Exception ignored) {}
        }

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
                    aplicarConfiguracion(cfg);
                    try {
                        String json = new Gson().toJson(response.body());
                        repository.cacheJson(CACHE_KEY_CONFIG, json, CacheManager.TTL_POMODORO_CONFIG);
                    } catch (Exception ignored) {}
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

    private void reproducirSonido() {
        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(requireContext(), uri);
            if (ringtone != null) {
                ringtone.play();
            }
        } catch (Exception ignored) {}
    }

    private void vibrar() {
        Context context = getContext();
        if (context == null) return;
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            if (vm == null) return;
            vibrator = vm.getDefaultVibrator();
        } else {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(300);
        }
    }

    private void mostrarNotificacion(boolean isFoco) {
        Context context = getContext();
        if (context == null) return;

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.canal_pomodoro),
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }

        String title = getString(isFoco ? R.string.foco_completado_notif : R.string.pausa_completada_notif);
        String body = getString(R.string.sesion_formato, ciclosCompletados);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificacionesPermitidas) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pomodoro)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        nm.notify(NOTIFICATION_ID_CICLO, builder.build());
    }

    private void solicitarPermisoNotificacion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            notificacionesPermitidas = true;
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificacionesPermitidas = true;
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
            Snackbar.make(binding.getRoot(), R.string.notif_pomodoro_permiso_razon, Snackbar.LENGTH_LONG)
                    .setAction(R.string.permitir, v ->
                            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                    PERMISSION_REQUEST_POST_NOTIFICATIONS))
                    .show();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_POST_NOTIFICATIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notificacionesPermitidas = true;
            }
        }
    }

    private void showConfigDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_pomodoro_config, null);
        com.google.android.material.textfield.TextInputEditText etTiempoEstudio = view.findViewById(R.id.etTiempoEstudio);
        com.google.android.material.textfield.TextInputEditText etTiempoDescanso = view.findViewById(R.id.etTiempoDescanso);
        com.google.android.material.textfield.TextInputEditText etTiempoDescansoLargo = view.findViewById(R.id.etTiempoDescansoLargo);
        com.google.android.material.textfield.TextInputEditText etCiclosAntesLargo = view.findViewById(R.id.etCiclosAntesLargo);

        etTiempoEstudio.setText(String.valueOf(tiempoFoco / 60));
        etTiempoDescanso.setText(String.valueOf(tiempoPausa / 60));
        etTiempoDescansoLargo.setText(String.valueOf(tiempoPausaLarga / 60));
        etCiclosAntesLargo.setText(String.valueOf(ciclosAntesPausaLarga));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.config_pomodoro);
        builder.setView(view);
        builder.setPositiveButton(R.string.guardar, null);
        builder.setNegativeButton(R.string.cancelar, null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String sEstudio = etTiempoEstudio.getText().toString().trim();
                String sDescanso = etTiempoDescanso.getText().toString().trim();
                String sDescansoLargo = etTiempoDescansoLargo.getText().toString().trim();
                String sCiclos = etCiclosAntesLargo.getText().toString().trim();

                if (sEstudio.isEmpty() || sDescanso.isEmpty() || sDescansoLargo.isEmpty() || sCiclos.isEmpty()) {
                    Snackbar.make(requireView(), R.string.config_valor_invalido, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                int estudio = Integer.parseInt(sEstudio);
                int descanso = Integer.parseInt(sDescanso);
                int descansoLargo = Integer.parseInt(sDescansoLargo);
                int ciclos = Integer.parseInt(sCiclos);

                if (estudio <= 0 || descanso <= 0 || descansoLargo <= 0 || ciclos <= 0) {
                    Snackbar.make(requireView(), R.string.config_valor_invalido, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                JsonObject body = new JsonObject();
                body.addProperty("tiempoEstudio", estudio);
                body.addProperty("tiempoDescanso", descanso);
                body.addProperty("tiempoDescansoLargo", descansoLargo);
                body.addProperty("ciclosAntesDescansoLargo", ciclos);

                Call<RespuestaApi<SuccessResponseDto>> call = repository.actualizarConfiguracion(body);
                activeCalls.add(call);
                call.enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<SuccessResponseDto>> call, Response<RespuestaApi<SuccessResponseDto>> response) {
                        activeCalls.remove(call);
                        if (response.isSuccessful()) {
                            tiempoFoco = estudio * 60;
                            tiempoPausa = descanso * 60;
                            tiempoPausaLarga = descansoLargo * 60;
                            ciclosAntesPausaLarga = ciclos;
                            if (!isRunning && !isPausa) {
                                segundosRestantes = tiempoFoco;
                                actualizarDisplay();
                            }
                            dialog.dismiss();
                            Snackbar.make(requireView(), R.string.config_guardada, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(requireView(), R.string.config_error, Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaApi<SuccessResponseDto>> call, Throwable t) {
                        activeCalls.remove(call);
                        mostrarErrorRed(t);
                    }
                });
            });
        });
        dialog.show();
    }

    private void showHistorialDialog() {
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        Call<RespuestaApi<PomodoroHistorialResponse>> call = repository.historial(hoy, hoy, 0, 50);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<PomodoroHistorialResponse>> call, Response<RespuestaApi<PomodoroHistorialResponse>> response) {
                activeCalls.remove(call);
                if (!isAdded()) return;

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.historial_titulo);

                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    PomodoroHistorialResponse data = response.body().getDatos();
                    List<SesionHistorial> historial = data.getHistorial();

                    if (historial != null && !historial.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (SesionHistorial item : historial) {
                            String fecha = item.getFechaInicio() != null ? item.getFechaInicio().substring(0, 10) : "—";
                            int minutos = calcularDuracionMinutos(item.getFechaInicio(), item.getFechaFin());
                            String linea = getString(R.string.historial_formato, fecha, item.getCiclosCompletados(), minutos);
                            sb.append(linea).append("\n");
                        }
                        builder.setMessage(sb.toString().trim());
                    } else {
                        builder.setMessage(R.string.historial_vacio);
                    }
                } else {
                    builder.setMessage(R.string.historial_vacio);
                }

                builder.setPositiveButton(R.string.ok, null);
                builder.show();
            }

            @Override
            public void onFailure(Call<RespuestaApi<PomodoroHistorialResponse>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private int calcularDuracionMinutos(String inicio, String fin) {
        if (inicio == null || fin == null) return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date fechaInicio = sdf.parse(inicio.length() > 19 ? inicio.substring(0, 19) : inicio);
            Date fechaFin = sdf.parse(fin.length() > 19 ? fin.substring(0, 19) : fin);
            return (int) TimeUnit.MILLISECONDS.toMinutes(fechaFin.getTime() - fechaInicio.getTime());
        } catch (Exception e) {
            return 0;
        }
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
        if (sesionId != -1 && !sesionFinalizada) {
            if (ciclosCompletadosSesion > 0) {
                finalizarSesion();
            } else {
                cancelarSesion();
            }
        }
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) call.cancel();
        }
        activeCalls.clear();
        binding = null;
    }
}
