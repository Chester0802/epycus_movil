package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class EstadoAnimoEntry {
    @SerializedName("fecha")
    private String fecha;

    @SerializedName("estado")
    private String estado;

    @SerializedName("nota")
    private String nota;

    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public String getNota() { return nota; }
}
