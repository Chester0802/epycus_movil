package es.epycus.app.repository;

import android.content.Context;

import java.util.List;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.MisionEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CategoriaDto;
import es.epycus.app.model.dto.CrearSubTareaDto;
import es.epycus.app.model.dto.EditarSubTareaDto;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.MisionCompletarResponse;
import es.epycus.app.model.dto.SubTareaResponse;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

import java.util.List;

public class MisionesRepository {
    private final RetrofitClient api;
    private final CacheManager cacheManager;
    private final AppDatabase database;

    public MisionesRepository(Context context) {
        this.api = RetrofitClient.getInstance(context);
        this.cacheManager = CacheManager.getInstance(context);
        this.database = AppDatabase.getInstance(context);
    }

    public Call<RespuestaApi<List<MisionDto>>> listar() {
        return api.getApiMisionesService().listar();
    }

    public Call<RespuestaApi<MisionCompletarResponse>> completar(int id) {
        return api.getApiMisionesService().completar(id);
    }

    public Call<RespuestaApi<SuccessResponseDto>> crear(Object body) {
        return api.getApiMisionesService().crear(body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> actualizar(int id, Object body) {
        return api.getApiMisionesService().actualizar(id, body);
    }

    public Call<RespuestaApi<SuccessResponseDto>> eliminar(int id) {
        return api.getApiMisionesService().eliminar(id);
    }

    public Call<RespuestaApi<List<CategoriaDto>>> categorias() {
        return api.getApiMisionesService().categorias();
    }

    public Call<RespuestaApi<List<SubTareaResponse>>> listarSubTareas(int misionId) {
        return api.getApiSubTareasService().listar(misionId);
    }

    public Call<RespuestaApi<SubTareaResponse>> obtenerSubTarea(int misionId, int id) {
        return api.getApiSubTareasService().obtener(misionId, id);
    }

    public Call<RespuestaApi<SuccessResponseDto>> crearSubTarea(int misionId, CrearSubTareaDto dto) {
        return api.getApiSubTareasService().crear(misionId, dto);
    }

    public Call<RespuestaApi<SuccessResponseDto>> actualizarSubTarea(int misionId, int id, EditarSubTareaDto dto) {
        return api.getApiSubTareasService().actualizar(misionId, id, dto);
    }

    public Call<RespuestaApi<SuccessResponseDto>> completarSubTarea(int misionId, int id) {
        return api.getApiSubTareasService().completar(misionId, id);
    }

    public Call<RespuestaApi<SuccessResponseDto>> descompletarSubTarea(int misionId, int id) {
        return api.getApiSubTareasService().descompletar(misionId, id);
    }

    public Call<RespuestaApi<SuccessResponseDto>> eliminarSubTarea(int misionId, int id) {
        return api.getApiSubTareasService().eliminar(misionId, id);
    }

    public void cacheJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public void getCachedJsonAsync(String key, CacheManager.CacheCallback callback) {
        cacheManager.getAsync(key, callback);
    }

    public void cacheMisiones(List<MisionEntity> misiones) {
        database.misionDao().deleteAll();
        database.misionDao().insertAll(misiones);
    }

    public List<MisionEntity> getCachedMisiones() {
        return database.misionDao().getAll();
    }

    public MisionEntity toEntity(MisionDto dto) {
        return new MisionEntity(
                dto.getId(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getNombreCurso(),
                dto.getPrioridad(),
                dto.getEstado(),
                dto.getFechaLimite(),
                dto.getXpOtorgado(),
                dto.getFechaCreacion(),
                dto.getCategoriaId()
        );
    }

    public MisionDto toDto(MisionEntity entity) {
        MisionDto dto = new MisionDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setNombreCurso(entity.getNombreCurso());
        dto.setPrioridad(entity.getPrioridad());
        dto.setEstado(entity.getEstado());
        dto.setFechaLimite(entity.getFechaLimite());
        dto.setXpOtorgado(entity.getXpOtorgado());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setCategoriaId(entity.getCategoriaId());
        return dto;
    }
}
