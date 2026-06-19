package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class RecomendacionPausaDto {
    @SerializedName("tipo")
    private String tipo;

    @SerializedName("duracionSegundos")
    private int duracionSegundos;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("icono")
    private String icono;

    public String getTipo() { return tipo; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public String getDescripcion() { return descripcion; }
    public String getIcono() { return icono; }
}
