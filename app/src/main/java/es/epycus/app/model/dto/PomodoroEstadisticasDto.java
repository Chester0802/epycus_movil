package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PomodoroEstadisticasDto {

    @SerializedName("totalSesiones")
    private int totalSesiones;

    @SerializedName("totalMinutosFoco")
    private int totalMinutosFoco;

    @SerializedName("promedioDiario")
    private double promedioDiario;

    @SerializedName("rachaActual")
    private int rachaActual;

    @SerializedName("mejorRacha")
    private int mejorRacha;

    @SerializedName("historialDiario")
    private List<EstadisticaDiaria> historialDiario;

    @SerializedName("historialSemanal")
    private List<EstadisticaSemanal> historialSemanal;

    public int getTotalSesiones() { return totalSesiones; }
    public int getTotalMinutosFoco() { return totalMinutosFoco; }
    public double getPromedioDiario() { return promedioDiario; }
    public int getRachaActual() { return rachaActual; }
    public int getMejorRacha() { return mejorRacha; }
    public List<EstadisticaDiaria> getHistorialDiario() { return historialDiario; }
    public List<EstadisticaSemanal> getHistorialSemanal() { return historialSemanal; }

    public static class EstadisticaDiaria {
        @SerializedName("fecha")
        private String fecha;

        @SerializedName("ciclos")
        private int ciclos;

        @SerializedName("minutosFoco")
        private int minutosFoco;

        public String getFecha() { return fecha; }
        public int getCiclos() { return ciclos; }
        public int getMinutosFoco() { return minutosFoco; }
    }

    public static class EstadisticaSemanal {
        @SerializedName("semana")
        private int semana;

        @SerializedName("ano")
        private int ano;

        @SerializedName("ciclos")
        private int ciclos;

        @SerializedName("minutosFoco")
        private int minutosFoco;

        public int getSemana() { return semana; }
        public int getAno() { return ano; }
        public int getCiclos() { return ciclos; }
        public int getMinutosFoco() { return minutosFoco; }
    }
}
