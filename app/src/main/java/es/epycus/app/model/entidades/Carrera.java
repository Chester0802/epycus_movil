package es.epycus.app.model.entidades;

import com.google.gson.annotations.SerializedName;

public class Carrera {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("area")
    private String area;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("estaActiva")
    private boolean estaActiva;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getArea() { return area; }
    public String getCodigo() { return codigo; }
    public boolean isEstaActiva() { return estaActiva; }
}
