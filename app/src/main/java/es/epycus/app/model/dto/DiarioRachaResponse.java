package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class DiarioRachaResponse {
    @SerializedName("diasConsecutivos")
    private int diasConsecutivos;

    public int getDiasConsecutivos() { return diasConsecutivos; }
}
