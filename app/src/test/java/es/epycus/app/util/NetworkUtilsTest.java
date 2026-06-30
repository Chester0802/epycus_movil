package es.epycus.app.util;

import static org.junit.Assert.assertEquals;

import es.epycus.app.R;

import org.junit.Test;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Mapeo de errores HTTP y de red a recursos de string. Lógica pura (las constantes
 * {@code R.string.*} son enteros generados) → no necesita Robolectric.
 */
public class NetworkUtilsTest {

    @Test
    public void httpError_403() {
        assertEquals(R.string.error_http_403, NetworkUtils.getHttpErrorResId(403));
    }

    @Test
    public void httpError_422() {
        assertEquals(R.string.error_http_422, NetworkUtils.getHttpErrorResId(422));
    }

    @Test
    public void httpError_500() {
        assertEquals(R.string.error_http_500, NetworkUtils.getHttpErrorResId(500));
    }

    @Test
    public void httpError_desconocido_devuelveErrorConexion() {
        assertEquals(R.string.error_conexion, NetworkUtils.getHttpErrorResId(418));
    }

    @Test
    public void networkError_timeout() {
        assertEquals(R.string.error_timeout,
                NetworkUtils.getNetworkErrorResId(new SocketTimeoutException()));
    }

    @Test
    public void networkError_conexionRechazada() {
        assertEquals(R.string.error_servidor_no_disponible,
                NetworkUtils.getNetworkErrorResId(new ConnectException()));
    }

    @Test
    public void networkError_hostDesconocido() {
        assertEquals(R.string.error_sin_conexion,
                NetworkUtils.getNetworkErrorResId(new UnknownHostException()));
    }

    @Test
    public void networkError_otro_devuelveErrorConexion() {
        assertEquals(R.string.error_conexion,
                NetworkUtils.getNetworkErrorResId(new RuntimeException("boom")));
    }
}
