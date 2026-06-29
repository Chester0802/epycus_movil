package es.epycus.app.api;

import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CrearSubTareaDto;
import es.epycus.app.model.dto.EditarSubTareaDto;
import es.epycus.app.model.dto.SubTareaResponse;
import es.epycus.app.model.dto.SuccessResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiSubTareasService {
    @GET("api/v1/misiones/{misionId}/sub-tareas")
    Call<RespuestaApi<List<SubTareaResponse>>> listar(@Path("misionId") int misionId);

    @GET("api/v1/misiones/{misionId}/sub-tareas/{id}")
    Call<RespuestaApi<SubTareaResponse>> obtener(@Path("misionId") int misionId, @Path("id") int id);

    @POST("api/v1/misiones/{misionId}/sub-tareas")
    Call<RespuestaApi<SuccessResponseDto>> crear(@Path("misionId") int misionId, @Body CrearSubTareaDto body);

    @PUT("api/v1/misiones/{misionId}/sub-tareas/{id}")
    Call<RespuestaApi<SuccessResponseDto>> actualizar(@Path("misionId") int misionId, @Path("id") int id, @Body EditarSubTareaDto body);

    @POST("api/v1/misiones/{misionId}/sub-tareas/{id}/completar")
    Call<RespuestaApi<SuccessResponseDto>> completar(@Path("misionId") int misionId, @Path("id") int id);

    @POST("api/v1/misiones/{misionId}/sub-tareas/{id}/descompletar")
    Call<RespuestaApi<SuccessResponseDto>> descompletar(@Path("misionId") int misionId, @Path("id") int id);

    @DELETE("api/v1/misiones/{misionId}/sub-tareas/{id}")
    Call<RespuestaApi<SuccessResponseDto>> eliminar(@Path("misionId") int misionId, @Path("id") int id);
}