package es.epycus.app.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import androidx.annotation.NonNull;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.RefreshDto;
import es.epycus.app.model.entidades.AuthResponse;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.SessionManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class AuthInterceptor implements Interceptor {
    private final SessionManager sessionManager;
    private final Context context;
    private final Object refreshLock = new Object();
    private volatile boolean refreshing = false;
    private volatile boolean loggedOut = false;

    public AuthInterceptor(SessionManager sessionManager, Context context) {
        this.sessionManager = sessionManager;
        this.context = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = sessionManager.getToken();
        Request original = chain.request();

        if (token != null) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Response response = chain.proceed(builder.build());

            if (response.code() == 401
                    && !original.url().encodedPath().contains("/auth/refresh")
                    && original.header("X-Retry") == null) {
                response.close();
                return refreshAndRetry(chain, original);
            }

            if (response.code() == 401 && refreshing) {
                response.close();
                String newToken = sessionManager.getToken();
                if (newToken != null && !newToken.equals(token)) {
                    Request.Builder retryBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + newToken)
                            .header("X-Retry", "true");
                    return chain.proceed(retryBuilder.build());
                }
                forceLogoutOnce();
                throw new IOException("Refresh already in progress but token not updated");
            }

            return response;
        }

        return chain.proceed(original);
    }

    private Response refreshAndRetry(Chain chain, Request original) throws IOException {
        synchronized (refreshLock) {
            if (refreshing) {
                String currentToken = sessionManager.getToken();
                Request.Builder retryBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + currentToken)
                        .header("X-Retry", "true");
                return chain.proceed(retryBuilder.build());
            }
            refreshing = true;
        }

        try {
            String refreshToken = sessionManager.getRefreshToken();
            if (refreshToken == null) {
                forceLogoutOnce();
                throw new IOException("No refresh token available");
            }

            ApiAuthService authlessService = RetrofitClient.getAuthlessRetrofit(context)
                    .create(ApiAuthService.class);

            Call<RespuestaApi<AuthResponse>> refreshCall =
                    authlessService.refresh(new RefreshDto(refreshToken));
            retrofit2.Response<RespuestaApi<AuthResponse>> refreshResponse =
                    refreshCall.execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null
                    && refreshResponse.body().isExito()
                    && refreshResponse.body().getDatos() != null) {

                AuthResponse authData = refreshResponse.body().getDatos();
                sessionManager.updateToken(authData.getToken(), authData.getRefreshToken());

                Request.Builder retryBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + authData.getToken())
                        .header("X-Retry", "true");
                Response retryResponse = chain.proceed(retryBuilder.build());
                if (retryResponse.code() == 401) {
                    retryResponse.close();
                    forceLogoutOnce();
                    throw new IOException("Retry also failed with 401");
                }
                return retryResponse;
            } else {
                forceLogoutOnce();
                throw new IOException("Refresh failed");
            }
        } catch (Exception e) {
            forceLogoutOnce();
            throw new IOException("Token refresh failed", e);
        } finally {
            synchronized (refreshLock) {
                refreshing = false;
            }
        }
    }

    private void forceLogoutOnce() {
        if (loggedOut) return;
        loggedOut = true;
        sessionManager.logout();
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }
}
