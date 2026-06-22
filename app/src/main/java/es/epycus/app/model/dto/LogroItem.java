package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class LogroItem {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("iconoUrl")
    private String iconoUrl;

    @SerializedName("condicionTipo")
    private String condicionTipo;

    @SerializedName("condicionValor")
    private int condicionValor;

    @SerializedName("xpRecompensa")
    private int xpRecompensa;

    @SerializedName("estaActivo")
    private boolean estaActivo;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getIconoUrl() { return iconoUrl; }
    public String getCondicionTipo() { return condicionTipo; }
    public int getCondicionValor() { return condicionValor; }
    public int getXpRecompensa() { return xpRecompensa; }
    public boolean isEstaActivo() { return estaActivo; }
}
