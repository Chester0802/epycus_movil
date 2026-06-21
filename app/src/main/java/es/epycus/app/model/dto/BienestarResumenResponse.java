package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class BienestarResumenResponse {
    @SerializedName("alertas")
    private Object alertas;

    @SerializedName("frase")
    private Object frase;

    @SerializedName("estadoHoy")
    private Object estadoHoy;

    @SerializedName("habitosPendientes")
    private int habitosPendientes;

    @SerializedName("misionesPendientes")
    private int misionesPendientes;

    public Object getAlertas() { return alertas; }
    public Object getFrase() { return frase; }
    public Object getEstadoHoy() { return estadoHoy; }
    public int getHabitosPendientes() { return habitosPendientes; }
    public int getMisionesPendientes() { return misionesPendientes; }
}
