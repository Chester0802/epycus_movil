package es.epycus.app.api;

import android.content.Context;

import es.epycus.app.BuildConfig;
import es.epycus.app.util.SessionManager;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static RetrofitClient instance;
    private static Retrofit authlessRetrofit;
    private final Retrofit retrofit;
    private final OkHttpClient okHttpClient;
    private final ApiAuthService apiAuthService;
    private final ApiBienestarService apiBienestarService;
    private final ApiDashboardService apiDashboardService;
    private final ApiDiarioService apiDiarioService;
    private final ApiEstadoAnimoService apiEstadoAnimoService;
    private final ApiGamificacionService apiGamificacionService;
    private final ApiHabitosService apiHabitosService;
    private final ApiIaService apiIaService;
    private final ApiMisionesService apiMisionesService;
    private final ApiPerfilService apiPerfilService;
    private final ApiPomodoroService apiPomodoroService;
    private final ApiProgresoService apiProgresoService;
    private final ApiAdminService apiAdminService;
    private final ApiSubTareasService apiSubTareasService;

    // Pines SHA-256 de la clave pública (SPKI) de la cadena TLS de app.epycus.es.
    // Se pinan 3 niveles para evitar que la app quede inutilizable en la renovación
    // de Let's Encrypt (certbot rota la clave de la hoja cada 90 días):
    //   1. Hoja (app.epycus.es)        -> máxima seguridad, cambia en cada renovación
    //   2. Intermedio (Let's Encrypt)  -> respaldo, sobrevive a la rotación de la hoja
    //   3. Raíz (ISRG Root X2)          -> respaldo estable durante años
    // OkHttp valida si CUALQUIER pin de la cadena coincide.
    private static final String PIN_LEAF = "sha256/3lfk/L2YnLH4PKmnFxaXzoayzzgEKxcG6G+XeqxGuhQ=";
    private static final String PIN_INTERMEDIATE = "sha256/s/tdAOmUzd8syaTuqfgGvFcn6DzA5Cmb+Vby1ST+U3Y=";
    private static final String PIN_ROOT = "sha256/diGVwiVYbubAI3RW4hB9xU8e/CH2GnkuvVFZE8zmgzI=";

    private static CertificatePinner buildCertificatePinner() {
        return new CertificatePinner.Builder()
                .add("app.epycus.es", PIN_LEAF)
                .add("app.epycus.es", PIN_INTERMEDIATE)
                .add("app.epycus.es", PIN_ROOT)
                .build();
    }

    private RetrofitClient(Context context) {
        SessionManager sessionManager = SessionManager.getInstance(context);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // BASIC nunca registra cabeceras (evita filtrar el JWT en Logcat); NONE en release.
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);

        CertificatePinner certificatePinner = buildCertificatePinner();

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(sessionManager, context))
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .certificatePinner(certificatePinner)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiAuthService = retrofit.create(ApiAuthService.class);
        apiBienestarService = retrofit.create(ApiBienestarService.class);
        apiDashboardService = retrofit.create(ApiDashboardService.class);
        apiDiarioService = retrofit.create(ApiDiarioService.class);
        apiEstadoAnimoService = retrofit.create(ApiEstadoAnimoService.class);
        apiGamificacionService = retrofit.create(ApiGamificacionService.class);
        apiHabitosService = retrofit.create(ApiHabitosService.class);
        apiIaService = retrofit.create(ApiIaService.class);
        apiMisionesService = retrofit.create(ApiMisionesService.class);
        apiPerfilService = retrofit.create(ApiPerfilService.class);
        apiPomodoroService = retrofit.create(ApiPomodoroService.class);
        apiProgresoService = retrofit.create(ApiProgresoService.class);
        apiAdminService = retrofit.create(ApiAdminService.class);
        apiSubTareasService = retrofit.create(ApiSubTareasService.class);
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized Retrofit getAuthlessRetrofit(Context context) {
        if (authlessRetrofit == null) {
            CertificatePinner certificatePinner = buildCertificatePinner();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .certificatePinner(certificatePinner)
                    .build();
            authlessRetrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return authlessRetrofit;
    }

    public OkHttpClient getHttpClient() { return okHttpClient; }

    public String getBaseUrl() { return BuildConfig.API_BASE_URL; }

    public ApiAuthService getApiAuthService() { return apiAuthService; }
    public ApiBienestarService getApiBienestarService() { return apiBienestarService; }
    public ApiDashboardService getApiDashboardService() { return apiDashboardService; }
    public ApiDiarioService getApiDiarioService() { return apiDiarioService; }
    public ApiEstadoAnimoService getApiEstadoAnimoService() { return apiEstadoAnimoService; }
    public ApiGamificacionService getApiGamificacionService() { return apiGamificacionService; }
    public ApiHabitosService getApiHabitosService() { return apiHabitosService; }
    public ApiIaService getApiIaService() { return apiIaService; }
    public ApiMisionesService getApiMisionesService() { return apiMisionesService; }
    public ApiPerfilService getApiPerfilService() { return apiPerfilService; }
    public ApiPomodoroService getApiPomodoroService() { return apiPomodoroService; }
    public ApiProgresoService getApiProgresoService() { return apiProgresoService; }
    public ApiAdminService getApiAdminService() { return apiAdminService; }
    public ApiSubTareasService getApiSubTareasService() { return apiSubTareasService; }
}
