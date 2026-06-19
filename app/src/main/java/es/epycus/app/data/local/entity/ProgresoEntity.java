package es.epycus.app.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progresos")
public class ProgresoEntity {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "xp_total")
    private int xpTotal;

    @ColumnInfo(name = "racha_actual")
    private int rachaActual;

    @ColumnInfo(name = "racha_maxima")
    private int rachaMaxima;

    @ColumnInfo(name = "nivel_actual_id")
    private int nivelActualId;

    @ColumnInfo(name = "fecha_ultima_actividad")
    private String fechaUltimaActividad;

    public ProgresoEntity(int id, int xpTotal, int rachaActual, int rachaMaxima,
                          int nivelActualId, String fechaUltimaActividad) {
        this.id = id;
        this.xpTotal = xpTotal;
        this.rachaActual = rachaActual;
        this.rachaMaxima = rachaMaxima;
        this.nivelActualId = nivelActualId;
        this.fechaUltimaActividad = fechaUltimaActividad;
    }

    public int getId() { return id; }
    public int getXpTotal() { return xpTotal; }
    public int getRachaActual() { return rachaActual; }
    public int getRachaMaxima() { return rachaMaxima; }
    public int getNivelActualId() { return nivelActualId; }
    public String getFechaUltimaActividad() { return fechaUltimaActividad; }
}
