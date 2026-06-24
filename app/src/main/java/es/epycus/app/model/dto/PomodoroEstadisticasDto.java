package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroEstadisticasDto {

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("ciclos")
    private int ciclos;

    @SerializedName("minutos")
    private int minutos;

    @SerializedName("xp")
    private int xp;

    public String getFecha() { return fecha; }
    public int getCiclos() { return ciclos; }
    public int getMinutos() { return minutos; }
    public int getXp() { return xp; }
}