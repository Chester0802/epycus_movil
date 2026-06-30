package es.epycus.app.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;

/**
 * Tests de los decodificadores estáticos de JWT de {@link SessionManager}.
 * No necesitan instancia (ni EncryptedSharedPreferences); sí Robolectric por
 * el uso de {@code android.util.Base64}.
 */
@RunWith(RobolectricTestRunner.class)
public class SessionManagerJwtTest {

    /** Construye un JWT de pega: header.payload.signature, payload = base64url(json). */
    private String jwt(String payloadJson) {
        String header = b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = b64(payloadJson);
        return header + "." + payload + ".firma_de_pega";
    }

    private String b64(String s) {
        return Base64.encodeToString(s.getBytes(StandardCharsets.UTF_8),
                Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    @Test
    public void extractId_desdeNameid() {
        String token = jwt("{\"nameid\":\"42\"}");
        assertEquals(42, SessionManager.extractIdFromToken(token));
    }

    @Test
    public void extractId_desdeSub() {
        String token = jwt("{\"sub\":\"15\"}");
        assertEquals(15, SessionManager.extractIdFromToken(token));
    }

    @Test
    public void extractId_desdeClaimSchemaUri() {
        String token = jwt("{\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier\":\"99\"}");
        assertEquals(99, SessionManager.extractIdFromToken(token));
    }

    @Test
    public void extractId_tokenNulo_devuelveMenosUno() {
        assertEquals(-1, SessionManager.extractIdFromToken(null));
    }

    @Test
    public void extractId_tokenMalformado_devuelveMenosUno() {
        assertEquals(-1, SessionManager.extractIdFromToken("esto-no-es-un-jwt"));
    }

    @Test
    public void extractId_sinClaimDeId_devuelveMenosUno() {
        String token = jwt("{\"name\":\"Ana\"}");
        assertEquals(-1, SessionManager.extractIdFromToken(token));
    }

    @Test
    public void extractName_desdeName() {
        String token = jwt("{\"name\":\"Ana López\"}");
        assertEquals("Ana López", SessionManager.extractNameFromToken(token));
    }

    @Test
    public void extractName_desdeUniqueName() {
        String token = jwt("{\"unique_name\":\"epycus_user\"}");
        assertEquals("epycus_user", SessionManager.extractNameFromToken(token));
    }

    @Test
    public void extractName_tokenNulo_devuelveNull() {
        assertNull(SessionManager.extractNameFromToken(null));
    }
}
