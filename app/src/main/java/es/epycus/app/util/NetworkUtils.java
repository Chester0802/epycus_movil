package es.epycus.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.StringRes;

import java.io.IOException;

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
        } else if (t instanceof java.net.ConnectException) {
            return R.string.error_servidor_no_disponible;
        } else if (t instanceof java.net.UnknownHostException) {
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
        if (response.errorBody() != null) {
            try {
                String errorJson = response.errorBody().string();
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                if (obj.has("mensaje") && !obj.get("mensaje").isJsonNull()) {
                    return obj.get("mensaje").getAsString();
                }
            } catch (IOException | com.google.gson.JsonSyntaxException ignored) { }
        }
        int resId = getHttpErrorResId(response.code());
        if (resId == R.string.error_conexion) {
            return context.getString(R.string.error_http_desconocido, response.code());
        }
        return context.getString(resId);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            android.net.NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        }
    }
}
