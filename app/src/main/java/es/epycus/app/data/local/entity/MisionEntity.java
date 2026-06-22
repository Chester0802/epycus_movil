package es.epycus.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "misiones")
public class MisionEntity {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "nombre_curso")
    private String nombreCurso;

    @ColumnInfo(name = "prioridad")
    private String prioridad;

    @ColumnInfo(name = "estado")
    private String estado;

    @ColumnInfo(name = "fecha_limite")
    private String fechaLimite;

    @ColumnInfo(name = "xp_otorgado")
    private int xpOtorgado;

    @ColumnInfo(name = "fecha_creacion")
    private String fechaCreacion;

    @ColumnInfo(name = "categoria_id")
    private int categoriaId;

    public MisionEntity(int id, String nombre, String descripcion, String nombreCurso,
                        String prioridad, String estado, String fechaLimite,
                        int xpOtorgado, String fechaCreacion, int categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nombreCurso = nombreCurso;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaLimite = fechaLimite;
        this.xpOtorgado = xpOtorgado;
        this.fechaCreacion = fechaCreacion;
        this.categoriaId = categoriaId;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getNombreCurso() { return nombreCurso; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public String getFechaLimite() { return fechaLimite; }
    public int getXpOtorgado() { return xpOtorgado; }
    public String getFechaCreacion() { return fechaCreacion; }
    public int getCategoriaId() { return categoriaId; }
}
