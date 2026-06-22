package es.epycus.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diario_entradas")
public class DiarioEntradaEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "estado")
    private String estado;

    @ColumnInfo(name = "nota")
    private String nota;

    public DiarioEntradaEntity(@NonNull String fecha, String estado, String nota) {
        this.fecha = fecha;
        this.estado = estado;
        this.nota = nota;
    }

    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public String getNota() { return nota; }
}
