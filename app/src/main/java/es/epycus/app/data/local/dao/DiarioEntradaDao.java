package es.epycus.app.data.local.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.epycus.app.data.local.entity.DiarioEntradaEntity;

@Dao
public interface DiarioEntradaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(DiarioEntradaEntity entrada);

    @Query("SELECT * FROM diario_entradas WHERE fecha = :fecha LIMIT 1")
    DiarioEntradaEntity getByFecha(String fecha);

    @Query("SELECT * FROM diario_entradas ORDER BY fecha DESC LIMIT 1")
    DiarioEntradaEntity getUltima();
}
