package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class CrearSubTareaDto {
    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("orden")
    private int orden;

    public CrearSubTareaDto(String nombre, String descripcion, int orden) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getOrden() { return orden; }
}