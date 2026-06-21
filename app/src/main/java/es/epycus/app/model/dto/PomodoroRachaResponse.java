package es.epycus.app.model.dto;

import com.google.gson.annotations.SerializedName;

public class PomodoroRachaResponse {
    @SerializedName("racha")
    private int racha;

    public int getRacha() { return racha; }
}
