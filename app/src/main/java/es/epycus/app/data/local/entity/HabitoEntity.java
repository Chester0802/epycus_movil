package es.epycus.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habitos")
public class HabitoEntity {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "frecuencia")
    private String frecuencia;

    @ColumnInfo(name = "racha_actual")
    private int rachaActual;

    @ColumnInfo(name = "racha_maxima")
    private int rachaMaxima;

    @ColumnInfo(name = "esta_activo")
    private boolean estaActivo;

    @ColumnInfo(name = "categoria_id")
    private int categoriaId;

    public HabitoEntity(int id, String nombre, String descripcion, String frecuencia,
                        int rachaActual, int rachaMaxima, boolean estaActivo, int categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.frecuencia = frecuencia;
        this.rachaActual = rachaActual;
        this.rachaMaxima = rachaMaxima;
        this.estaActivo = estaActivo;
        this.categoriaId = categoriaId;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getFrecuencia() { return frecuencia; }
    public int getRachaActual() { return rachaActual; }
    public int getRachaMaxima() { return rachaMaxima; }
    public boolean isEstaActivo() { return estaActivo; }
    public int getCategoriaId() { return categoriaId; }
}
