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
import es.epycus.app.data.local.dao.WriteBackDao;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.data.local.entity.DiarioEntradaEntity;
import es.epycus.app.data.local.entity.HabitoEntity;
import es.epycus.app.data.local.entity.MisionEntity;
import es.epycus.app.data.local.entity.ProgresoEntity;
import es.epycus.app.data.local.entity.UsuarioEntity;
import es.epycus.app.data.local.entity.WriteBackEntity;

@Database(entities = {UsuarioEntity.class, HabitoEntity.class, ProgresoEntity.class,
        CacheEntity.class, MisionEntity.class, DiarioEntradaEntity.class, WriteBackEntity.class},
        version = 5, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    private static final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    public abstract UsuarioDao usuarioDao();
    public abstract HabitoDao habitoDao();
    public abstract ProgresoDao progresoDao();
    public abstract CacheDao cacheDao();
    public abstract MisionDao misionDao();
    public abstract DiarioEntradaDao diarioEntradaDao();
    public abstract WriteBackDao writeBackDao();

    public static ExecutorService getWriteExecutor() {
        return writeExecutor;
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "epycus_cache")
                    // Esta BD es un CACHE (los datos se re-piden al servidor) y la cola
                    // write-back es transitoria. Ante cualquier cambio de esquema o
                    // migración incompleta, recrear la BD es preferible a crashear.
                    // (Los tokens NO están aquí; viven en EncryptedSharedPreferences.)
                    // Versiones previas tenían columnas extra en 'usuarios' (token,
                    // refresh_token, fecha_registro) que se quitaron sin migración -> esto
                    // evita el "Migration didn't properly handle: usuarios" al actualizar.
                    .fallbackToDestructiveMigration()
                    // La UI offline-first lee el cache de forma síncrona en onCreateView
                    // (getCachedMisiones/getActivos/getById/CacheManager.get) para pintar
                    // datos al instante antes de refrescar desde la red. Al ser una BD de
                    // cache con consultas diminutas, se permiten lecturas en el hilo
                    // principal (las escrituras siguen yendo por getWriteExecutor()).
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
