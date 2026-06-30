package es.epycus.app.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import es.epycus.app.data.local.dao.CacheDao;
import es.epycus.app.data.local.entity.CacheEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CacheDaoTest {

    private AppDatabase db;
    private CacheDao dao;

    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.cacheDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insert_y_getValue() {
        dao.insert(new CacheEntity("dashboard", "{\"x\":1}"));
        assertEquals("{\"x\":1}", dao.getValue("dashboard"));
    }

    @Test
    public void getValue_claveInexistente_devuelveNull() {
        assertNull(dao.getValue("no_existe"));
    }

    @Test
    public void insert_conflictoReemplaza() {
        dao.insert(new CacheEntity("k", "v1"));
        dao.insert(new CacheEntity("k", "v2"));

        assertEquals("v2", dao.getValue("k"));
        assertEquals(1, dao.getAll().size());
    }

    @Test
    public void getAll_devuelveTodas() {
        dao.insert(new CacheEntity("a", "1"));
        dao.insert(new CacheEntity("b", "2"));

        assertEquals(2, dao.getAll().size());
    }

    @Test
    public void delete_porClave() {
        dao.insert(new CacheEntity("a", "1"));
        dao.insert(new CacheEntity("b", "2"));

        dao.delete("a");

        assertNull(dao.getValue("a"));
        assertEquals("2", dao.getValue("b"));
    }

    @Test
    public void deleteAll_vacia() {
        dao.insert(new CacheEntity("a", "1"));
        dao.insert(new CacheEntity("b", "2"));

        dao.deleteAll();

        assertTrue(dao.getAll().isEmpty());
    }
}
