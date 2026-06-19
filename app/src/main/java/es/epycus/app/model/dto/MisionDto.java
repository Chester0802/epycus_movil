package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class MisionDto {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("nombreCurso")
    private String nombreCurso;

    @SerializedName("fechaLimite")
    private String fechaLimite;

    @SerializedName("prioridad")
    private String prioridad;

    @SerializedName("estado")
    private String estado;

    @SerializedName("xpOtorgado")
    private int xpOtorgado;

    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    @SerializedName("categoriaId")
    private int categoriaId;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getFechaLimite() { return fechaLimite; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public int getXpOtorgado() { return xpOtorgado; }
    public String getFechaCreacion() { return fechaCreacion; }

    public boolean isCompletada() { return "Completada".equals(estado); }
}
