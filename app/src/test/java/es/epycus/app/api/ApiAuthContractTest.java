package es.epycus.app.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.LoginDto;
import es.epycus.app.model.dto.MensajeResponseDto;
import es.epycus.app.model.dto.RefreshDto;
import es.epycus.app.model.entidades.AuthResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Tests de contrato de {@link ApiAuthService} contra un servidor simulado (MockWebServer).
 * Verifica tanto la petición (ruta, método, cuerpo) como la deserialización de la respuesta,
 * sin pasar por el {@code RetrofitClient} real (que tiene certificate pinning a producción).
 */
public class ApiAuthContractTest {

    private MockWebServer server;
    private ApiAuthService service;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        service = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiAuthService.class);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void login_enviaPostCorrectoYDeserializaToken() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"exito\":true,\"mensaje\":\"ok\",\"datos\":{" +
                        "\"token\":\"eyJabc\",\"refreshToken\":\"r-123\",\"mensaje\":\"ok\"}}"));

        Response<RespuestaApi<AuthResponse>> resp =
                service.login(new LoginDto("ana@epycus.es", "secreto")).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/v1/auth/login", req.getPath());
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("\"correo\":\"ana@epycus.es\""));
        assertTrue(body.contains("\"contrasena\":\"secreto\""));

        assertTrue(resp.isSuccessful());
        assertTrue(resp.body().isExito());
        assertEquals("eyJabc", resp.body().getDatos().getToken());
        assertEquals("r-123", resp.body().getDatos().getRefreshToken());
    }

    @Test
    public void refresh_enviaRefreshTokenEnElCuerpo() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"exito\":true,\"datos\":{\"token\":\"nuevo\",\"refreshToken\":\"r-456\"}}"));

        service.refresh(new RefreshDto("r-123")).execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/v1/auth/refresh", req.getPath());
        assertTrue(req.getBody().readUtf8().contains("\"refreshToken\":\"r-123\""));
    }

    @Test
    public void logout_esPostSinCuerpo() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "{\"exito\":true,\"mensaje\":\"Sesión cerrada\"}"));

        Response<RespuestaApi<MensajeResponseDto>> resp = service.logout().execute();

        RecordedRequest req = server.takeRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("/api/v1/auth/logout", req.getPath());
        assertEquals(0, req.getBodySize());
        assertTrue(resp.body().isExito());
    }

    @Test
    public void login_401_seReportaComoNoExitoso() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).setBody(
                "{\"exito\":false,\"mensaje\":\"Credenciales inválidas\"}"));

        Response<RespuestaApi<AuthResponse>> resp =
                service.login(new LoginDto("x@y.es", "mal")).execute();

        assertFalse(resp.isSuccessful());
        assertEquals(401, resp.code());
    }
}
