package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroIniciarResponse {
    @SerializedName("sesionId")
    private int sesionId;

    @SerializedName("fechaInicio")
    private String fechaInicio;

    public int getSesionId() { return sesionId; }
    public String getFechaInicio() { return fechaInicio; }
}
