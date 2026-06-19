package es.epycus.app.data.local.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.epycus.app.data.local.entity.HabitoEntity;

@Dao
public interface HabitoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<HabitoEntity> habitos);

    @Query("SELECT * FROM habitos WHERE esta_activo = 1")
    List<HabitoEntity> getActivos();

    @Query("DELETE FROM habitos")
    void deleteAll();
}
