package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardResponse {
    @SerializedName("kpis")
    private Kpis kpis;

    @SerializedName("habitosPendientes")
    private int habitosPendientes;

    @SerializedName("misionesPendientes")
    private int misionesPendientes;

    @SerializedName("frase")
    private Frase frase;

    public Kpis getKpis() { return kpis; }
    public int getHabitosPendientes() { return habitosPendientes; }
    public int getMisionesPendientes() { return misionesPendientes; }
    public Frase getFrase() { return frase; }

    public static class Kpis {
        @SerializedName("habitosPendientes")
        private int habitosPendientes;

        @SerializedName("misionesPendientes")
        private int misionesPendientes;

        public int getHabitosPendientes() { return habitosPendientes; }
        public int getMisionesPendientes() { return misionesPendientes; }
    }

    public static class Frase {
        @SerializedName("frase")
        private String frase;

        @SerializedName("autor")
        private String autor;

        public String getFrase() { return frase; }
        public String getAutor() { return autor; }
    }
}
