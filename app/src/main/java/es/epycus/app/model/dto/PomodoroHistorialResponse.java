package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroHistorialResponse {
    @SerializedName("historial")
    private Object historial;

    @SerializedName("pagina")
    private int pagina;

    @SerializedName("tamano")
    private int tamano;

    public Object getHistorial() { return historial; }
    public int getPagina() { return pagina; }
    public int getTamano() { return tamano; }
}
