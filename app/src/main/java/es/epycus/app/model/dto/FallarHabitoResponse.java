package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class FallarHabitoResponse {
    @SerializedName("rachaRota")
    private boolean rachaRota;

    public boolean isRachaRota() { return rachaRota; }
}
