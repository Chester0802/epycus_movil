package es.epycus.app.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import es.epycus.app.data.local.dao.WriteBackDao;
import es.epycus.app.data.local.entity.WriteBackEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

/**
 * Tests de la cola de operaciones offline (write_back_queue) sobre Room en memoria.
 * Cubre la persistencia que usan {@code SyncWorker} y {@code PomodoroRepository}.
 */
@RunWith(RobolectricTestRunner.class)
public class WriteBackDaoTest {

    private AppDatabase db;
    private WriteBackDao dao;

    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.writeBackDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    private WriteBackEntity nuevo(String tipo, String endpoint, long createdAt, String status) {
        WriteBackEntity e = new WriteBackEntity();
        e.operationType = tipo;
        e.endpoint = endpoint;
        e.requestBody = "{}";
        e.createdAt = createdAt;
        e.retryCount = 0;
        e.status = status;
        return e;
    }

    @Test
    public void insert_y_getPending_devuelveLoEncolado() {
        dao.insert(nuevo("ciclo_completado", "/api/v1/pomodoro/1/ciclo-completado", 100, "pending"));

        List<WriteBackEntity> pendientes = dao.getPending();

        assertEquals(1, pendientes.size());
        assertEquals("ciclo_completado", pendientes.get(0).operationType);
        assertEquals(1, dao.getPendingCount());
    }

    @Test
    public void getPending_excluyeLosNoPendientes() {
        dao.insert(nuevo("a", "/a", 100, "pending"));
        dao.insert(nuevo("b", "/b", 200, "failed"));

        List<WriteBackEntity> pendientes = dao.getPending();

        assertEquals(1, pendientes.size());
        assertEquals("a", pendientes.get(0).operationType);
        assertEquals(1, dao.getPendingCount());
    }

    @Test
    public void getPending_ordenadoPorCreatedAtAscendente() {
        dao.insert(nuevo("tardio", "/2", 500, "pending"));
        dao.insert(nuevo("temprano", "/1", 100, "pending"));

        List<WriteBackEntity> pendientes = dao.getPending();

        assertEquals("temprano", pendientes.get(0).operationType);
        assertEquals("tardio", pendientes.get(1).operationType);
    }

    @Test
    public void update_persisteElContadorDeReintentos() {
        dao.insert(nuevo("x", "/x", 100, "pending"));
        WriteBackEntity e = dao.getPending().get(0);

        e.retryCount = 2;
        dao.update(e);

        assertEquals(2, dao.getPending().get(0).retryCount);
    }

    @Test
    public void delete_eliminaLaOperacion() {
        dao.insert(nuevo("x", "/x", 100, "pending"));
        WriteBackEntity e = dao.getPending().get(0);

        dao.delete(e);

        assertEquals(0, dao.getPendingCount());
    }

    @Test
    public void deleteFailed_borraSoloLasFallidas() {
        dao.insert(nuevo("ok", "/ok", 100, "pending"));
        dao.insert(nuevo("ko", "/ko", 200, "failed"));

        int borradas = dao.deleteFailed();

        assertEquals(1, borradas);
        assertEquals(1, dao.getPendingCount());
    }

    @Test
    public void deleteAll_vaciaLaCola() {
        dao.insert(nuevo("a", "/a", 100, "pending"));
        dao.insert(nuevo("b", "/b", 200, "pending"));

        dao.deleteAll();

        assertEquals(0, dao.getPendingCount());
        assertEquals(0, dao.getPending().size());
    }
}
