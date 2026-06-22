package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class LogroUsuarioItem {
    @SerializedName("id")
    private int id;

    @SerializedName("fechaObtenido")
    private String fechaObtenido;

    @SerializedName("logro")
    private LogroItem logro;

    public int getId() { return id; }
    public String getFechaObtenido() { return fechaObtenido; }
    public LogroItem getLogro() { return logro; }
}
