package es.epycus.app.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.model.dto.MisionDto;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Contrato de los servicios de hábitos, misiones y pomodoro contra MockWebServer.
 * El foco es verificar la URL ({@code @Path}) y el verbo HTTP, además de un par de
 * deserializaciones de listas. Asegura que las rutas del cliente coinciden con el backend
 * (incluido el endpoint que reencola {@code SyncWorker}).
 */
public class ApiContractTest {

    private MockWebServer server;
    private Retrofit retrofit;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    private void enqueueOk(String body) {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(body));
    }

    // --- Hábitos ---

    @Test
    public void habitos_hoy_getYDeserializaLista() throws Exception {
        ApiHabitosService s = retrofit.create(ApiHabitosService.class);
        enqueueOk("{\"exito\":true,\"datos\":[{\"id\":1,\"nombre\":\"Leer\",\"estadoHoy\":\"Pendiente\"}]}");

        Response<RespuestaApi<List<HabitoHoyDto>>> resp = s.hoy().execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/api/v1/habitos/hoy", req.getPath());
        assertEquals(1, resp.body().getDatos().size());
        assertEquals("Leer", resp.body().getDatos().get(0).getNombre());
    }

    @Test
    public void habitos_completar_postConIdEnLaRuta() throws Exception {
        ApiHabitosService s = retrofit.create(ApiHabitosService.class);
        enqueueOk("{\"exito\":true}");

        s.completar(5).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/v1/habitos/5/completar", req.getPath());
    }

    @Test
    public void habitos_eliminar_delete() throws Exception {
        ApiHabitosService s = retrofit.create(ApiHabitosService.class);
        enqueueOk("{\"exito\":true}");

        s.eliminar(7).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("DELETE", req.getMethod());
        assertEquals("/api/v1/habitos/7", req.getPath());
    }

    @Test
    public void habitos_actualizar_putConCuerpo() throws Exception {
        ApiHabitosService s = retrofit.create(ApiHabitosService.class);
        enqueueOk("{\"exito\":true}");

        s.actualizar(3, Collections.singletonMap("nombre", "Nuevo")).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("PUT", req.getMethod());
        assertEquals("/api/v1/habitos/3", req.getPath());
        assertTrue(req.getBody().readUtf8().contains("\"nombre\":\"Nuevo\""));
    }

    // --- Misiones ---

    @Test
    public void misiones_listar_getYDeserializaLista() throws Exception {
        ApiMisionesService s = retrofit.create(ApiMisionesService.class);
        enqueueOk("{\"exito\":true,\"datos\":[{\"id\":1,\"nombre\":\"Proyecto\"}]}");

        Response<RespuestaApi<List<MisionDto>>> resp = s.listar().execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("GET", req.getMethod());
        assertEquals("/api/v1/misiones", req.getPath());
        assertEquals(1, resp.body().getDatos().size());
    }

    @Test
    public void misiones_completar_postConIdEnLaRuta() throws Exception {
        ApiMisionesService s = retrofit.create(ApiMisionesService.class);
        enqueueOk("{\"exito\":true}");

        s.completar(9).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/v1/misiones/9/completar", req.getPath());
    }

    // --- Pomodoro (endpoints que alimentan la cola offline) ---

    @Test
    public void pomodoro_cicloCompletado_coincideConElEndpointDeLaColaOffline() throws Exception {
        ApiPomodoroService s = retrofit.create(ApiPomodoroService.class);
        enqueueOk("{\"exito\":true}");

        s.cicloCompletado(2, Collections.singletonMap("duracion", 25)).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        // PomodoroRepository encola exactamente este endpoint -> deben coincidir.
        assertEquals("/api/v1/pomodoro/2/ciclo-completado", req.getPath());
    }

    @Test
    public void pomodoro_cancelar_post() throws Exception {
        ApiPomodoroService s = retrofit.create(ApiPomodoroService.class);
        enqueueOk("{\"exito\":true}");

        s.cancelar(4).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/v1/pomodoro/4/cancelar", req.getPath());
    }
}
