package es.epycus.app.api;

import java.io.IOException;

import androidx.annotation.NonNull;

import es.epycus.app.util.SessionManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = sessionManager.getToken();
        Request original = chain.request();

        if (token != null) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            return chain.proceed(builder.build());
        }

        return chain.proceed(original);
    }
}
