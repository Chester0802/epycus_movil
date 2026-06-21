package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroConfiguracionResponse {
    @SerializedName("tiempoEstudio")
    private int tiempoEstudio;

    @SerializedName("tiempoDescanso")
    private int tiempoDescanso;

    @SerializedName("tiempoDescansoLargo")
    private int tiempoDescansoLargo;

    @SerializedName("ciclosAntesDescansoLargo")
    private int ciclosAntesDescansoLargo;

    @SerializedName("sonidoActivo")
    private boolean sonidoActivo;

    @SerializedName("sonidoSeleccionado")
    private String sonidoSeleccionado;

    @SerializedName("volumen")
    private double volumen;

    @SerializedName("autoIniciarDescanso")
    private boolean autoIniciarDescanso;

    @SerializedName("autoIniciarEnfoque")
    private boolean autoIniciarEnfoque;

    @SerializedName("ticTacActivo")
    private boolean ticTacActivo;

    @SerializedName("metaDiariaCiclos")
    private int metaDiariaCiclos;

    @SerializedName("modoPersonalizadoMinutos")
    private Integer modoPersonalizadoMinutos;

    @SerializedName("vibracionActiva")
    private boolean vibracionActiva;

    @SerializedName("notificacionDesktop")
    private boolean notificacionDesktop;

    public int getTiempoEstudio() { return tiempoEstudio; }
    public int getTiempoDescanso() { return tiempoDescanso; }
    public int getTiempoDescansoLargo() { return tiempoDescansoLargo; }
    public int getCiclosAntesDescansoLargo() { return ciclosAntesDescansoLargo; }
    public boolean isSonidoActivo() { return sonidoActivo; }
    public String getSonidoSeleccionado() { return sonidoSeleccionado; }
    public double getVolumen() { return volumen; }
    public boolean isAutoIniciarDescanso() { return autoIniciarDescanso; }
    public boolean isAutoIniciarEnfoque() { return autoIniciarEnfoque; }
    public boolean isTicTacActivo() { return ticTacActivo; }
    public int getMetaDiariaCiclos() { return metaDiariaCiclos; }
    public Integer getModoPersonalizadoMinutos() { return modoPersonalizadoMinutos; }
    public boolean isVibracionActiva() { return vibracionActiva; }
    public boolean isNotificacionDesktop() { return notificacionDesktop; }
}
