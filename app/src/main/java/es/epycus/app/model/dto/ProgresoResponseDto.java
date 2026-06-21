package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class ProgresoResponseDto {
    @SerializedName("progreso")
    private Object progreso;

    @SerializedName("nivelSiguiente")
    private Object nivelSiguiente;

    @SerializedName("xpParaSiguiente")
    private int xpParaSiguiente;

    @SerializedName("porcentaje")
    private double porcentaje;

    public Object getProgreso() { return progreso; }
    public Object getNivelSiguiente() { return nivelSiguiente; }
    public int getXpParaSiguiente() { return xpParaSiguiente; }
    public double getPorcentaje() { return porcentaje; }
}
