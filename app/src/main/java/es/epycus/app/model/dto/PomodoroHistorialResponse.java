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
        @SerializedName("id")
        private int id;

        @SerializedName("fechaInicio")
        private String fechaInicio;

        @SerializedName("fechaFin")
        private String fechaFin;

        @SerializedName("ciclosCompletados")
        private int ciclosCompletados;

        @SerializedName("xpOtorgado")
        private int xpOtorgado;

        @SerializedName("fueCompletada")
        private boolean fueCompletada;

        @SerializedName("tipo")
        private String tipo;

        public int getId() { return id; }
        public String getFechaInicio() { return fechaInicio; }
        public String getFechaFin() { return fechaFin; }
        public int getCiclosCompletados() { return ciclosCompletados; }
        public int getXpOtorgado() { return xpOtorgado; }
        public boolean isFueCompletada() { return fueCompletada; }
        public String getTipo() { return tipo; }
    }
}
