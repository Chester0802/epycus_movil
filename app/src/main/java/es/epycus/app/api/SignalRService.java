package es.epycus.app.api;

import android.content.Context;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import es.epycus.app.BuildConfig;
import es.epycus.app.util.SessionManager;
import io.reactivex.rxjava3.core.Single;

public class SignalRService {
    private static SignalRService instance;
    private final HubConnection hubConnection;
    private final SessionManager sessionManager;
    private final SignalRListener listener;

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
        });
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
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start()
                    .doOnError(error -> {
                        if (listener != null) {
                            listener.onError("Error al conectar SignalR: " + error.getMessage());
                        }
                    })
                    .subscribe();
        }
    }

    public void iniciarConexion() {
        start();
    }

    public void stop() {
        if (hubConnection.getConnectionState() != HubConnectionState.DISCONNECTED) {
            hubConnection.stop();
        }
    }

    public void desconectar() {
        stop();
    }

    public void actualizarToken(String nuevoToken) {
        hubConnection.stop();
        start();
    }

    public void unirseAlGrupo(int usuarioId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.invoke("UnirseAlGrupo", usuarioId)
                    .doOnError(error -> {
                        if (listener != null) {
                            listener.onError("Error al unirse al grupo: " + error.getMessage());
                        }
                    })
                    .subscribe();
        }
    }

    public void abandonarGrupo(int usuarioId) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.invoke("SalirDelGrupo", usuarioId)
                    .subscribe();
        }
    }

    public HubConnectionState getConnectionState() {
        return hubConnection.getConnectionState();
    }

    public static void resetInstance() {
        if (instance != null) {
            instance.stop();
            instance = null;
        }
    }
}