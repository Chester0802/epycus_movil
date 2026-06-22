package es.epycus.app.repository;

import android.content.Context;

import java.util.List;

import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.MisionEntity;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CategoriaDto;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.MisionCompletarResponse;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.util.CacheManager;
import retrofit2.Call;

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

    public Call<RespuestaApi<SuccessResponseDto>> eliminar(int id) {
        return api.getApiMisionesService().eliminar(id);
    }

    public Call<RespuestaApi<List<CategoriaDto>>> categorias() {
        return api.getApiMisionesService().categorias();
    }

    public void cacheJson(String key, String json, long ttlSeconds) {
        cacheManager.put(key, json, ttlSeconds);
    }

    public String getCachedJson(String key) {
        return cacheManager.get(key);
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
