package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PomodoroHistorialResponse {

    @SerializedName("historial")
    private List<SesionHistorial> historial;

    @SerializedName("pagina")
    private int pagina;

    @SerializedName("tamano")
    private int tamano;

    public List<SesionHistorial> getHistorial() { return historial; }
    public int getPagina() { return pagina; }
    public int getTamano() { return tamano; }

    public static class SesionHistorial {
        @SerializedName("fecha")
        private String fecha;

        @SerializedName("ciclos")
        private int ciclos;

        @SerializedName("duracionMinutos")
        private int duracionMinutos;

        public String getFecha() { return fecha; }
        public int getCiclos() { return ciclos; }
        public int getDuracionMinutos() { return duracionMinutos; }
    }
}
