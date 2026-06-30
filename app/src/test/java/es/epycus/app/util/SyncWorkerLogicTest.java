package es.epycus.app.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test de {@link SyncWorker#httpMethodFor(String)}: los tipos de operación semánticos
 * (p. ej. "ciclo_completado") deben resolverse a POST; los verbos HTTP se respetan.
 */
public class SyncWorkerLogicTest {

    @Test
    public void verbosHttpSeRespetan() {
        assertEquals("GET", SyncWorker.httpMethodFor("get"));
        assertEquals("PUT", SyncWorker.httpMethodFor("PUT"));
        assertEquals("PATCH", SyncWorker.httpMethodFor("patch"));
        assertEquals("DELETE", SyncWorker.httpMethodFor("Delete"));
        assertEquals("POST", SyncWorker.httpMethodFor("post"));
    }

    @Test
    public void tiposSemanticosDePomodoro_seMapeanAPost() {
        assertEquals("POST", SyncWorker.httpMethodFor("ciclo_completado"));
        assertEquals("POST", SyncWorker.httpMethodFor("finalizar"));
        assertEquals("POST", SyncWorker.httpMethodFor("cancelar"));
    }

    @Test
    public void nulo_seMapeaAPost() {
        assertEquals("POST", SyncWorker.httpMethodFor(null));
    }
}
