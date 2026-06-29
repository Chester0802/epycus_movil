package es.epycus.app.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.dao.WriteBackDao;
import es.epycus.app.data.local.entity.WriteBackEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PomodoroIniciarResponse;
import es.epycus.app.model.dto.PomodoroCicloCompletadoResponse;
import es.epycus.app.model.dto.PomodoroFinalizarResponse;
import es.epycus.app.model.dto.PomodoroConfiguracionResponse;
import es.epycus.app.model.dto.PomodoroTipResponse;
import es.epycus.app.model.dto.PomodoroSesionActivaResponse;
import es.epycus.app.model.dto.PomodoroHistorialResponse;
import es.epycus.app.model.dto.PomodoroRachaResponse;
import es.epycus.app.model.dto.PomodoroEstadisticasDto;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PomodoroRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;
    private final WriteBackDao writeBackDao;
    private final ExecutorService writeExecutor;

    public PomodoroRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
        this.writeBackDao = AppDatabase.getInstance(context).writeBackDao();
        this.writeExecutor = AppDatabase.getWriteExecutor();
    }

    public Call<RespuestaApi<PomodoroIniciarResponse>> iniciar(Object body) {
        return api.getApiPomodoroService().iniciar(body);
    }

    public Call<RespuestaApi<PomodoroCicloCompletadoResponse>> cicloCompletado(int sesionId, Object body) {
        return api.getApiPomodoroService().cicloCompletado(sesionId, body);
    }

    public Call<RespuestaApi<PomodoroFinalizarResponse>> finalizar(int sesionId, Object body) {
        return api.getApiPomodoroService().finalizar(sesionId, body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> cancelar(int sesionId) {
        return api.getApiPomodoroService().cancelar(sesionId);
    }

    public Call<RespuestaApi<PomodoroConfiguracionResponse>> configuracion() {
        return api.getApiPomodoroService().configuracion();
    }

    public Call<RespuestaApi<SuccessResponseDto>> actualizarConfiguracion(Object body) {
        return api.getApiPomodoroService().actualizarConfiguracion(body);
    }

    public Call<RespuestaApi<PomodoroTipResponse>> tipAleatorio() {
        return api.getApiPomodoroService().tipAleatorio();
    }

    public Call<RespuestaApi<PomodoroSesionActivaResponse>> sesionActiva() {
        return api.getApiPomodoroService().sesionActiva();
    }

    public Call<RespuestaApi<PomodoroRachaResponse>> racha() {
        return api.getApiPomodoroService().racha();
    }

    public Call<RespuestaApi<PomodoroHistorialResponse>> historial(String desde, String hasta, int pagina, int tamano) {
        return api.getApiPomodoroService().historial(desde, hasta, pagina, tamano);
    }

    public Call<RespuestaApi<PomodoroEstadisticasDto>> estadisticas(String desde, String hasta) {
        return api.getApiPomodoroService().estadisticas(desde, hasta);
    }

    public void cacheJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public void getCachedJsonAsync(String key, CacheManager.CacheCallback callback) {
        cacheManager.getAsync(key, callback);
    }

    public String getCachedJson(String key) {
        return cacheManager.get(key);
    }

    public void cicloCompletadoAsync(int sesionId, Object body, WriteBackCallback callback) {
        executeWithRetry("ciclo_completado", "/api/v1/pomodoro/" + sesionId + "/ciclo-completado", body, callback);
    }

    public void finalizarAsync(int sesionId, Object body, WriteBackCallback callback) {
        executeWithRetry("finalizar", "/api/v1/pomodoro/" + sesionId + "/finalizar", body, callback);
    }

    public void cancelarAsync(int sesionId, WriteBackCallback callback) {
        executeWithRetry("cancelar", "/api/v1/pomodoro/" + sesionId + "/cancelar", null, callback);
    }

    private void executeWithRetry(String operationType, String endpoint, Object body, WriteBackCallback callback) {
        writeExecutor.execute(() -> {
            int sesionId = extractSesionId(endpoint);
            switch (operationType) {
                case "ciclo_completado":
                    @SuppressWarnings("unchecked")
                    Call<RespuestaApi<PomodoroCicloCompletadoResponse>> callCiclo = 
                        (Call<RespuestaApi<PomodoroCicloCompletadoResponse>>) api.getApiPomodoroService().cicloCompletado(sesionId, body);
                    callCiclo.enqueue(new Callback<RespuestaApi<PomodoroCicloCompletadoResponse>>() {
                        @Override
                        public void onResponse(Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call, Response<RespuestaApi<PomodoroCicloCompletadoResponse>> response) {
                            handleResponse(response, operationType, endpoint, body, callback);
                        }
                        @Override
                        public void onFailure(Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call, Throwable t) {
                            handleFailure(operationType, endpoint, body, t, callback);
                        }
                    });
                    break;
                case "finalizar":
                    @SuppressWarnings("unchecked")
                    Call<RespuestaApi<PomodoroFinalizarResponse>> callFinalizar = 
                        (Call<RespuestaApi<PomodoroFinalizarResponse>>) api.getApiPomodoroService().finalizar(sesionId, body);
                    callFinalizar.enqueue(new Callback<RespuestaApi<PomodoroFinalizarResponse>>() {
                        @Override
                        public void onResponse(Call<RespuestaApi<PomodoroFinalizarResponse>> call, Response<RespuestaApi<PomodoroFinalizarResponse>> response) {
                            handleResponse(response, operationType, endpoint, body, callback);
                        }
                        @Override
                        public void onFailure(Call<RespuestaApi<PomodoroFinalizarResponse>> call, Throwable t) {
                            handleFailure(operationType, endpoint, body, t, callback);
                        }
                    });
                    break;
                case "cancelar":
                    Call<RespuestaApi<SuccessResponseDto>> callCancelar = api.getApiPomodoroService().cancelar(sesionId);
                    callCancelar.enqueue(new Callback<RespuestaApi<SuccessResponseDto>>() {
                        @Override
                        public void onResponse(Call<RespuestaApi<SuccessResponseDto>> call, Response<RespuestaApi<SuccessResponseDto>> response) {
                            handleResponse(response, operationType, endpoint, body, callback);
                        }
                        @Override
                        public void onFailure(Call<RespuestaApi<SuccessResponseDto>> call, Throwable t) {
                            handleFailure(operationType, endpoint, body, t, callback);
                        }
                    });
                    break;
                default:
                    if (callback != null) callback.onFailure("Unknown operation");
                    return;
            }
        });
    }

    private void handleResponse(Response<?> response, String operationType, String endpoint, Object body, WriteBackCallback callback) {
        if (response.isSuccessful()) {
            writeExecutor.execute(() -> writeBackDao.deleteAll());
            if (callback != null) callback.onSuccess();
        } else {
            queueForRetry(operationType, endpoint, body);
            if (callback != null) callback.onFailure("HTTP " + response.code());
        }
    }

    private void handleFailure(String operationType, String endpoint, Object body, Throwable t, WriteBackCallback callback) {
        queueForRetry(operationType, endpoint, body);
        if (callback != null) callback.onFailure(t.getMessage());
    }

    private int extractSesionId(String endpoint) {
        String[] parts = endpoint.split("/");
        return Integer.parseInt(parts[parts.length - 2]);
    }

    private void queueForRetry(String operationType, String endpoint, Object body) {
        writeExecutor.execute(() -> {
            WriteBackEntity entity = new WriteBackEntity();
            entity.operationType = operationType;
            entity.endpoint = endpoint;
            entity.requestBody = body != null ? new com.google.gson.Gson().toJson(body) : "";
            entity.createdAt = System.currentTimeMillis();
            entity.retryCount = 0;
            entity.status = "pending";
            writeBackDao.insert(entity);
        });
    }

    public void procesarColaWriteBack() {
        writeExecutor.execute(() -> {
            var pending = writeBackDao.getPending();
            for (WriteBackEntity entity : pending) {
                if (entity.retryCount >= 3) {
                    entity.status = "failed";
                    writeBackDao.update(entity);
                    continue;
                }
                reintentarOperacion(entity);
            }
        });
    }

    private void reintentarOperacion(WriteBackEntity entity) {
        int sesionId = Integer.parseInt(entity.endpoint.split("/")[entity.endpoint.split("/").length - 2]);
        switch (entity.operationType) {
            case "ciclo_completado":
                @SuppressWarnings("unchecked")
                Call<RespuestaApi<PomodoroCicloCompletadoResponse>> callCiclo = 
                    (Call<RespuestaApi<PomodoroCicloCompletadoResponse>>) api.getApiPomodoroService().cicloCompletado(sesionId, new com.google.gson.Gson().fromJson(entity.requestBody, Object.class));
                callCiclo.enqueue(new Callback<RespuestaApi<PomodoroCicloCompletadoResponse>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call, Response<RespuestaApi<PomodoroCicloCompletadoResponse>> response) {
                        handleRetryResponse(response, entity);
                    }
                    @Override
                    public void onFailure(Call<RespuestaApi<PomodoroCicloCompletadoResponse>> call, Throwable t) {
                        handleRetryFailure(entity, t);
                    }
                });
                break;
            case "finalizar":
                @SuppressWarnings("unchecked")
                Call<RespuestaApi<PomodoroFinalizarResponse>> callFinalizar = 
                    (Call<RespuestaApi<PomodoroFinalizarResponse>>) api.getApiPomodoroService().finalizar(sesionId, new com.google.gson.Gson().fromJson(entity.requestBody, Object.class));
                callFinalizar.enqueue(new Callback<RespuestaApi<PomodoroFinalizarResponse>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<PomodoroFinalizarResponse>> call, Response<RespuestaApi<PomodoroFinalizarResponse>> response) {
                        handleRetryResponse(response, entity);
                    }
                    @Override
                    public void onFailure(Call<RespuestaApi<PomodoroFinalizarResponse>> call, Throwable t) {
                        handleRetryFailure(entity, t);
                    }
                });
                break;
            case "cancelar":
                Call<RespuestaApi<SuccessResponseDto>> callCancelar = api.getApiPomodoroService().cancelar(sesionId);
                callCancelar.enqueue(new Callback<RespuestaApi<SuccessResponseDto>>() {
                    @Override
                    public void onResponse(Call<RespuestaApi<SuccessResponseDto>> call, Response<RespuestaApi<SuccessResponseDto>> response) {
                        handleRetryResponse(response, entity);
                    }
                    @Override
                    public void onFailure(Call<RespuestaApi<SuccessResponseDto>> call, Throwable t) {
                        handleRetryFailure(entity, t);
                    }
                });
                break;
            default:
                return;
        }
    }

    private void handleRetryResponse(Response<?> response, WriteBackEntity entity) {
        writeExecutor.execute(() -> {
            if (response.isSuccessful()) {
                writeBackDao.delete(entity);
            } else {
                entity.retryCount++;
                writeBackDao.update(entity);
            }
        });
    }

    private void handleRetryFailure(WriteBackEntity entity, Throwable t) {
        writeExecutor.execute(() -> {
            entity.retryCount++;
            writeBackDao.update(entity);
        });
    }

    public interface WriteBackCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
