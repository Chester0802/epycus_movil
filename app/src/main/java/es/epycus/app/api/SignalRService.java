package es.epycus.app.api;

import android.content.Context;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import es.epycus.app.BuildConfig;
import es.epycus.app.util.SessionManager;
import io.reactivex.rxjava3.core.Single;

public class SignalRService {
    private static final long RECONNECT_BASE_MS = 2_000L;   // primer reintento a los 2s
    private static final long RECONNECT_MAX_MS = 60_000L;   // tope de 60s entre reintentos

    private static SignalRService instance;
    private final HubConnection hubConnection;
    private final SessionManager sessionManager;
    private final SignalRListener listener;

    // Reconexión con backoff exponencial (el cliente Java de SignalR no la trae).
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean shouldReconnect = false;
    private volatile int reconnectAttempt = 0;
    private volatile int lastJoinedGroup = -1;

    public interface SignalRListener {
        void onPomodoroCicloCompletado(int xpGanado, boolean sugerirDescanso, String pausaActiva);
        void onPomodoroFinalizado(int xpTotal, boolean sesionGuardada);
        void onHabitoCompletado(int habitId, int xpGanado);
        void onMisionCompletada(int misionId, int xpGanado);
        void onNotificacionRecibida(String titulo, String mensaje, String tipo);
        void onEstadoCambio(String entidad, int entidadId, String nuevoEstado);
        void onConnectionStateChanged(boolean conectado);
        void onError(String error);
    }

    private SignalRService(Context context, SignalRListener listener) {
        this.sessionManager = SessionManager.getInstance(context);
        this.listener = listener;

        String hubUrl = BuildConfig.API_BASE_URL + "hubs/notificaciones";

        this.hubConnection = HubConnectionBuilder.create(hubUrl)
                .withAccessTokenProvider(Single.fromCallable(() -> sessionManager.getToken()))
                .build();

        hubConnection.on("RecibirAlerta", (tipo, mensaje, icono, esCritica, fecha) -> {
            if (listener != null) {
                listener.onNotificacionRecibida(tipo, mensaje, tipo);
            }
        }, String.class, String.class, String.class, boolean.class, String.class);

        hubConnection.onClosed(error -> {
            if (listener != null) {
                listener.onConnectionStateChanged(false);
            }
            if (error != null && listener != null) {
                listener.onError("Conexión cerrada: " + error.getMessage());
            }
            // Si el cierre no fue intencional, reintentar con backoff exponencial.
            if (shouldReconnect) {
                scheduleReconnect();
            }
        });
    }

    private void scheduleReconnect() {
        long delay = Math.min(RECONNECT_BASE_MS * (1L << Math.min(reconnectAttempt, 5)), RECONNECT_MAX_MS);
        // Jitter ±20% para evitar reconexiones sincronizadas (thundering herd).
        long jitter = (long) (delay * 0.2 * (Math.random() * 2 - 1));
        long finalDelay = Math.max(RECONNECT_BASE_MS, delay + jitter);
        reconnectAttempt++;
        reconnectScheduler.schedule(() -> {
            if (!shouldReconnect) return;
            if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                hubConnection.start()
                        .doOnComplete(() -> {
                            reconnectAttempt = 0;
                            if (listener != null) {
                                listener.onConnectionStateChanged(true);
                            }
                            // Reincorporarse al grupo tras reconectar (el servidor pierde la membresía).
                            if (lastJoinedGroup > 0) {
                                unirseAlGrupo(lastJoinedGroup);
                            }
                        })
                        .doOnError(err -> {
                            if (shouldReconnect) {
                                scheduleReconnect();
                            }
                        })
                        .subscribe(() -> {}, err -> {});
            }
        }, finalDelay, TimeUnit.MILLISECONDS);
    }

    public static synchronized SignalRService getInstance(Context context, SignalRListener listener) {
        if (instance == null) {
            instance = new SignalRService(context.getApplicationContext(), listener);
        }
        return instance;
    }

    public static synchronized SignalRService getInstance() {
        return instance;
    }

    public void start() {
        shouldReconnect = true;
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start()
                    .doOnComplete(() -> {
                        reconnectAttempt = 0;
                        if (listener != null) {
                            listener.onConnectionStateChanged(true);
                        }
                    })
                    .doOnError(error -> {
                        if (listener != null) {
                            listener.onError("Error al conectar SignalR: " + error.getMessage());
                        }
                        // Reintentar la conexión inicial fallida con backoff.
                        if (shouldReconnect) {
                            scheduleReconnect();
                        }
                    })
                    .subscribe(() -> {}, err -> {});
        }
    }

    public void iniciarConexion() {
        start();
    }

    public void stop() {
        shouldReconnect = false;   // cierre intencional: no reintentar
        if (hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
            hubConnection.stop();
        }
    }

    public void desconectar() {
        stop();
    }

    public void actualizarToken(String nuevoToken) {
        // Mantener shouldReconnect=true: al cerrarse, onClosed reconectará con el token nuevo
        // (el accessTokenProvider lee sessionManager.getToken() en cada conexión).
        shouldReconnect = true;
        if (hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
            hubConnection.stop();
        } else {
            start();
        }
    }

    public void unirseAlGrupo(int usuarioId) {
        lastJoinedGroup = usuarioId;   // se recuerda para reincorporarse tras una reconexión
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.invoke("UnirseAlGrupo", usuarioId)
                    .doOnError(error -> {
                        if (listener != null) {
                            listener.onError("Error al unirse al grupo: " + error.getMessage());
                        }
                    })
                    .subscribe(() -> {}, err -> {});
        }
    }

    public void abandonarGrupo(int usuarioId) {
        if (lastJoinedGroup == usuarioId) {
            lastJoinedGroup = -1;
        }
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.invoke("SalirDelGrupo", usuarioId)
                    .subscribe(() -> {}, err -> {});
        }
    }

    public HubConnectionState getConnectionState() {
        return hubConnection.getConnectionState();
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.stop();
            instance.reconnectScheduler.shutdownNow();
            instance = null;
        }
    }
}