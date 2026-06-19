package es.epycus.app.model.entidades;

import com.google.gson.annotations.SerializedName;

public class Nivel {
    @SerializedName("id")
    private int id;

    @SerializedName("numero")
    private int numero;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("xpRequerido")
    private int xpRequerido;

    @SerializedName("descripcion")
    private String descripcion;

    public int getId() { return id; }
    public int getNumero() { return numero; }
    public String getTitulo() { return titulo; }
    public int getXpRequerido() { return xpRequerido; }
    public String getDescripcion() { return descripcion; }
}
