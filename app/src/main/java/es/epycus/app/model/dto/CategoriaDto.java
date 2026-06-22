package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class CategoriaDto {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
}
