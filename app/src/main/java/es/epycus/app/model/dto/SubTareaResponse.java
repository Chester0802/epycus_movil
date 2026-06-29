package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class SubTareaResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("misionId")
    private int misionId;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("estaCompletada")
    private boolean estaCompletada;

    @SerializedName("orden")
    private int orden;

    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    @SerializedName("tiempoEnfoqueSegundos")
    private int tiempoEnfoqueSegundos;

    @SerializedName("tiempoEnfoqueFormateado")
    private String tiempoEnfoqueFormateado;

    @SerializedName("fechaCompletado")
    private String fechaCompletado;

    public int getId() { return id; }
    public int getMisionId() { return misionId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isEstaCompletada() { return estaCompletada; }
    public int getOrden() { return orden; }
    public String getFechaCreacion() { return fechaCreacion; }
    public int getTiempoEnfoqueSegundos() { return tiempoEnfoqueSegundos; }
    public String getTiempoEnfoqueFormateado() { return tiempoEnfoqueFormateado; }
    public String getFechaCompletado() { return fechaCompletado; }
}