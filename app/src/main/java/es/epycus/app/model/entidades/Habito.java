package es.epycus.app.model.entidades;

import com.google.gson.annotations.SerializedName;

public class Habito {
    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("frecuencia")
    private String frecuencia;

    @SerializedName("conPomodoro")
    private boolean conPomodoro;

    @SerializedName("rachaActual")
    private int rachaActual;

    @SerializedName("rachaMaxima")
    private int rachaMaxima;

    @SerializedName("estaActivo")
    private boolean estaActivo;

    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    @SerializedName("categoriaId")
    private int categoriaId;

    @SerializedName("recordatorioHora")
    private String recordatorioHora;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getFrecuencia() { return frecuencia; }
    public boolean isConPomodoro() { return conPomodoro; }
    public int getRachaActual() { return rachaActual; }
    public int getRachaMaxima() { return rachaMaxima; }
    public boolean isEstaActivo() { return estaActivo; }
    public String getFechaCreacion() { return fechaCreacion; }
    public int getCategoriaId() { return categoriaId; }
    public String getRecordatorioHora() { return recordatorioHora; }
}
