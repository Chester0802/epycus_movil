package es.epycus.app.data.local.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.epycus.app.data.local.entity.MisionEntity;

@Dao
public interface MisionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MisionEntity> misiones);

    @Query("SELECT * FROM misiones")
    List<MisionEntity> getAll();

    @Query("SELECT * FROM misiones WHERE estado != 'Completado'")
    List<MisionEntity> getActivas();

    @Query("DELETE FROM misiones")
    void deleteAll();
}
