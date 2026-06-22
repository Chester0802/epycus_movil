package es.epycus.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.ChatResponse;
import es.epycus.app.model.dto.DashboardResponse;
import es.epycus.app.model.dto.GamificacionResponse;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.PerfilResponse;
import es.epycus.app.model.entidades.AuthResponse;

import static org.junit.Assert.*;

public class ContratoJsonTest {

    private final Gson gson = new Gson();

    private String loadFixture(String name) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("fixtures/" + name);
        assertNotNull("Fixture not found: " + name, is);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    @Test
    public void authResponse_login() throws IOException {
        String json = loadFixture("auth_response_login.json");
        Type type = new TypeToken<RespuestaApi<AuthResponse>>() {}.getType();
        RespuestaApi<AuthResponse> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        assertEquals("Inicio de sesión exitoso", resp.getMensaje());
        assertNull(resp.getErrores());

        AuthResponse data = resp.getDatos();
        assertNotNull(data);
        assertNotNull(data.getToken());
        assertTrue(data.getToken().startsWith("eyJ"));
        assertNotNull(data.getRefreshToken());
        assertEquals("Inicio de sesión exitoso", data.getMensaje());
    }

    @Test
    public void authResponse_refresh() throws IOException {
        String json = loadFixture("auth_response_refresh.json");
        Type type = new TypeToken<RespuestaApi<AuthResponse>>() {}.getType();
        RespuestaApi<AuthResponse> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        assertEquals("Token renovado", resp.getMensaje());

        AuthResponse data = resp.getDatos();
        assertNotNull(data);
        assertNotNull(data.getToken());
        assertNotNull(data.getRefreshToken());
    }

    @Test
    public void dashboardResponse() throws IOException {
        String json = loadFixture("dashboard_response.json");
        Type type = new TypeToken<RespuestaApi<DashboardResponse>>() {}.getType();
        RespuestaApi<DashboardResponse> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        DashboardResponse data = resp.getDatos();
        assertNotNull(data);

        DashboardResponse.Kpis kpis = data.getKpis();
        assertNotNull(kpis);
        assertEquals(3, kpis.getHabitosPendientes());
        assertEquals(2, kpis.getMisionesPendientes());

        assertEquals(3, data.getHabitosPendientes());

        DashboardResponse.Frase frase = data.getFrase();
        assertNotNull(frase);
        assertTrue(frase.getFrase().contains("éxito"));
        assertEquals("Robert Collier", frase.getAutor());
    }

    @Test
    public void gamificacionResponse() throws IOException {
        String json = loadFixture("gamificacion_response.json");
        Type type = new TypeToken<RespuestaApi<GamificacionResponse>>() {}.getType();
        RespuestaApi<GamificacionResponse> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        GamificacionResponse data = resp.getDatos();
        assertNotNull(data);

        assertEquals(1250, data.getXpTotal());
        assertEquals(5, data.getNivel());
        assertEquals("Estudiante Constante", data.getTitulo());
        assertEquals(7, data.getRachaActual());
        assertEquals(250, data.getXpParaSiguienteNivel());
        assertEquals(75.0, data.getPorcentajeProgreso(), 0.001);
        assertNotNull(data.getImagenPersonaje());
    }

    @Test
    public void habitosHoy_list() throws IOException {
        String json = loadFixture("habitos_hoy.json");
        Type type = new TypeToken<RespuestaApi<List<HabitoHoyDto>>>() {}.getType();
        RespuestaApi<List<HabitoHoyDto>> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        List<HabitoHoyDto> data = resp.getDatos();
        assertNotNull(data);
        assertEquals(3, data.size());

        HabitoHoyDto primero = data.get(0);
        assertEquals(1, primero.getId());
        assertEquals("Leer 30 minutos", primero.getNombre());
        assertEquals("Pendiente", primero.getEstadoHoy());
        assertEquals(50, primero.getXpPotencial());
        assertEquals("Estudio", primero.getCategoria());
        assertTrue(primero.isPendiente());

        HabitoHoyDto segundo = data.get(1);
        assertTrue(segundo.isCompletado());

        HabitoHoyDto tercero = data.get(2);
        assertTrue(tercero.isFallado());
    }

    @Test
    public void misiones_lista() throws IOException {
        String json = loadFixture("misiones_lista.json");
        Type type = new TypeToken<RespuestaApi<List<MisionDto>>>() {}.getType();
        RespuestaApi<List<MisionDto>> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        List<MisionDto> data = resp.getDatos();
        assertNotNull(data);
        assertEquals(2, data.size());

        MisionDto primera = data.get(0);
        assertEquals(1, primera.getId());
        assertEquals("Proyecto final de matemáticas", primera.getNombre());
        assertEquals("Completar la entrega del proyecto final", primera.getDescripcion());
        assertEquals("Matemáticas Avanzadas", primera.getNombreCurso());
        assertEquals("2026-07-15", primera.getFechaLimite());
        assertEquals("Alta", primera.getPrioridad());
        assertEquals("Pendiente", primera.getEstado());
        assertEquals(200, primera.getXpOtorgado());
        assertEquals(1, primera.getCategoriaId());
        assertFalse(primera.isCompletada());

        MisionDto segunda = data.get(1);
        assertEquals("Media", segunda.getPrioridad());
        assertNull(segunda.getDescripcion());
    }

    @Test
    public void perfilResponse() throws IOException {
        String json = loadFixture("perfil_response.json");
        Type type = new TypeToken<RespuestaApi<PerfilResponse>>() {}.getType();
        RespuestaApi<PerfilResponse> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        PerfilResponse data = resp.getDatos();
        assertNotNull(data);

        assertNotNull(data.getImagenPersonaje());

        PerfilResponse.Perfil perfil = data.getPerfil();
        assertNotNull(perfil);
        assertEquals("Juan Pérez", perfil.getNombre());
        assertEquals("juan@epycus.es", perfil.getCorreoElectronico());
        assertEquals("EP-001", perfil.getCodigoUnico());
        assertEquals("Masculino", perfil.getGenero());
        assertEquals("Ingeniería Informática", perfil.getCarreraNombre());
        assertEquals(5, perfil.getNivelActual());
        assertEquals(1250, perfil.getXpTotal());
        assertEquals(7, perfil.getRachaActual());
        assertEquals(30, perfil.getRachaMaxima());
        assertEquals("2025-09-01", perfil.getFechaRegistro());
    }

    @Test
    public void chatResponse() throws IOException {
        String json = loadFixture("chat_response.json");
        Type type = new TypeToken<RespuestaApi<ChatResponse>>() {}.getType();
        RespuestaApi<ChatResponse> resp = gson.fromJson(json, type);

        assertTrue(resp.isExito());
        ChatResponse data = resp.getDatos();
        assertNotNull(data);

        assertTrue(data.getRespuesta().contains("ayudarte"));
        assertEquals("conv-abc-123", data.getConversacionId());
    }
}
