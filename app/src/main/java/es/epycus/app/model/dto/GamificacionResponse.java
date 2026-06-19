package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class GamificacionResponse {
    @SerializedName("xpTotal")
    private int xpTotal;

    @SerializedName("nivel")
    private int nivel;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("rachaActual")
    private int rachaActual;

    @SerializedName("xpParaSiguienteNivel")
    private int xpParaSiguienteNivel;

    @SerializedName("porcentajeProgreso")
    private double porcentajeProgreso;

    @SerializedName("imagenPersonaje")
    private String imagenPersonaje;

    public int getXpTotal() { return xpTotal; }
    public int getNivel() { return nivel; }
    public String getTitulo() { return titulo; }
    public int getRachaActual() { return rachaActual; }
    public int getXpParaSiguienteNivel() { return xpParaSiguienteNivel; }
    public double getPorcentajeProgreso() { return porcentajeProgreso; }
    public String getImagenPersonaje() { return imagenPersonaje; }
}
