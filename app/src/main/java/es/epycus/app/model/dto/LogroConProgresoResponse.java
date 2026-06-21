package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class LogroConProgresoResponse {
    @SerializedName("logro")
    private Object logro;

    @SerializedName("desbloqueado")
    private boolean desbloqueado;

    @SerializedName("progreso")
    private int progreso;

    @SerializedName("meta")
    private int meta;

    public Object getLogro() { return logro; }
    public boolean isDesbloqueado() { return desbloqueado; }
    public int getProgreso() { return progreso; }
    public int getMeta() { return meta; }
}
