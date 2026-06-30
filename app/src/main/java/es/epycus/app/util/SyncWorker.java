package es.epycus.app.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.concurrent.TimeUnit;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.WriteBackEntity;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Vacía la cola de operaciones offline (write_back_queue) usando WorkManager.
 *
 * <p>Sustituye al antiguo {@code WriteBackQueue} (que nunca se enganchó) y al flush
 * de Pomodoro {@code procesarColaWriteBack()} (que nunca se invocaba). Al ejecutarse
 * dentro de un {@link Worker}, el trabajo:
 * <ul>
 *   <li>se persiste y <b>sobrevive a la muerte del proceso</b>;</li>
 *   <li>espera a que haya red ({@link NetworkType#CONNECTED}) antes de ejecutarse;</li>
 *   <li>reintenta con backoff exponencial si quedan operaciones pendientes.</li>
 * </ul>
 *
 * <p>Las peticiones se lanzan con el {@link okhttp3.OkHttpClient} compartido, que ya
 * incluye {@code AuthInterceptor} → los reintentos van autenticados con el Bearer token
 * y renuevan el JWT ante un 401.
 */
public class SyncWorker extends Worker {
    private static final String TAG = "SyncWorker";
    private static final String UNIQUE_WORK = "epycus_writeback_sync";
    private static final int MAX_RETRIES = 3;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /** Encola (de forma única) un vaciado de la cola write-back para cuando haya red. */
    public static void schedule(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build();
        // KEEP: si ya hay un flush programado/en curso, no se duplica.
        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, request);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(ctx);
        RetrofitClient client = RetrofitClient.getInstance(ctx);

        List<WriteBackEntity> pending = db.writeBackDao().getPending();
        boolean needsRetry = false;

        for (WriteBackEntity item : pending) {
            boolean retryable = !execute(client, item);
            if (!retryable) {
                // Éxito, o error de cliente (4xx) que no se va a resolver reintentando.
                db.writeBackDao().delete(item);
            } else {
                item.retryCount++;
                if (item.retryCount >= MAX_RETRIES) {
                    item.status = "failed";
                    db.writeBackDao().update(item);
                    Log.w(TAG, "Operación descartada tras " + MAX_RETRIES + " intentos: " + item.endpoint);
                } else {
                    db.writeBackDao().update(item);
                    needsRetry = true;
                }
            }
        }
        return needsRetry ? Result.retry() : Result.success();
    }

    /**
     * Ejecuta una operación pendiente de forma síncrona.
     *
     * @return {@code true} si tuvo éxito o si debe descartarse (4xx irrecuperable);
     *         {@code false} si debe reintentarse (fallo de red, 5xx, 408, 429).
     */
    private boolean execute(RetrofitClient client, WriteBackEntity item) {
        try {
            String base = client.getBaseUrl();
            String endpoint = item.endpoint != null && item.endpoint.startsWith("/")
                    ? item.endpoint.substring(1)
                    : (item.endpoint != null ? item.endpoint : "");
            String url = base + endpoint;

            String metodo = httpMethodFor(item.operationType);

            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = (item.requestBody == null || item.requestBody.isEmpty())
                    ? RequestBody.create(new byte[0], null)
                    : RequestBody.create(item.requestBody, jsonType);

            Request.Builder builder = new Request.Builder().url(url);
            switch (metodo) {
                case "DELETE": builder.delete(body); break;
                case "PUT":    builder.put(body);    break;
                case "PATCH":  builder.patch(body);  break;
                default:       builder.post(body);   break;
            }

            try (Response response = client.getHttpClient().newCall(builder.build()).execute()) {
                int code = response.code();
                if (response.isSuccessful()) {
                    return true;
                }
                // 4xx (salvo 408/429) son errores de cliente: no se resuelven reintentando.
                if (code >= 400 && code < 500 && code != 408 && code != 429) {
                    Log.w(TAG, "Operación descartada por error " + code + ": " + item.endpoint);
                    return true;
                }
                Log.w(TAG, "Operación falló con código " + code + ", se reintentará: " + item.endpoint);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error ejecutando operación pendiente: " + e.getMessage());
            return false;
        }
    }

    /**
     * El {@code operationType} guardado puede ser un verbo HTTP o un nombre semántico
     * (p. ej. "ciclo_completado"). Si no es un verbo reconocido se asume POST, que es
     * el método de todas las operaciones write-back actuales.
     */
    @androidx.annotation.VisibleForTesting
    static String httpMethodFor(String operationType) {
        if (operationType == null) return "POST";
        String verb = operationType.toUpperCase(java.util.Locale.ROOT);
        switch (verb) {
            case "GET":
            case "PUT":
            case "PATCH":
            case "DELETE":
            case "POST":
                return verb;
            default:
                return "POST";
        }
    }
}
