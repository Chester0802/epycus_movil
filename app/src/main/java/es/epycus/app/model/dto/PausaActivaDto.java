package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PausaActivaDto {
    @SerializedName("ciclosCompletados")
    private int ciclosCompletados;

    public PausaActivaDto(int ciclosCompletados) {
        this.ciclosCompletados = ciclosCompletados;
    }
}
