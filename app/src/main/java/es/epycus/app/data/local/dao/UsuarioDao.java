package es.epycus.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.epycus.app.data.local.entity.UsuarioEntity;

@Dao
public interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UsuarioEntity usuario);

    @Query("SELECT * FROM usuarios WHERE id = :userId")
    UsuarioEntity getById(int userId);

    @Query("DELETE FROM usuarios")
    void deleteAll();
}
