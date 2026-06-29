package es.epycus.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import es.epycus.app.data.local.entity.WriteBackEntity;

import java.util.List;

@Dao
public interface WriteBackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(WriteBackEntity entity);

    @Update
    void update(WriteBackEntity entity);

    @Delete
    void delete(WriteBackEntity entity);

    @Query("SELECT * FROM write_back_queue WHERE status = 'pending' ORDER BY createdAt ASC")
    List<WriteBackEntity> getPending();

    @Query("SELECT COUNT(*) FROM write_back_queue WHERE status = 'pending'")
    int getPendingCount();

    @Query("DELETE FROM write_back_queue WHERE status = 'failed'")
    int deleteFailed();

    @Query("DELETE FROM write_back_queue")
    void deleteAll();
}