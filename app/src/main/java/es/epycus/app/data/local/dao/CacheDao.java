package es.epycus.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.epycus.app.data.local.entity.CacheEntity;

@Dao
public interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CacheEntity cache);

    @Query("SELECT value FROM cache WHERE `key` = :key")
    String getValue(String key);

    @Query("DELETE FROM cache WHERE `key` = :key")
    void delete(String key);

    @Query("DELETE FROM cache")
    void deleteAll();
}
