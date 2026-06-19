package es.epycus.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.epycus.app.data.local.entity.ProgresoEntity;

@Dao
public interface ProgresoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProgresoEntity progreso);

    @Query("SELECT * FROM progresos WHERE id = :id")
    ProgresoEntity getById(int id);

    @Query("DELETE FROM progresos")
    void deleteAll();
}
