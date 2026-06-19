package es.epycus.app.model.entidades;

import com.google.gson.annotations.SerializedName;

public class ProgresoUsuario {
    @SerializedName("id")
    private int id;

    @SerializedName("xpTotal")
    private int xpTotal;

    @SerializedName("rachaActual")
    private int rachaActual;

    @SerializedName("rachaMaxima")
    private int rachaMaxima;

    @SerializedName("fechaUltimaActividad")
    private String fechaUltimaActividad;

    @SerializedName("fechaInicioRacha")
    private String fechaInicioRacha;

    @SerializedName("diaDeGraciaUsado")
    private boolean diaDeGraciaUsado;

    @SerializedName("productividadDiaria")
    private double productividadDiaria;

    @SerializedName("nivelActualId")
    private int nivelActualId;

    public int getId() { return id; }
    public int getXpTotal() { return xpTotal; }
    public int getRachaActual() { return rachaActual; }
    public int getRachaMaxima() { return rachaMaxima; }
    public String getFechaUltimaActividad() { return fechaUltimaActividad; }
    public String getFechaInicioRacha() { return fechaInicioRacha; }
    public boolean isDiaDeGraciaUsado() { return diaDeGraciaUsado; }
    public double getProductividadDiaria() { return productividadDiaria; }
    public int getNivelActualId() { return nivelActualId; }
}
