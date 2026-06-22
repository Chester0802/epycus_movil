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
    public String getNombreCurso() { return nombreCurso; }
    public String getFechaLimite() { return fechaLimite; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public int getXpOtorgado() { return xpOtorgado; }
    public String getFechaCreacion() { return fechaCreacion; }
    public int getCategoriaId() { return categoriaId; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public void setFechaLimite(String fechaLimite) { this.fechaLimite = fechaLimite; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setXpOtorgado(int xpOtorgado) { this.xpOtorgado = xpOtorgado; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public boolean isCompletada() { return "Completado".equals(estado); }
}
