package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroSesionActivaResponse {
    @SerializedName("activa")
    private boolean activa;

    @SerializedName("sesionId")
    private Integer sesionId;

    @SerializedName("fechaInicio")
    private String fechaInicio;

    @SerializedName("ciclosCompletados")
    private Integer ciclosCompletados;

    public boolean isActiva() { return activa; }
    public Integer getSesionId() { return sesionId; }
    public String getFechaInicio() { return fechaInicio; }
    public Integer getCiclosCompletados() { return ciclosCompletados; }
}
