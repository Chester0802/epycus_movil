package es.epycus.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import es.epycus.app.data.local.dao.CacheDao;
import es.epycus.app.data.local.dao.HabitoDao;
import es.epycus.app.data.local.dao.ProgresoDao;
import es.epycus.app.data.local.dao.UsuarioDao;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.data.local.entity.HabitoEntity;
import es.epycus.app.data.local.entity.ProgresoEntity;
import es.epycus.app.data.local.entity.UsuarioEntity;

@Database(entities = {UsuarioEntity.class, HabitoEntity.class, ProgresoEntity.class, CacheEntity.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UsuarioDao usuarioDao();
    public abstract HabitoDao habitoDao();
    public abstract ProgresoDao progresoDao();
    public abstract CacheDao cacheDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "epycus_cache")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
