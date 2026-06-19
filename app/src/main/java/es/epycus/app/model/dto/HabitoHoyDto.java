package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class HabitoHoyDto {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("estadoHoy")
    private String estadoHoy;

    @SerializedName("xpPotencial")
    private int xpPotencial;

    @SerializedName("categoria")
    private String categoria;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEstadoHoy() { return estadoHoy; }
    public int getXpPotencial() { return xpPotencial; }
    public String getCategoria() { return categoria; }

    public boolean isCompletado() { return "Completado".equals(estadoHoy); }
    public boolean isFallado() { return "Fallado".equals(estadoHoy); }
    public boolean isPendiente() { return "Pendiente".equals(estadoHoy); }
}
