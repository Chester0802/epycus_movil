package es.epycus.app.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import es.epycus.app.data.local.dao.HabitoDao;
import es.epycus.app.data.local.dao.UsuarioDao;
import es.epycus.app.data.local.entity.HabitoEntity;
import es.epycus.app.data.local.entity.UsuarioEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class EntidadesDaoTest {

    private AppDatabase db;
    private UsuarioDao usuarioDao;
    private HabitoDao habitoDao;

    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        usuarioDao = db.usuarioDao();
        habitoDao = db.habitoDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void usuario_insert_y_getById() {
        usuarioDao.insert(new UsuarioEntity(7, "Ana", "ana@epycus.es"));

        UsuarioEntity u = usuarioDao.getById(7);

        assertEquals("Ana", u.getNombre());
        assertEquals("ana@epycus.es", u.getCorreoElectronico());
    }

    @Test
    public void usuario_getById_inexistente_devuelveNull() {
        assertNull(usuarioDao.getById(999));
    }

    @Test
    public void usuario_insert_reemplazaPorConflictoDeId() {
        usuarioDao.insert(new UsuarioEntity(1, "Viejo", "v@e.es"));
        usuarioDao.insert(new UsuarioEntity(1, "Nuevo", "n@e.es"));

        assertEquals("Nuevo", usuarioDao.getById(1).getNombre());
    }

    @Test
    public void usuario_deleteAll() {
        usuarioDao.insert(new UsuarioEntity(1, "A", "a@e.es"));
        usuarioDao.deleteAll();
        assertNull(usuarioDao.getById(1));
    }

    @Test
    public void habito_getActivos_soloDevuelveActivos() {
        HabitoEntity activo = new HabitoEntity(1, "Leer", "d", "diaria", 3, 5, true, 1);
        HabitoEntity inactivo = new HabitoEntity(2, "Correr", "d", "diaria", 0, 0, false, 1);
        habitoDao.insertAll(Arrays.asList(activo, inactivo));

        assertEquals(1, habitoDao.getActivos().size());
        assertEquals("Leer", habitoDao.getActivos().get(0).getNombre());
    }

    @Test
    public void habito_deleteAll() {
        habitoDao.insertAll(Arrays.asList(
                new HabitoEntity(1, "Leer", "d", "diaria", 3, 5, true, 1)));
        habitoDao.deleteAll();
        assertTrue(habitoDao.getActivos().isEmpty());
    }
}
