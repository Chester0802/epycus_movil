package es.epycus.app.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CacheManagerTest {

    private AppDatabase db;
    private CacheManager cache;

    private long now() {
        return System.currentTimeMillis() / 1000;
    }

    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        cache = new CacheManager(db);   // constructor @VisibleForTesting (sin purga async)
    }

    @After
    public void tearDown() {
        db.close();
    }

    // --- isExpired (lógica pura) ---

    @Test
    public void isExpired_dentroDelTtl_noExpira() {
        assertFalse(CacheManager.isExpired(1000, 300, 1200)); // pasaron 200 <= 300
    }

    @Test
    public void isExpired_justoEnElLimite_noExpira() {
        assertFalse(CacheManager.isExpired(1000, 300, 1300)); // pasaron 300 == 300
    }

    @Test
    public void isExpired_pasadoElTtl_expira() {
        assertTrue(CacheManager.isExpired(1000, 300, 1301)); // pasaron 301 > 300
    }

    // --- get() ---

    @Test
    public void get_entradaFresca_devuelveDato() {
        db.cacheDao().insert(new CacheEntity("k", CacheManager.wrap("hola", 300, now())));
        assertEquals("hola", cache.get("k"));
    }

    @Test
    public void get_entradaExpirada_devuelveNullYLaElimina() {
        db.cacheDao().insert(new CacheEntity("k", CacheManager.wrap("vieja", 300, now() - 1000)));

        assertNull(cache.get("k"));
        assertNull(db.cacheDao().getValue("k")); // se eliminó
    }

    @Test
    public void get_claveInexistente_devuelveNull() {
        assertNull(cache.get("no_existe"));
    }

    @Test
    public void get_entradaCorrupta_devuelveNull() {
        db.cacheDao().insert(new CacheEntity("k", "esto no es json"));
        assertNull(cache.get("k"));
    }

    // --- purgeExpiredBlocking() ---

    @Test
    public void purge_eliminaExpiradasYConservaFrescas() {
        db.cacheDao().insert(new CacheEntity("fresca", CacheManager.wrap("a", 300, now())));
        db.cacheDao().insert(new CacheEntity("vieja", CacheManager.wrap("b", 300, now() - 1000)));
        db.cacheDao().insert(new CacheEntity("rota", "{no-json"));

        cache.purgeExpiredBlocking();

        assertEquals(1, db.cacheDao().getAll().size());
        assertEquals("a", cache.get("fresca"));
        assertNull(db.cacheDao().getValue("vieja"));
        assertNull(db.cacheDao().getValue("rota"));
    }
}
