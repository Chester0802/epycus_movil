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

    @ColumnInfo(name = "estado_animo")
    private int estadoAnimo;

    @ColumnInfo(name = "diario_texto")
    private String diarioTexto;

    public DiarioEntradaEntity(@NonNull String fecha, int estadoAnimo, String diarioTexto) {
        this.fecha = fecha;
        this.estadoAnimo = estadoAnimo;
        this.diarioTexto = diarioTexto;
    }

    public String getFecha() { return fecha; }
    public int getEstadoAnimo() { return estadoAnimo; }
    public String getDiarioTexto() { return diarioTexto; }
}
