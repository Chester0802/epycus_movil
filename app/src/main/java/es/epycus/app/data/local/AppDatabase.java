package es.epycus.app.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.epycus.app.data.local.dao.CacheDao;
import es.epycus.app.data.local.dao.DiarioEntradaDao;
import es.epycus.app.data.local.dao.HabitoDao;
import es.epycus.app.data.local.dao.MisionDao;
import es.epycus.app.data.local.dao.ProgresoDao;
import es.epycus.app.data.local.dao.UsuarioDao;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.data.local.entity.DiarioEntradaEntity;
import es.epycus.app.data.local.entity.HabitoEntity;
import es.epycus.app.data.local.entity.MisionEntity;
import es.epycus.app.data.local.entity.ProgresoEntity;
import es.epycus.app.data.local.entity.UsuarioEntity;

@Database(entities = {UsuarioEntity.class, HabitoEntity.class, ProgresoEntity.class,
        CacheEntity.class, MisionEntity.class, DiarioEntradaEntity.class},
        version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    private static final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    public abstract UsuarioDao usuarioDao();
    public abstract HabitoDao habitoDao();
    public abstract ProgresoDao progresoDao();
    public abstract CacheDao cacheDao();
    public abstract MisionDao misionDao();
    public abstract DiarioEntradaDao diarioEntradaDao();

    public static ExecutorService getWriteExecutor() {
        return writeExecutor;
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "epycus_cache")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
