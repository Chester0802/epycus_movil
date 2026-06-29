package es.epycus.app.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.WriteBackEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteBackQueue {
    private static final String TAG = "WriteBackQueue";
    private static WriteBackQueue instance;
    private final AppDatabase database;
    private final Gson gson;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private final Context context;
    private boolean isProcessing = false;

    private WriteBackQueue(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized WriteBackQueue getInstance(Context context) {
        if (instance == null) {
            instance = new WriteBackQueue(context);
        }
        return instance;
    }

    public static WriteBackQueue getInstance() {
        return instance;
    }

    public void enqueue(String operationType, String endpoint, String requestBody) {
        WriteBackEntity entity = new WriteBackEntity();
        entity.operationType = operationType;
        entity.endpoint = endpoint;
        entity.requestBody = requestBody;
        entity.createdAt = System.currentTimeMillis();
        entity.retryCount = 0;
        entity.status = "pending";

        executor.execute(() -> {
            database.writeBackDao().insert(entity);
            Log.d(TAG, "Enqueued: " + operationType + " -> " + endpoint);
        });
    }

    public void enqueue(String operationType, String endpoint, Object requestObject) {
        String json = gson.toJson(requestObject);
        enqueue(operationType, endpoint, json);
    }

    public void processQueue(SyncCallback callback) {
        if (isProcessing) {
            Log.d(TAG, "Ya está procesando la cola");
            if (callback != null) callback.onProgress(0, 0);
            return;
        }

        if (!NetworkUtils.isOnline(context)) {
            Log.d(TAG, "Sin conexión, no se procesa la cola");
            if (callback != null) callback.onNoConnection();
            return;
        }

        isProcessing = true;
        executor.execute(() -> {
            try {
                List<WriteBackEntity> pending = database.writeBackDao().getPending();
                int total = pending.size();
                int processed = 0;
                int failed = 0;

                for (WriteBackEntity item : pending) {
                    if (!NetworkUtils.isOnline(context)) {
                        break;
                    }

                    boolean success = executeOperation(item);
                    if (success) {
                        database.writeBackDao().delete(item);
                        processed++;
                    } else {
                        item.retryCount++;
                        if (item.retryCount >= 3) {
                            item.status = "failed";
                            failed++;
                        }
                        database.writeBackDao().update(item);
                    }

                    final int currentProcessed = processed;
                    final int currentTotal = total;
                    mainHandler.post(() -> {
                        if (callback != null) callback.onProgress(currentProcessed, currentTotal);
                    });
                }

                isProcessing = false;
                final int finalProcessed = processed;
                final int finalFailed = failed;
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(finalProcessed, finalFailed);
                });
            } catch (Exception e) {
                isProcessing = false;
                Log.e(TAG, "Error procesando cola: " + e.getMessage());
                mainHandler.post(() -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
            }
        });
    }

    private boolean executeOperation(WriteBackEntity item) {
        try {
            RetrofitClient client = RetrofitClient.getInstance(context);
            String base = client.getBaseUrl();
            String endpoint = item.endpoint != null && item.endpoint.startsWith("/")
                    ? item.endpoint.substring(1)
                    : (item.endpoint != null ? item.endpoint : "");
            String url = base + endpoint;

            String metodo = item.operationType == null ? "POST" : item.operationType.toUpperCase();

            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = (item.requestBody == null || item.requestBody.isEmpty())
                    ? RequestBody.create(new byte[0], null)
                    : RequestBody.create(item.requestBody, jsonType);

            Request.Builder builder = new Request.Builder().url(url);
            switch (metodo) {
                case "DELETE":
                    builder.delete(body);
                    break;
                case "PUT":
                    builder.put(body);
                    break;
                case "PATCH":
                    builder.patch(body);
                    break;
                default:
                    builder.post(body);
                    break;
            }

            try (Response response = client.getHttpClient().newCall(builder.build()).execute()) {
                int code = response.code();
                if (response.isSuccessful()) {
                    return true;
                }
                if (code >= 400 && code < 500 && code != 408 && code != 429) {
                    Log.w(TAG, "Operación descartada por error de cliente " + code + ": " + item.endpoint);
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

    public interface SyncCallback {
        void onProgress(int processed, int total);
        void onComplete(int processed, int failed);
        void onNoConnection();
        void onError(String error);
    }

    public void getPendingCount(CountCallback callback) {
        executor.execute(() -> {
            int count = database.writeBackDao().getPendingCount();
            mainHandler.post(() -> callback.onCount(count));
        });
    }

    public interface CountCallback {
        void onCount(int count);
    }
}