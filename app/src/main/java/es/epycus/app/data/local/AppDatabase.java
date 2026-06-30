package es.epycus.app.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
        version = 4, exportSchema = true)
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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Migración 2->3: tabla progresos ya existía en v2, no hay cambios de esquema
            // Si hubiera cambios, agregar ALTER TABLE aquí
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS write_back_queue (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "operationType TEXT NOT NULL," +
                    "endpoint TEXT NOT NULL," +
                    "requestBody TEXT NOT NULL," +
                    "createdAt INTEGER NOT NULL," +
                    "retryCount INTEGER NOT NULL DEFAULT 0," +
                    "status TEXT NOT NULL DEFAULT 'pending')");
        }
    };
}
