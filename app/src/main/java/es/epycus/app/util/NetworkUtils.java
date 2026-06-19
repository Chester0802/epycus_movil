package es.epycus.app.util;

import androidx.annotation.StringRes;

import es.epycus.app.R;
import es.epycus.app.model.RespuestaApi;

public class NetworkUtils {

    @StringRes
    public static int getHttpErrorResId(int httpCode) {
        switch (httpCode) {
            case 403: return R.string.error_http_403;
            case 422: return R.string.error_http_422;
            case 500: return R.string.error_http_500;
            default: return R.string.error_conexion;
        }
    }

    @StringRes
    public static int getNetworkErrorResId(Throwable t) {
        if (t instanceof java.net.SocketTimeoutException) {
            return R.string.error_timeout;
        } else if (t instanceof java.net.UnknownHostException
                || t instanceof java.net.ConnectException) {
            return R.string.error_sin_conexion;
        } else {
            return R.string.error_conexion;
        }
    }

    public static String getErrorMessage(android.content.Context context,
                                          retrofit2.Response<?> response) {
        if (response.body() != null && ((RespuestaApi<?>) response.body()).getMensaje() != null) {
            return ((RespuestaApi<?>) response.body()).getMensaje();
        }
        int resId = getHttpErrorResId(response.code());
        if (resId == R.string.error_conexion) {
            return context.getString(R.string.error_http_desconocido, response.code());
        }
        return context.getString(resId);
    }
}
